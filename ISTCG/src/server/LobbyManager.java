package server;

import Shared.ClientMessages;
import server.network.ClientAccount;
import server.network.ConnectionsHandler;

public class LobbyManager {

	public static void say( String sender, String message) {
		ConnectionsHandler.get().SendMessageToAllAuthenticated( ClientMessages.MESSAGE, sender, message );
	}

	public static void loginMessage(String userName) {
		ConnectionsHandler.get().SendMessageToAllAuthenticated( ClientMessages.USER_LOGGED_IN, userName );
	}
	
	public static boolean whisper( ClientAccount sender, String receiver, String message) {
		ClientAccount ca = ConnectionsHandler.get().GetClientByName( receiver );
		if( ca != null ) {
			ca.SendMessage( ClientMessages.PRIVATE_MESSAGE, sender.getUserName(), message );
			sender.SendMessage( ClientMessages.MESSAGE, "To " + receiver, message );
			return true;
		} else {
			sender.SendMessage( ClientMessages.SERVER, "User does not exist or is not logged in." );
			return false;
		}
	}
}
