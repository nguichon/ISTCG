package server.store;

import Shared.ClientMessages;
import server.Database;
import server.network.ClientAccount;

public class StoreItem {
	public int item_id;
	public int item_cost;
	public int item_name;
	public int item_description;
	
	public void Purchase( ClientAccount buyer ) {
		int buyerPoints = ServerStore.GetBalance( buyer );
		if( buyerPoints >= item_cost ) {
			Database.get().quickInsert( "UPDATE USERS SET points = " + ( buyerPoints - item_cost ) + " WHERE id = " + buyer.getUserName() );
			buyer.SendMessage( ClientMessages.RECEIPT, String.valueOf(ServerStore.GetBalance( buyer ) ));
		}
	}
}
