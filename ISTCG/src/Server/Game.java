package Server;

import java.util.ArrayList;

public class Game {
	private int m_GameID;
	ArrayList<GamePlayer> m_Players;
	
	public Game(int id, ClientAccount[] players) {
		m_GameID = id;
		
		m_Players = new ArrayList<GamePlayer>();
		for(ClientAccount ca : players) { AddToGame( ca ); }
	}
	
	public void AddToGame( ClientAccount ca ) {
		SendMessageToAllPlayers( "PLAYERJOINED;" + m_GameID + ";" + ca.getId() + ";" + ca.getName() );
		for( GamePlayer gp : m_Players ) { ca.SendMessage( "PLAYERJOINED;" + m_GameID + ";" + gp.getClient().getId() + ";" + gp.getClient().getName() ); }
		m_Players.add( new GamePlayer( this, ca ));
	}

	public void HandleMessage( String[] message ) {
		
	}
	
	public void SendMessageToAllPlayers( String message ) {
		for( GamePlayer gp : m_Players ) {
			gp.getClient().SendMessage( message );
		}
	}
	
	public int GetID() {
		return m_GameID;
	}
}
