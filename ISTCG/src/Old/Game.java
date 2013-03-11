package Old;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * This is a specific game. I'll write more documentation later...
 * @author Nicholas Guichon
 */
public class Game {
	//GAME CONSTANTS
	private static int STARTING_HAND_SIZE = 7;
	
	//Game setup variables
	private int m_GameID;
	ArrayList<GamePlayer> m_Players;
	private boolean m_Started;
	
	//Game turn variables
	Iterator<GamePlayer> m_PlayerIterator;
	GamePlayer m_CurrentPlayer;
	
	/**
	 * This creates a game instance, should ONLY be called by GameManager
	 * 
	 * @param id 
	 * 		The specific id of this game, passed in by GameManager.
	 * @param players 
	 * 		List of players that will be in this game.
	 */
	public Game(int id, ClientAccount[] players) {
		m_GameID = id;
		m_Started = false;
		m_Players = new ArrayList<GamePlayer>();
		for(ClientAccount ca : players) { AddToGame( ca ); }
	}
	
	/**
	 * Adds a new player to the game.
	 * @param ca 
	 * 		The ClientAccount/Socket connection for this new player
	 */
	private void AddToGame( ClientAccount ca ) {
		ca.SendMessage( ClientMessages.JOIN, "" + m_GameID );
		SendMessageToAllPlayers( ClientMessages.PLAYER_JOINED, "" + m_GameID,  ca.getUserName(), "" + ca.getUserID() );
		for( GamePlayer gp : m_Players ) { ca.SendMessage( ClientMessages.PLAYER_JOINED, "" + m_GameID,  gp.getAccount().getUserName(), "" + gp.GetPlayerID() ); }
		m_Players.add( new GamePlayer( this, ca ));
	}

	/**
	 * Handles a message being sent. ClientAccounts will send certain messages here
	 * @param origin
	 * 		The player_id of the ClientAccount that this message originates from.
	 * @param message
	 * 		The actual message sent by the ClientAccount, split on ';'
	 */
	public synchronized void HandleMessage( int origin, String[] message ) {
		switch(ClientResponses.valueOf( message[0].toUpperCase() )) {
		case END:
			//TODO needs to check that "stack" is empty
			if( m_Started && m_CurrentPlayer.GetPlayerID() == origin ) { EndTurn(); }
			break;
		case DECKLIST:
			for( GamePlayer gp : m_Players ) { 
				if(gp.GetPlayerID() == Integer.valueOf(origin)) {
					gp.LoadDeck( message[2] );
				}
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Iterates through the list of GamePlayers, sending the same message to each of them
	 * @param message
	 * 		Message that will be sent to users
	 */
	public void SendMessageToAllPlayers( ClientMessages messageType, String ... parameters ) {
		for( GamePlayer gp : m_Players ) {
			gp.getClient().SendMessage( messageType, parameters );
		}
	}
	
	/**
	 * Gets the id of this game, as determined by its constructor.
	 * @return
	 * 		The database ID number of this game, this ID number should be unique unless constructed illegally.
	 */
	public int GetID() {
		return m_GameID;
	}
	
	/**
	 * Starts this game, drawing each player's initial hand and picking a random starting player.
	 */
	private void StartGame() {
		for( GamePlayer gp : m_Players ) {
			gp.DrawCards( STARTING_HAND_SIZE );
		}
		
		m_PlayerIterator = m_Players.iterator();
		int firstPlayer = new Random().nextInt( m_Players.size() );
		for( int i = 0; i <= firstPlayer; i++ ) {
			NextPlayer();
		}
		
		StartTurn();
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
		if( !m_PlayerIterator.hasNext() ) { m_PlayerIterator = m_Players.iterator(); }
		m_CurrentPlayer = m_PlayerIterator.next();
	}

	/**
	 * Ran by GamePlayers. If run, and all players are ready: the game starts.
	 */
	public void Ready() {
		boolean start = true;
		for( GamePlayer gp : m_Players ) {
			if( !gp.isReady() ) {
				start = false;
			}
		}
		
		if( start ) {
			StartGame();
		}
	}
}
