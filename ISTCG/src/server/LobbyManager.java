package server;

import server.network.ClientAccount;
import server.network.ClientMessages;
import server.network.ConnectionsHandler;

public class LobbyManager {

	public static void say( String sender, String message) {
		ConnectionsHandler.get().SendMessageToAllAuthenticated( ClientMessages.MESSAGE, sender, message );
	}

	public static void loginMessage(String userName) {
		ConnectionsHandler.get().SendMessageToAllAuthenticated( ClientMessages.USER_LOGGED_IN, userName );
	}
	
	public static boolean whisper(String sender, String receiver, String message) {
		ClientAccount ca = ConnectionsHandler.get().GetClientByName( receiver );
		if( ca != null ) {
			ca.SendMessage( ClientMessages.MESSAGE, sender, message );
			return true;
		}
		return false;
	}
}
