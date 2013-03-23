package server.games;

import server.games.cards.CardList;
import server.games.cards.ServerCardInstance;
import server.network.ClientAccount;
import Shared.ClientMessages;
import Shared.ClientResponses;
import Shared.GameResources;
import Shared.GameZones;

public class GamePlayer {
	// GAME CONSTANTS
		public enum PlayerStates { JOINED, 		// Player joined, needs to submit decklist.
									READY, 			// Decks submited, waiting for other players.
									ACTIVE, 		// Waiting for this player to do things.
									WAITING, 		// This player is waiting for things to happen.
									DONE,			// Player is ready to let stack resolve.
									DISCONNECTED, 	// Disconnected, do not send messages pl0x.
									READING,
									DEAD; }			// This guy is dead, what a scrub.
		
	// Configuration variables
		private GameInstance m_Game;
		private ClientAccount m_ClientAccount;
		
	// GamePlayer variables
		private CardList m_Deck;
		private CardList m_Hand = new CardList( this, GameZones.HAND );
		private CardList m_Scrapyard = new CardList( this, GameZones.GRAVEYARD );
		
	// GameFlow variables
		private PlayerStates m_PlayerState;
	

	//Constructors and administrative methods
	public GamePlayer( GameInstance g, ClientAccount acc ) {
		m_Game = g;
		m_ClientAccount = acc;
		SendMessageFromGame( ClientMessages.JOIN );
		ChangeState(PlayerStates.JOINED);
	}
	
	//Data getters
	public ClientAccount getClientAccount() { return m_ClientAccount; }
	public PlayerStates getState() { return m_PlayerState; }
	
	//Game play methods
	public void DrawCards( int number ) {
		for( int i = 0; i < number; i++ ) {
			ServerCardInstance card = m_Deck.GetTopCard();
			removeCardFromZone( card, GameZones.DECK );
			putCardInZone( card, GameZones.HAND );
		}
	}
	public void AddResource( GameResources res, int value ) {
		System.out.println("ADDING " + res.name() + "x" + value );
		// TODO add resources to pool.
	}
	public void LoadDeck( String decklist ) {
	    if( m_PlayerState == PlayerStates.JOINED ) {
            CardList newDeck = new CardList( this, GameZones.DECK );
            
            String[] cards = decklist.split("\\|");
            for( String s : cards ) {
                    String[] values = s.split(",");
                    for( int i = 0; i < Integer.valueOf(values[1]); i++ ) {
                            ServerCardInstance toAdd = new ServerCardInstance( m_Game, this, Integer.valueOf(values[0]) );
                            newDeck.Add(toAdd);
                    }
            }
            
            newDeck.Shuffle();
            
            if( newDeck.Validate() ) {
                    m_Deck = newDeck;
                    ChangeState(PlayerStates.READY);
                    m_Game.Ready();
            } else {
                   	SendMessageFromGame( 	ClientMessages.GAME_ERROR, 
                                    		String.valueOf( m_Game.GetGameID() ), 
                                    		ClientResponses.DECKLIST.name(), 
                   							"Invalid deck list." );
           }
	    }
    }
	
	//Player card management
	public void putCardInZone( ServerCardInstance card, GameZones zone ) {
		switch( zone ) {
		case DECK:
			m_Deck.Add( card );
			//m_Game.SendMessageToAllPlayers( ClientMessages.HIDE, String.valueOf( card.GetCardUID() ), GameZones.GRAVEYARD.name() );
			updateGameZoneCount( zone );
			break;
		case HAND:
			m_Hand.Add( card );
			//m_Game.SendMessageToAllPlayers( ClientMessages.HIDE, String.valueOf( card.GetCardUID() ), GameZones.GRAVEYARD.name() );
			SendMessageFromGame( ClientMessages.MOVE, String.valueOf( card.GetCardUID() ), GameZones.HAND.name() );
			updateGameZoneCount( zone );
			break;
		case GRAVEYARD:
			m_Scrapyard.Add( card );
			m_Game.SendMessageToAllPlayers( ClientMessages.MOVE, String.valueOf( card.GetCardUID() ), GameZones.GRAVEYARD.name() );
			updateGameZoneCount( zone );
			break;
		default:
			break;
	}
	}
	public void removeCardFromZone( ServerCardInstance card, GameZones zone ) {
		switch( zone ) {
			case DECK:
				m_Deck.Remove( card );
				updateGameZoneCount( zone );
				break;
			case HAND:
				m_Hand.Remove( card );
				updateGameZoneCount( zone );
				break;
			case GRAVEYARD:
				m_Scrapyard.Remove( card );
				updateGameZoneCount( zone );
				break;
			default:
				break;
		}
	}
	public boolean isCardInZone( ServerCardInstance card, GameZones zone ) {
		switch( zone ) {
		case DECK:
			return m_Deck.Contains( card );
		case HAND:
			return m_Hand.Contains( card );
		case GRAVEYARD:
			return m_Scrapyard.Contains( card );
		default:
			return false;
		}
	}
	public void updateGameZoneCount( GameZones zone ) {
		int count = 0;
		switch( zone ) {
			case DECK:
				count = m_Deck.Count();
				break;
			case HAND:
				count = m_Hand.Count();
				break;
			case GRAVEYARD:
				count = m_Scrapyard.Count();
				break;
			default:
				break;
		}
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_ZONE, String.valueOf( m_ClientAccount.getUserID() ), zone.name(), String.valueOf( count ) );
	}
	
	//Game flow methods
	public boolean PlayCard( ServerCardInstance card ) {
		if( m_PlayerState == PlayerStates.ACTIVE && isCardInZone( card, GameZones.HAND ) ) {
			// TODO Check for cost/can play
			
			removeCardFromZone( card, GameZones.HAND );
			m_Game.PutCardOnStack( card );
			m_Game.StartStacking();
			
			return true;
		} else {
			SendMessageFromGame( ClientMessages.GAME_ERROR, "Cannot play card " + String.valueOf( card.GetCardUID() ) + " at this time.");
			return false;
		}
	}
	public void StartTurn() {
		// A player draws a card on their turn.
		DrawCards(1);
		
		// Let all players know that this player's turn has started.
		m_Game.SendMessageToAllPlayers( ClientMessages.PLAYER_TURN, String.valueOf( m_ClientAccount.getUserID() ));
		
		setActive();
	}
	public void setActive() {
		// Set the player state to active.
		ChangeState(PlayerStates.ACTIVE);
	}
	public void setWaiting() {
		ChangeState(PlayerStates.WAITING);
	}
	public void setDone() {
		ChangeState(PlayerStates.DONE);
	}
	public void setReading() {
		ChangeState(PlayerStates.READING);
	}
	
	public void SendMessageFromGame( ClientMessages messageType, String...args ) {
		String argString = String.valueOf(m_Game.GetGameID()) + ";";
		for( int i = 0; i < args.length; i++ ) {
			argString += args[i] + ";";
		}
		m_ClientAccount.SendMessage( messageType, argString );
	}
	
	private void ChangeState( PlayerStates newState ) {
		m_PlayerState = newState;
		SendMessageFromGame( ClientMessages.PLAYERSTATE, m_PlayerState.name() );
	}

	
}
