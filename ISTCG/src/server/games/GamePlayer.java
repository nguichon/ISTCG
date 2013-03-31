package server.games;

import server.games.cards.CardList;
import server.games.cards.ServerCardInstance;
import server.games.cards.ServerCardTemplate;
import server.games.cards.abilities.Target;
import server.network.ClientAccount;
import Shared.CardTypes;
import Shared.ClientMessages;
import Shared.ClientResponses;
import Shared.GameResources;
import Shared.GameZones;
import Shared.StatBlock;
import Shared.StatBlock.StatType;

public class GamePlayer {
	// GAME CONSTANTS
		public enum PlayerStates { JOINED, 		// Player joined, needs to submit decklist.
									READY, 			// Decks submited, waiting for other players.
									ACTIVE, 		// Waiting for this player to do things.
									WAITING, 		// This player is waiting for things to happen.
									DONE,			// Player is ready to let stack resolve.
									DISCONNECTED, 	// Disconnected, do not send messages pl0x.
									READING,
									DEAD; }			// This guy is dead, what a scrub like that Magnus.
		
	// Configuration variables
		private GameInstance m_Game;
		private ClientAccount m_ClientAccount;
		
	// GamePlayer variables
		private CardList m_Deck;
		private CardList m_Hand = new CardList( this, GameZones.HAND );
		private CardList m_Scrapyard = new CardList( this, GameZones.GRAVEYARD );
		private CardList m_Delayed = new CardList( this, GameZones.DELAYED );
		private ServerCardInstance m_CommandUnit;
		private int[] m_Resources = new int[GameResources.values().length];
		
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
	public void onGameStart() {
        m_CommandUnit.SetLocation( GameZones.FIELD );
		m_Game.SendMessageToAllPlayers( ClientMessages.MOVE, String.valueOf( m_CommandUnit.GetCardUID() ), GameZones.FIELD.name() );
		updateGameZoneCount( GameZones.DECK );
		updateGameZoneCount( GameZones.HAND );
		updateGameZoneCount( GameZones.GRAVEYARD );
	}
	public void DrawCards( int number ) {
		for( int i = 0; i < number; i++ ) {
			ServerCardInstance card = m_Deck.GetTopCard();
			removeCardFromZone( card, GameZones.DECK );
			putCardInZone( card, GameZones.HAND );
		}
	}
	public void AddResource( GameResources res, int value ) {
		m_Resources[ res.ordinal() ] += value;
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_PLAYER, 
										String.valueOf( m_ClientAccount.getUserID() ), 
										res.name(), 
										String.valueOf( m_Resources[ res.ordinal() ] ) );
	}
	public void LoadDeck( int commandUnit, String decklist ) {
	    if( m_PlayerState == PlayerStates.JOINED ) {
            CardList newDeck = new CardList( this, GameZones.DECK );
            m_CommandUnit = new ServerCardInstance( m_Game, this, commandUnit );
            
            String[] cards = decklist.split("\\|");
            for( String s : cards ) {
                    String[] values = s.split(",");
                    for( int i = 0; i < Integer.valueOf(values[1]); i++ ) {
                            ServerCardInstance toAdd = new ServerCardInstance( m_Game, this, Integer.valueOf(values[0]) );
                            newDeck.Add(toAdd);
                    }
            }
            
            newDeck.Shuffle();
            
            if( m_CommandUnit.GetCardTemplate().getCardType() == CardTypes.COMMAND_UNIT && newDeck.Validate() ) {
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
			updateGameZoneCount( zone );
			break;
		case HAND:
			m_Hand.Add( card );
			SendMessageFromGame( ClientMessages.MOVE, String.valueOf( card.GetCardUID() ), GameZones.HAND.name() );
			updateGameZoneCount( zone );
			break;
		case GRAVEYARD:
			m_Scrapyard.Add( card );
			m_Game.SendMessageToAllPlayers( ClientMessages.MOVE, String.valueOf( card.GetCardUID() ), GameZones.GRAVEYARD.name() );
			updateGameZoneCount( zone );
			break;
		case DELAYED:
			m_Delayed.Add( card );
			m_Game.SendMessageToAllPlayers( ClientMessages.MOVE, String.valueOf( card.GetCardUID() ), GameZones.DELAYED.name() );
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
			case DELAYED:
				m_Delayed.Remove( card );
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
	public boolean PlayCard( ServerCardInstance card, String targets ) {
		if( m_PlayerState == PlayerStates.ACTIVE && isCardInZone( card, GameZones.HAND ) ) {
			//Check targets
			String[] trgts = targets.split("|");
			card.clearTargets();
			for( String target : trgts ) {
				card.addTarget( new Target( m_Game.GetCardInstance(Integer.valueOf(target)) ) );
			}
			if( !card.ValidateTargets() ) {
				SendMessageFromGame( ClientMessages.GAME_ERROR, "Invalid/Incorrect Number of targets.");
				return false;
			}

			//Check for costs
			ServerCardTemplate sct = card.GetCardTemplate();
			StatBlock m = sct.getStat( StatType.METAL );
			if( m != null && m.m_Value != -1 && m.m_Value > m_Resources[GameResources.METAL.ordinal()] ) {
				SendMessageFromGame( ClientMessages.GAME_ERROR, "Not enough Metal to play " + String.valueOf( card.GetCardUID() ) + " at this time.");
				return false;
			}
			StatBlock e = sct.getStat( StatType.ENERGY );
			if(e != null && e.m_Value != -1 && e.m_Value >m_Resources[GameResources.ENERGY.ordinal()] ) {
				SendMessageFromGame( ClientMessages.GAME_ERROR, "Not enough Energy to play " + String.valueOf( card.GetCardUID() ) + " at this time.");
				return false;
			}
			StatBlock t = sct.getStat( StatType.TECH );
			if(t != null && t.m_Value != -1 && t.m_Value >m_Resources[GameResources.TECH.ordinal()] ) {
				SendMessageFromGame( ClientMessages.GAME_ERROR, "Not enough Tech points to play " + String.valueOf( card.GetCardUID() ) + " at this time.");
				return false;
			}
			
			//Pay costs
			if( m != null && m.m_Value != -1 ) m_Resources[GameResources.METAL.ordinal()] -= m.m_Value;
			if( e != null && e.m_Value != -1 ) m_Resources[GameResources.METAL.ordinal()] -= e.m_Value;
			if( t != null && t.m_Value != -1 ) m_Resources[GameResources.METAL.ordinal()] -= t.m_Value;
			
			//Put onto stack
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
		m_Resources[ GameResources.METAL.ordinal() ] = 0;
		m_Resources[ GameResources.ENERGY.ordinal() ] = 0;
		m_Resources[ GameResources.TECH.ordinal() ] = 0;
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_PLAYER, 
				String.valueOf( m_ClientAccount.getUserID() ), 
				GameResources.METAL.name(), 
				String.valueOf( m_Resources[ GameResources.METAL.ordinal() ] ) );
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_PLAYER, 
				String.valueOf( m_ClientAccount.getUserID() ), 
				GameResources.ENERGY.name(), 
				String.valueOf( m_Resources[ GameResources.ENERGY.ordinal() ] ) );
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_PLAYER, 
				String.valueOf( m_ClientAccount.getUserID() ), 
				GameResources.TECH.name(), 
				String.valueOf( m_Resources[ GameResources.TECH.ordinal() ] ) );
		DrawCards( 1 );
		m_Game.SendMessageToAllPlayers( ClientMessages.PLAYER_TURN, String.valueOf( m_ClientAccount.getUserID() ));
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
		SendMessageFromGame( ClientMessages.PLAYERSTATE, String.valueOf(m_ClientAccount.getUserID()), m_PlayerState.name() );
	}

	
}
