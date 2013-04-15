package server.store;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import server.Database;
import server.network.ClientAccount;
import server.network.ConnectionsHandler;
import Shared.ClientMessages;

public class ServerStore {
	private static HashMap<Integer, StoreItem> items = new HashMap<Integer, StoreItem> ();
	
	public static void Initialize() {
		/*ResultSet rs = Database.get().quickQuery( "SELECT * FROM store_items" );
		try {
			while( rs.next() ) {
				items.put( rs.getInt( "id" ), new StoreItem( rs ) );
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
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
	public static void Buy(ClientAccount ca, Integer product_id) {
		StoreItem si = items.get( product_id );
		if( si != null ) {
			si.Purchase( ca );
		} else {
			ca.SendMessage( ClientMessages.SERVER, "Item does not exist." );
		}
	}
	public static void GetInfo( ClientAccount ca, Integer product_id ) {
		StoreItem si = items.get( product_id );
		if( si != null ) {
			ca.SendMessage( ClientMessages.PRODUCTINFO, String.valueOf( si.item_id ), String.valueOf( si.item_cost ), si.item_name, si.item_description );
		} else {
			ca.SendMessage( ClientMessages.SERVER, "Item does not exist." );
		}
	}
}
