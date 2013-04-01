package server.store;

import java.sql.ResultSet;
import java.sql.SQLException;

import Shared.ClientMessages;
import server.Database;
import server.network.ClientAccount;

public class StoreItem {
	public int item_id;
	public int item_cost;
	public String item_name;
	public String item_description;
	
	public void Purchase( ClientAccount buyer ) {
		int buyerPoints = ServerStore.GetBalance( buyer );
		if( buyerPoints >= item_cost ) {
			Database.get().quickInsert( "UPDATE USERS SET points = " + ( buyerPoints - item_cost ) + " WHERE id = " + buyer.getUserID() );
			buyer.SendMessage( ClientMessages.RECEIPT, String.valueOf(ServerStore.GetBalance( buyer ) ));
		}
	}
	
	public StoreItem( ResultSet rs ) {
		try {
			item_id = rs.getInt("id");
			item_cost = rs.getInt("point_cost");
			item_name = rs.getString( "item_name" );
			item_description = rs.getString( "description" );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
