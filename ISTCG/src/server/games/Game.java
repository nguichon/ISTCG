package server.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

import server.games.cards.CardInstance;
import server.network.ClientAccount;
import server.network.ClientMessages;
import server.network.ClientResponses;

/**
 * This is a specific game. I'll write more documentation later...
 * @author Nicholas Guichon
 */
public class Game {
	
	//GAME CONSTANTS
	private static int STARTING_HAND_SIZE = 7;
	
	//Game setup variables
	private int m_GameID;
	private ArrayList<GamePlayer> m_Players;
	private boolean m_Started;
	
	//Game turn variables
	private GamePlayer m_CurrentPlayer;
	private int m_CurrentPlayerIndex;
	
	//Game object variables
	private HashMap<Integer, CardInstance> m_CardsInPlay = new HashMap<Integer, CardInstance>();
	private Stack<CardInstance> m_CardsOnStack = new Stack<CardInstance>();
	private int m_CardsCreated = 0;
	
	
	/**
	 * Gets a number to use for a unique card identifier.
	 * 
	 * @return
	 * 		An integer, unique to this instance of Game
	 */
	public synchronized int getNewCardID() {
		return m_CardsCreated++;
	}
	
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
	 * 
	 * @param ca 
	 * 		The ClientAccount/Socket connection for this new player
	 */
	private void AddToGame( ClientAccount ca ) {
		if( !m_Started ) {
			ca.SendMessage( ClientMessages.JOIN, "" + m_GameID );
			SendMessageToAllPlayers( ClientMessages.PLAYER_JOINED, "" + m_GameID,  ca.getUserName(), "" + ca.getUserID() );
			for( GamePlayer gp : m_Players ) { ca.SendMessage( ClientMessages.PLAYER_JOINED, "" + m_GameID,  gp.getAccount().getUserName(), "" + gp.GetPlayerID() ); }
			m_Players.add( new GamePlayer( this, ca ));
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
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Iterates through the list of GamePlayers, sending the same message to each of them
	 * @param message
	 * 		Message that will be sent to users
	 */
	public void SendMessageToAllPlayers( ClientMessages messageType, String ... parameters ) {
		for( GamePlayer gp : m_Players ) { gp.getClient().SendMessage( messageType, parameters ); }
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
		m_Started = true;
		
		for( GamePlayer gp : m_Players ) {
			gp.DrawCards( STARTING_HAND_SIZE );
		}
		
		m_CurrentPlayerIndex = new Random().nextInt( m_Players.size() );
		m_CurrentPlayer = m_Players.get( m_CurrentPlayerIndex );
		
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
		if( m_CurrentPlayerIndex++ >= m_Players.size() ) { m_CurrentPlayerIndex = 0; }
		m_CurrentPlayer = m_Players.get( m_CurrentPlayerIndex );
	}

	/**
	 * Ran by GamePlayers. If run, and all players are ready: the game starts.
	 */
	public synchronized void Ready() {
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
