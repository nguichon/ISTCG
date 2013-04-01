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
			
			Database.get().quickInsert( "INSERT INTO collections(" +
					"id, created_at, modified_at, card_id, owner_id, count, is_signed, is_foil, set_id)" +
					"VALUES (DEFAULT, DEFAULT, DEFAULT, 0, " + user + ", 20, false, false, -1);" );
			Database.get().quickInsert( "INSERT INTO collections(" +
					"id, created_at, modified_at, card_id, owner_id, count, is_signed, is_foil, set_id)" +
					"VALUES (DEFAULT, DEFAULT, DEFAULT, 1, " + user + ", 20, false, false, -1);" );
			Database.get().quickInsert( "INSERT INTO collections(" +
					"id, created_at, modified_at, card_id, owner_id, count, is_signed, is_foil, set_id)" +
					"VALUES (DEFAULT, DEFAULT, DEFAULT, 2, " + user + ", 20, false, false, -1);" );
			Database.get().quickInsert( "INSERT INTO collections(" +
					"id, created_at, modified_at, card_id, owner_id, count, is_signed, is_foil, set_id)" +
					"VALUES (DEFAULT, DEFAULT, DEFAULT, 3, " + user + ", 1, false, false, -1);" );
			Database.get().quickInsert( "INSERT INTO collections(" +
					"id, created_at, modified_at, card_id, owner_id, count, is_signed, is_foil, set_id)" +
					"VALUES (DEFAULT, DEFAULT, DEFAULT, 5, " + user + ", 10, false, false, -1);" );
			Database.get().quickInsert( "INSERT INTO collections(" +
					"id, created_at, modified_at, card_id, owner_id, count, is_signed, is_foil, set_id)" +
					"VALUES (DEFAULT, DEFAULT, DEFAULT, 19, " + user + ", 10, false, false, -1);" );
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
