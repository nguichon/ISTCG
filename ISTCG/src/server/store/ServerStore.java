package server.store;

import java.sql.ResultSet;
import java.sql.SQLException;

import server.Database;
import server.network.ClientAccount;
import server.network.ConnectionsHandler;
import Shared.ClientMessages;

public class ServerStore {
	public static void SendBalance( ClientAccount ca ) {
		ca.SendMessage( ClientMessages.BALANCE, String.valueOf( GetBalance( ca ) ) );
	}
	public static int GetBalance( ClientAccount ca ) {
		ResultSet rs = Database.get().quickQuery( "SELECT points FROM users WHERE id = " + ca.getUserID() );
		try {
			if( rs.next() ) {
				return rs.getInt( "points" );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
