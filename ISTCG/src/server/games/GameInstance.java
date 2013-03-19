package server.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

import Shared.ClientMessages;
import Shared.ClientResponses;
import Shared.GameZones;

import server.games.cards.ServerCardInstance;
import server.games.cards.ServerCardTemplateManager;
import server.network.ClientAccount;

/**
 * This is a specific game. I'll write more documentation later...
 * @author Nicholas Guichon
 */
public class GameInstance {
	
	//GAME CONSTANTS
	private static int STARTING_HAND_SIZE = 7;
	
	//Game setup variables
	private int m_GameID;
	private HashMap< Integer, GamePlayer > m_Players;
	private HashMap< Integer, ServerCardInstance > m_Directory = new HashMap< Integer, ServerCardInstance >();
	private ArrayList< Integer > m_PlayerList;
	private boolean m_Started;
	
	//Game turn variables
	private GamePlayer m_CurrentPlayer;
	private int m_CurrentPlayerIndex;
	
	//Game object variables
	private HashMap<Integer, ServerCardInstance> m_CardsInPlay = new HashMap<Integer, ServerCardInstance>();
	private Stack<ServerCardInstance> m_CardsOnStack = new Stack<ServerCardInstance>();
	private int m_CardsCreated = 0;
	
	
	/**
	 * Gets a number to use for a unique card identifier.
	 * 
	 * @return
	 * 		An integer, unique to this instance of Game
	 */
	public synchronized int CreateNewCardID() {
		return m_CardsCreated++;
	}
	
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
		m_GameID = id;
		m_Started = false;
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
		if( !m_Started && !m_Players.containsKey( clientToAdd.getUserID() ) ) {
			clientToAdd.SendMessage( ClientMessages.JOIN, "" + m_GameID );
			
			SendMessageToAllPlayers( ClientMessages.PLAYER_JOINED, "" + m_GameID,  clientToAdd.getUserName(), "" + clientToAdd.getUserID() );
			
			for( Integer i : m_PlayerList ) { 
				GamePlayer player = m_Players.get(i);
				clientToAdd.SendMessage( 
						ClientMessages.PLAYER_JOINED, 
						m_GameID + "",  
						player.getAccount().getUserName(), 
						player.GetPlayerID() + ""); 
			}
			
			m_Players.put( clientToAdd.getUserID(), new GamePlayer( this, clientToAdd ) );
			m_PlayerList.add( clientToAdd.getUserID() );
		}
	}

	/**
	 * Handles a message being sent. ClientAccounts will send certain messages here
	 * @param origin
	 * 		The player_id of the ClientAccount that this message originates from.
	 * @param message
	 * 		The actual message sent by the ClientAccount, split on ';'
	 */
	public synchronized void HandleMessage( int origin, String[] message ) {
		try {
			switch(ClientResponses.valueOf( message[0].toUpperCase() )) {
				case END:
					if( m_CardsOnStack.empty() ) {
						if( m_Started && m_CurrentPlayer.GetPlayerID() == origin ) { 
							EndTurn(); 
						}
					}
					break;
				case DECKLIST:
					m_Players.get( origin ).LoadDeck( message[2] );
					break;
				case GETCARDINFO:
					m_Directory.get( Integer.valueOf(message[2]) ).SendCardInformation( m_Players.get( origin ) );
					break;
				case PASS:
					m_Players.get( origin ).Pass();
					break;
				case PLAY:
					m_Players.get( origin ).PlayCard( new Integer(message[1]) );
					break;
				default:
					break;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private void PlayCard(int origin, int card_template_id) {
		//m_CardsOnStack.add( new CardInstance( this, origin, CardTemplateManager.get().GetCardTemplate( card_template_id )  ) );
	}

	/**
	 * Iterates through the list of GamePlayers, sending the same message to each of them
	 * @param message
	 * 		Message that will be sent to users
	 */
	public void SendMessageToAllPlayers( ClientMessages messageType, String ... parameters ) {
		for( Integer i : m_PlayerList ) { 
			GamePlayer player = m_Players.get(i);
			player.getClient().SendMessage( messageType, parameters ); 
		}
	}
	
	/**
	 * Gets the id of this game, as determined by its constructor.
	 * @return
	 * 		The database ID number of this game, this ID number should be unique unless constructed illegally.
	 */
	public int GetGameID() {
		return m_GameID;
	}
	
	/**
	 * Starts this game, drawing each player's initial hand and picking a random starting player.
	 */
	private void StartGame() {
		if( !m_Started ) {
			m_Started = true;
			
			for( Integer i : m_PlayerList ) { 
				GamePlayer player = m_Players.get(i);
				player.DrawCards( STARTING_HAND_SIZE );
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
		SendMessageToAllPlayers( ClientMessages.PLAYER_TURN, "" + m_GameID, "" + m_CurrentPlayer.GetPlayerID());
		m_CurrentPlayer.DrawCards(1);
	}
	
	/**
	 * Ends the current turn.
	 */
	private void EndTurn() {
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
			if( !player.isReady() ) {
				start_the_game = false;
			}
		}
		
		if( start_the_game ) {
			StartGame();
		}
	}

	public void AddToStack(ServerCardInstance cardPlayed) {
		cardPlayed.SetLocation( GameZones.STACK );
		m_CardsOnStack.add( cardPlayed );
		
		SendMessageToAllPlayers( 
        		ClientMessages.MOVE,
				GetGameID() + "",
				cardPlayed.GetCardUID() + "",
				GameZones.STACK.name() );
		
	}

	public boolean GameStackStep() {
		int initial_player = m_CurrentPlayerIndex;
		int current_player = initial_player;
		
		while( m_Players.get( m_PlayerList.get( current_player ) ).getState() == GamePlayerStates.DONE ) {
			current_player++;
			if( current_player >= m_PlayerList.size() ) {
				current_player = 0;
			}
			
			if( current_player == initial_player ) {
				return false;
			}
		}
		
		m_Players.get( m_PlayerList.get( current_player ) ).getAccount().SendMessage( ClientMessages.PRIORITY );
		
		return true;
	}
	public void MakeAllPlayersActive() {
		for( Integer i : m_PlayerList ) { 
			GamePlayer player = m_Players.get(i);
			player.SetState( GamePlayerStates.ACTIVE );
		}		
	}
}
