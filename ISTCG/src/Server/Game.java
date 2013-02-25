package Server;

import java.util.ArrayList;

import Shared.GameZones;

public class Game {
	private int m_GameID;
	ArrayList<GamePlayer> m_Players;
	
	public Game(int id, ClientAccount[] players) {
		m_GameID = id;
		
		m_Players = new ArrayList<GamePlayer>();
		for(ClientAccount ca : players) { AddToGame( ca ); }

		for(ClientAccount ca : players ) {
			SendMessageToAllPlayers("UPDATE;" + m_GameID + ";" + ca.getId() + ";" + GameZones.DECK + ";50");
			SendMessageToAllPlayers("UPDATE;" + m_GameID + ";" + ca.getId() + ";" + GameZones.HAND + ";8");
		}
	}
	
	public void AddToGame( ClientAccount ca ) {
		ca.SendMessage( "JOIN;" + m_GameID );
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
	
	public void LoadDeck( GamePlayer gp, String decklist ) {
		
	}
	
	public int GetID() {
		return m_GameID;
	}
}
