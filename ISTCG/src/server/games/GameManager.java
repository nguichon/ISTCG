package server.games;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import Shared.ClientMessages;

import server.Database;
import server.ServerMain;
import server.network.ClientAccount;


public class GameManager {
	private Map<Integer, GameInstance> m_ActiveGames;
	
	private int GetNewGameNumber() {
		ResultSet rs = Database.get().quickQuery("INSERT INTO GAMES DEFAULT VALUES RETURNING id;");
		try {
			rs.next();
			int value = rs.getInt(1);
			
			return value;
		} catch (SQLException e) {
			e.printStackTrace();
			ServerMain.ConsoleMessage('!', "Failed to get new game ID");
			return -1;
		}
	}

	public int CreateGame( ClientAccount... players ) {
		int gameNumber = GetNewGameNumber();
		synchronized( m_ActiveGames ) {
			m_ActiveGames.put( gameNumber , new GameInstance(gameNumber, players) );
		}
		return gameNumber;
	}
	
	public void RemoveGame( int id ) {
		synchronized( m_ActiveGames ) {
			m_ActiveGames.remove( id );
		}
	}
	
	public void RemoveGame( GameInstance g ) {
		RemoveGame( g.GetGameID() );
	}
	
	public void SendMessageToGame( int game_id, ClientAccount origin, String[] message ) {
		GameInstance targetGame = m_ActiveGames.get(game_id);
		if( !(targetGame == null) ) {
			targetGame.HandleMessage(origin, message);
		} else {
			origin.SendMessage( ClientMessages.SERVER, "Bad game id: " + game_id + "." );
		}
	}
	
	//=====
	//Singleton methods
	//=====
	private static GameManager m_Instance;
	private GameManager() {
		m_ActiveGames = new HashMap<Integer, GameInstance>();
	}
	public static GameManager get() {
		if( m_Instance == null ) { m_Instance = new GameManager(); }
		return m_Instance;
	}
}
