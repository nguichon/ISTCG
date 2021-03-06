package server.games;

import server.games.cards.CardList;
import server.games.cards.ServerCardInstance;
import server.games.cards.ServerCardTemplate;
import server.games.cards.abilities.Target;
import server.games.events.ResolutionEvent;
import server.network.ClientAccount;
import NewClient.ClientCardTemplateManager;
import Shared.CardTypes;
import Shared.ClientMessages;
import Shared.ClientResponses;
import Shared.GameResources;
import Shared.GameZones;
import Shared.PlayerStates;
import Shared.StatBlock;
import Shared.StatBlock.StatType;

public class GamePlayer {
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
		m_Game.GameMessage( String.format("%s draws %d cards.", 
				m_ClientAccount.getUserName(),
				number) );
		for( int i = 0; i < number; i++ ) {
			ServerCardInstance card = m_Deck.GetTopCard();
			removeCardFromZone( card, GameZones.DECK );
			putCardInZone( card, GameZones.HAND );
		}
	}
	public void DiscardHand( ) {
		m_Game.GameMessage( String.format("%s discards his/her hand.", 
				m_ClientAccount.getUserName()) );
		while( m_Hand.Count() > 0 ) {
			ServerCardInstance card = m_Hand.GetTopCard();
			removeCardFromZone( card, GameZones.HAND );
			putCardInZone( card, GameZones.GRAVEYARD );
		}
	}
	public void AddResource( GameResources res, int value ) {
		m_Game.GameMessage( String.format("%s adds %d %s to his/her resource pool.", 
				m_ClientAccount.getUserName(),
				value,
				res.name()));
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
            
            if(  newDeck.Validate() ) {
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
			if( targets != null ) {
				String[] trgts = targets.split("|");
				card.clearTargets();
				for( String target : trgts ) {
					card.addTarget( new Target( m_Game.GetCardInstance(Integer.valueOf(target)) ) );
				}
				if( !card.ValidateTargets() ) {
					SendMessageFromGame( ClientMessages.GAME_ERROR, "Invalid/Incorrect Number of targets.");
					return false;
				}
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
			if( e != null && e.m_Value != -1 ) m_Resources[GameResources.ENERGY.ordinal()] -= e.m_Value;
			if( t != null && t.m_Value != -1 ) m_Resources[GameResources.TECH.ordinal()] -= t.m_Value;
			
			for( GameResources res : GameResources.values() ) {
				m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_PLAYER, 
											String.valueOf( m_ClientAccount.getUserID() ), 
											res.name(), 
											String.valueOf( m_Resources[ res.ordinal() ] ) );
			}
			
			//Put onto stack
			//m_Game.GameMessage( String.format( "%s played %s.", m_ClientAccount.getUserName(), ClientCardTemplateManager.get().GetClientCardTemplate( card.GetCardTemplate().getCardTemplateID() ).getCardName() ) );
			removeCardFromZone( card, GameZones.HAND );
			
			if( card.GetCardTemplate().getCardType() == CardTypes.RESOURCE ) {
				ResolutionEvent re = new ResolutionEvent( m_Game );
				card.Resolve( re );
				return true;
			}
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
	
	private int m_MessagesSent = 0;
	public void SendMessageFromGame( ClientMessages messageType, String...args ) {
		String argString = String.valueOf(m_Game.GetGameID()) + ";" + m_MessagesSent++ + ";";
		for( int i = 0; i < args.length; i++ ) {
			argString += args[i] + ";";
		}
		m_ClientAccount.SendMessage( messageType, argString );
	}
	
	private void ChangeState( PlayerStates newState ) {
		m_PlayerState = newState;
		SendMessageFromGame( ClientMessages.PLAYER_STATE, String.valueOf(m_ClientAccount.getUserID()), m_PlayerState.name() );
	}

	public void setDead() {
		ChangeState(PlayerStates.DEAD);
	}

	
}
