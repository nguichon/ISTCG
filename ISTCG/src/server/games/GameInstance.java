package server.games;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import NewClient.ClientCardTemplateManager;
import Shared.ClientMessages;
import Shared.ClientResponses;
import Shared.GameZones;

import server.Database;
import server.ServerMain;
import server.games.cards.ServerCardInstance;
import server.games.events.ResolutionEvent;
import server.games.stack.Attack;
import server.games.stack.StackObject;
import server.network.ClientAccount;
import Shared.GameStates;

import Shared.PlayerStates;

/**
 * This is a specific game. I'll write more documentation later...
 * @author Nicholas Guichon
 */
public class GameInstance {
	//GAME CONSTANTS
	private static final int STARTING_HAND_SIZE = 7;
	
	//GameInstance Variables
	private final int m_GameInstanceID;
	
	//GameFlow Variables
	private GameStates m_GameInstanceState = GameStates.CREATED;	// Current states of the game.
	
	//Player Variables
	private HashMap< Integer, GamePlayer > m_Players;				// Collection of players in this GameInstance, stored by User_ID
	private ArrayList< Integer > m_PlayerList;						// List of players' User_IDs in turn order.
	
	//Game turn variables
	private GamePlayer m_CurrentPlayer;								// GamePlayer link to the current player.
	private int m_CurrentPlayerIndex;								// Index of m_PlayerList for the current player.
	private int m_ActivePlayerIndex;								// Index of m_PlayerList for the current player.
	
	//Game object variables
	private HashMap< Integer, ServerCardInstance > m_Directory = new HashMap< Integer, ServerCardInstance >();
	private Stack< StackObject > m_ObjectsOnStack = new Stack< StackObject >();
	private HashMap< Integer, ServerCardInstance > m_CardsOnField = new HashMap< Integer, ServerCardInstance >();
	private int m_UniqueIDsCreated = 0;
	
	
	/**
	 * Gets a number to use for a unique card identifier.
	 * 
	 * @return
	 * 		An integer, unique to this instance of Game
	 */
	public synchronized int CreateNewUniqueID() {
		return m_UniqueIDsCreated++;
	}
	
	/**
	 * Adds a card to this GameInstance's list of cards.
	 * @param toAdd
	 * 		The card to add to the instance.
	 */
	public void AddToDirectory( ServerCardInstance toAdd ) {
		m_Directory.put( toAdd.GetCardUID(), toAdd );
	}
	
	/**
	 * This creates a game instance, should ONLY be called by GameManager
	 * 
	 * @param id 
	 * 		The specific id of this game, passed in by GameManager.
	 * @param players 
	 * 		List of players that will be in this game.
	 */
	public GameInstance(int id, ClientAccount[] players) {
		m_GameInstanceID = id;
		m_Players = new HashMap< Integer, GamePlayer >();
		m_PlayerList = new ArrayList< Integer >();
		for(ClientAccount ca : players) { AddToGame( ca ); }
	}
	
	/**
	 * Adds a new player to the game.
	 * 
	 * @param ca 
	 * 		The ClientAccount/Socket connection for this new player
	 */
	private void AddToGame( ClientAccount clientToAdd ) {
		if( m_GameInstanceState == GameStates.CREATED && !m_Players.containsKey( clientToAdd.getUserID() ) ) {
			Database.get().quickInsert( "INSERT INTO game_players(id, created_at, modified_at, game_id, user_id) VALUES (DEFAULT,DEFAULT,DEFAULT," + m_GameInstanceID + "," + clientToAdd.getUserID() + ");");
			GamePlayer gp = new GamePlayer( this, clientToAdd );
			SendMessageToAllPlayers( ClientMessages.PLAYER_JOINED, gp.getClientAccount().getUserName(), String.valueOf(gp.getClientAccount().getUserID()) );
			
			for( Integer i : m_PlayerList ) { 
				GamePlayer player = m_Players.get(i);
				gp.SendMessageFromGame( ClientMessages.PLAYER_JOINED, player.getClientAccount().getUserName(), String.valueOf(player.getClientAccount().getUserID()) ); 
			}
			
			m_Players.put( clientToAdd.getUserID(), gp );
			m_PlayerList.add( clientToAdd.getUserID() );
		}
	}

	/**
	 * Handles a message being sent. ClientAccounts will send certain messages here
	 * 
	 * @param origin
	 * 		The player_id of the ClientAccount that this message originates from.
	 * @param message
	 * 		The actual message sent by the ClientAccount, split on ';'
	 */
	public synchronized void HandleMessage( ClientAccount origin, String[] message ) throws IndexOutOfBoundsException {
		try {
			switch(ClientResponses.valueOf( message[0].toUpperCase() )) {
				case END:
					if( m_GameInstanceState == GameStates.ACTIVE && origin.getUserID() == m_CurrentPlayer.getClientAccount().getUserID() ) {
						EndTurn();
					}
					break;
				case CANCEL:
					if( m_GameInstanceState == GameStates.CREATED ) {
						//TODO Cancel the game
					} else {
						origin.SendMessage( ClientMessages.SERVER, "Game " + m_GameInstanceID + " has already started and cannot be canceled.");
					}
					break;
				case DECKLIST:
					int COMMAND_UNIT = Integer.valueOf(message[2]);
					String DECK_LIST = message[3];
					if( m_GameInstanceState == GameStates.CREATED ) {
						m_Players.get( origin.getUserID() ).LoadDeck( COMMAND_UNIT, DECK_LIST );
					} else {
						origin.SendMessage( ClientMessages.SERVER, "Game " + m_GameInstanceID + " has already started, you cannot submit another decklist.");
					}
					break;
				case GETCARDINFO:
					int CARD_ID = Integer.valueOf(message[2]);
					ServerCardInstance targetCard = m_Directory.get( CARD_ID );
					if( !(targetCard == null) ) {
						targetCard.SendCardInformation( m_Players.get( origin.getUserID() ) );
					} else {
						origin.SendMessage( ClientMessages.SERVER, "CardInstance " + CARD_ID + " does not exist.");
					}
					break;
				case PASS:
					switch( m_GameInstanceState ){
					case STACKING:
						if( origin.getUserID() == m_PlayerList.get( m_ActivePlayerIndex ) ) {
							PassPriority();
						}
						break;
					case RESOLVING:
						m_Players.get( origin.getUserID() ).setWaiting();
						try {
							Thread.sleep( 100 );
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						CheckForResolution();
						break;
					default:
						break;
					}
					break;
				case ATTACK:
					ServerCardInstance ATTACKER = m_Directory.get( Integer.valueOf( message[2] ));
					ServerCardInstance DEFENDER = m_Directory.get( Integer.valueOf( message[3] ));
					switch( m_GameInstanceState ) {
					case ACTIVE:
						if( !(m_Players.get( origin.getUserID() ).getState() == PlayerStates.ACTIVE) ) {
							m_Players.get( origin.getUserID() ).SendMessageFromGame( ClientMessages.GAME_ERROR, String.valueOf( ATTACKER.GetCardUID() ) + " cannot attack at this time, not your turn.");
							break;
						}
						if( !( ATTACKER.getController() != DEFENDER.getController() ) ) {
							m_Players.get( origin.getUserID() ).SendMessageFromGame( ClientMessages.GAME_ERROR, String.valueOf( ATTACKER.GetCardUID() ) + " cannot attack your own ship.");
							break;
						}
						if( !ATTACKER.getActive() ) {
							m_Players.get( origin.getUserID() ).SendMessageFromGame( ClientMessages.GAME_ERROR, String.valueOf( ATTACKER.GetCardUID() ) + " cannot attack, because it already has.");
							break;
						}
						
						ATTACKER.ChangeState( false );
						PutAttackOnStack( new Attack( this, ATTACKER, DEFENDER ) );
						StartStacking();
						break;
					default:
						m_Players.get( origin.getUserID() ).SendMessageFromGame( ClientMessages.GAME_ERROR, String.valueOf( ATTACKER.GetCardUID() ) + " cannot attack at this time.");
						break;
					}
					break;
				case PLAY:
					ServerCardInstance CARD_TO_PLAY = m_Directory.get( Integer.valueOf(message[2]) );
					String TARGETING_SOLUTION = null;
					if( message.length == 4 ) { TARGETING_SOLUTION = message[3]; }
					switch( m_GameInstanceState ) {
					case ACTIVE:
						if( m_Players.get( origin.getUserID() ).getState() == PlayerStates.ACTIVE ) {
							m_Players.get( origin.getUserID() ).PlayCard( CARD_TO_PLAY, TARGETING_SOLUTION );
						}
						break;
					case STACKING:
						if( m_Players.get( origin.getUserID() ).getState() == PlayerStates.ACTIVE && CARD_TO_PLAY.GetCardTemplate().isFast() ) {
							m_Players.get( origin.getUserID() ).PlayCard( CARD_TO_PLAY, TARGETING_SOLUTION );
							/*for( Integer p : m_PlayerList ) {
								if( p != origin.getUserID() ) { m_Players.get( p ).setWaiting(); }
							}*/
						}
						break;
					default:
						m_Players.get( origin.getUserID() ).SendMessageFromGame( ClientMessages.GAME_ERROR, "Cannot play card " + String.valueOf( CARD_TO_PLAY.GetCardUID() ) + " at this time.");
						break;
					}
					break;
				default:
					ServerMain.ConsoleMessage( '?', "Reached default block in method \"GameInstance.HandleMessage\"." );
					break;
			}
		} catch ( RuntimeException e ) {
			origin.SendMessage( ClientMessages.SERVER, "Unknown error caught. " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	/**
	 * Iterates through the list of GamePlayers, sending the same message to each of them
	 * @param message
	 * 		Message that will be sent to users
	 */
	public void SendMessageToAllPlayers( ClientMessages messageType, String ... parameters ) {
		for( Integer i : m_PlayerList ) { 
			GamePlayer player = m_Players.get(i);
			player.SendMessageFromGame( messageType, parameters ); 
		}
	}
	
	/**
	 * Gets the id of this game, as determined by its constructor.
	 * @return
	 * 		The database ID number of this game, this ID number should be unique unless constructed illegally.
	 */
	public int GetGameID() {
		return m_GameInstanceID;
	}
	
	/**
	 * Starts this game, drawing each player's initial hand and picking a random starting player.
	 */
	private void StartGame() {
		if( m_GameInstanceState == GameStates.CREATED ) {
			ChangeState(GameStates.STARTING);
			
			for( Integer i : m_PlayerList ) { 
				GamePlayer player = m_Players.get(i);
				player.DrawCards( STARTING_HAND_SIZE );
				player.onGameStart();
			}
			
			m_CurrentPlayerIndex = new Random().nextInt( m_Players.size() );
			m_CurrentPlayer = m_Players.get( m_PlayerList.get( m_CurrentPlayerIndex ) );
			
			StartTurn();
		}
	}
	
	/**
	 * Starts the current turn.
	 */
	private void StartTurn() {
		m_CurrentPlayer.StartTurn();
		SetActivePlayer();
		//Set game state.
		ChangeState(GameStates.ACTIVE);
		for( ServerCardInstance sci : m_Directory.values() ) {
			if( sci.getController() == m_CurrentPlayer ) {
				sci.ChangeState( true );
			}
		}
	}
	
	private void SetActivePlayer() {
		for( Integer i : m_PlayerList ) { 
			if( i != m_CurrentPlayer.getClientAccount().getUserID() ) {
				m_Players.get(i).setWaiting();
			}
		}
		m_ActivePlayerIndex = m_CurrentPlayerIndex;
		m_CurrentPlayer.setActive();
	}
	
	public void SetAllPlayersToWaiting() {
		for( Integer i : m_PlayerList ) { 
			m_Players.get(i).setWaiting();
		}
	}
	public void SetAllPlayersToReading() {
		for( Integer i : m_PlayerList ) { 
			m_Players.get(i).setReading();
		}
	}
	public void PassPriority() {
		GamePlayer gp = m_Players.get(m_PlayerList.get( m_ActivePlayerIndex ));
		gp.setDone();

		boolean start_resolving = true;
		for( Integer id : m_PlayerList ) {
			if( m_Players.get(id).getState() != PlayerStates.DONE ) {
				start_resolving = false;
			}
		}
		
		if( start_resolving ) {
			StartResolving();
		} else {
			if( ++m_ActivePlayerIndex >= m_Players.size() ) { m_ActivePlayerIndex = 0; }
			gp = m_Players.get(m_PlayerList.get( m_ActivePlayerIndex ));
			
			if( gp.getState() == PlayerStates.WAITING ) {
				gp = m_Players.get(m_PlayerList.get( m_ActivePlayerIndex ));
				gp.setActive();
			} else {
				PassPriority();
			}
		}
	}

	public void StartResolving() {
		ChangeState(GameStates.RESOLVING);
		SetAllPlayersToReading();
	}
	public void CheckForResolution() {
		boolean resolve = true;
		for( Integer id : m_PlayerList ) {
			if( m_Players.get(id).getState() != PlayerStates.WAITING ) {
				resolve = false;
			}
		}
		
		if( resolve ) {
			ResolveStackObject( GetObjectFromStack() );
			SetAllPlayersToReading();
			if( m_ObjectsOnStack.isEmpty() ) {
				ChangeState(GameStates.ACTIVE);
				SetActivePlayer();
			}
		}
	}
	
	private void ResolveStackObject( StackObject so ) {
		ResolutionEvent e = new ResolutionEvent( this );
		so.Resolve( e );
	}

	public StackObject GetObjectFromStack() {
		StackObject so = m_ObjectsOnStack.pop();
		if( ServerCardInstance.class.isInstance( so )) {
			((ServerCardInstance)so).SetLocation( GameZones.UNKNOWN );
			SendMessageToAllPlayers( ClientMessages.HIDE, String.valueOf( ((ServerCardInstance)so).GetCardUID() ), GameZones.UNKNOWN.name() );
		} else {
			SendMessageToAllPlayers( ClientMessages.REMOVE_STACK_OBJECT, String.valueOf( so.getStackObjectID() ) );
		}
		return so;
	}
	public void PutCardOnStack( ServerCardInstance card ) {
		card.SetLocation( GameZones.STACK );
		m_ObjectsOnStack.push( card );
		SendMessageToAllPlayers( ClientMessages.MOVE, String.valueOf( card.GetCardUID() ), GameZones.STACK.name() );
	}
	public void PutAttackOnStack( Attack attack ) {
		m_ObjectsOnStack.push( attack );
		SendMessageToAllPlayers( ClientMessages.STACK_OBJECT, 
								 String.valueOf(attack.getStackObjectID()), 
								 "ATTACK", 
								 String.valueOf(attack.getAttacker().GetCardUID()), 
								 String.valueOf( attack.getDefender().GetCardUID()));
		GameMessage( String.format( "%s's %s is attacking %s's %s.", 
				attack.getAttacker().getController().getClientAccount().getUserName(), 
				attack.getAttacker().GetCardTemplate().getName() ,
				attack.getDefender().getController().getClientAccount().getUserName(),
				attack.getDefender().GetCardTemplate().getName()) );
	}
	public void PutCardOntoField( ServerCardInstance card ) {
		card.SetLocation( GameZones.FIELD );
		m_CardsOnField.put( card.GetCardUID(), card );
		SendMessageToAllPlayers( ClientMessages.MOVE, String.valueOf( card.GetCardUID() ), GameZones.FIELD.name() );
	}
	public void GameMessage( String message ) {
		for( int gp : m_PlayerList ) 
			m_Players.get(gp).getClientAccount().SendMessage( ClientMessages.MESSAGE, "Game " + m_GameInstanceID, message );
	}
	
	/**
	 * Ends the current turn and starts the next one.
	 */
	private void EndTurn() {
		ChangeState(GameStates.BETWEEN_TURNS);
		NextPlayer();
		StartTurn();
	}
	
	/**
	 * Gets the next player, if there is no next player. 
	 * The iterator is reset before getting a player (aka gets the first player in the list)
	 */
	private void NextPlayer() {
		if( ++m_CurrentPlayerIndex >= m_Players.size() ) { m_CurrentPlayerIndex = 0; }
		m_CurrentPlayer = m_Players.get( m_PlayerList.get( m_CurrentPlayerIndex ) );
	}

	/**
	 * Ran by GamePlayers. If run, and all players are ready: the game starts.
	 */
	public void Ready() {
		boolean start_the_game = true;
		for( Integer i : m_PlayerList ) { 
			GamePlayer player = m_Players.get(i);
			if( player.getState() == PlayerStates.JOINED ) {
				start_the_game = false;
			}
		}
		
		if( start_the_game ) {
			StartGame();
		}
	}

	public void StartStacking() {
		ChangeState(GameStates.STACKING);
	}
	
	public void ChangeState( GameStates newState ) {
		m_GameInstanceState = newState;
		SendMessageToAllPlayers( ClientMessages.GAME_STATE, m_GameInstanceState.name() );
	}

	public ServerCardInstance GetCardInstance(Integer valueOf) {
		return m_Directory.get( valueOf );
	}

	public Collection<GamePlayer> getPlayers() {
		return m_Players.values();
	}
}
