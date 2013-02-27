package Server;

import java.util.ArrayList;

import Shared.GameZones;

public class Game {
	//GAME CONSTANTS
	private static int STARTING_HAND_SIZE = 7;
	
	private int m_GameID;
	ArrayList<GamePlayer> m_Players;
	private boolean m_Started;
	
	public Game(int id, ClientAccount[] players) {
		m_GameID = id;
		m_Started = false;
		m_Players = new ArrayList<GamePlayer>();
		for(ClientAccount ca : players) { AddToGame( ca ); }
	}
	
	public void AddToGame( ClientAccount ca ) {
		ca.SendMessage( "JOIN;" + m_GameID );
		SendMessageToAllPlayers( "PLAYERJOINED;" + m_GameID + ";" + ca.getUserID() + ";" + ca.getUserName() );
		for( GamePlayer gp : m_Players ) { ca.SendMessage( "PLAYERJOINED;" + m_GameID + ";" + gp.getClient().getUserID() + ";" + gp.getClient().getUserName() ); }
		m_Players.add( new GamePlayer( this, ca ));
	}

	public void HandleMessage( int origin, String[] message ) {
		switch(ClientResponses.valueOf( message[0].toUpperCase() )) {
		default:
			break;
		}
	}
	
	public void SendMessageToAllPlayers( String message ) {
		for( GamePlayer gp : m_Players ) {
			gp.getClient().SendMessage( message );
		}
	}
	
	public int GetID() {
		return m_GameID;
	}
	
	private void StartGame() {
		for( GamePlayer gp : m_Players ) {
			gp.DrawCards( 7 );
		}
	}

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
