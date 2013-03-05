package Old;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class GameManager {
	private Map<Integer, Game> m_ActiveGames;
	
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
		m_ActiveGames.put( gameNumber , new Game(gameNumber, players) );
		return gameNumber;
	}
	
	public void RemoveGame( int id ) {
		m_ActiveGames.remove( id );
	}
	
	public void RemoveGame( Game g ) {
		RemoveGame( g.GetID() );
	}
	
	public void SendMessageToGame( int game_id, int origin, String[] message ) {
		m_ActiveGames.get(game_id).HandleMessage(origin, message);
	}
	
	//=====
	//Singleton methods
	//=====
	private static GameManager m_Instance;
	private GameManager() {
		m_ActiveGames = new HashMap<Integer, Game>();
	}
	public static GameManager get() {
		if( m_Instance == null ) { m_Instance = new GameManager(); }
		return m_Instance;
	}
}
