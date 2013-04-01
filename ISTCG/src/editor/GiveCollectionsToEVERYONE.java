package editor;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.Database;
import server.ServerMain;

public class GiveCollectionsToEVERYONE {
	public static void main(String[] args) {
		try {
			Database.initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Database.get().quickInsert( "DELETE FROM COLLECTIONS" );
		ResultSet rs = Database.get().quickQuery( "SELECT * FROM USERS;" );
		
		try {
			while(rs.next()) {
				GiveCollection(rs);
			}
		} catch (SQLException e) {
			ServerMain.ConsoleMessage('!', "A card failed to load, fatal error");
			e.printStackTrace();
		}
	}
	
	private static void GiveCollection( ResultSet rs ) {
		try {
			int user = rs.getInt( "id" );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
