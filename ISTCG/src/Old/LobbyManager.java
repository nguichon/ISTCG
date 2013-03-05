package Old;

public class LobbyManager {

	public static void say(String userName, String message) {
		ConnectionsHandler.get().SendMessageToAllAuthenticated("SAY;" + userName + ";" + message);
		//MessageHandler.get().SayToAll("SAY;" + userName + ";" + message);
	}

	public static void loginMessage(String userName) {
		ConnectionsHandler.get().SendMessageToAllAuthenticated("LOGGED_IN_MESSAGE;" + userName);
		//MessageHandler.get().SayToAll("LOGGED_IN_MESSAGE;" + userName);
	}
	
	public static boolean whisper(String userName1, String userName2, String message) {
		//ConnectionsHandler.get().SendMessageToAll("TELL;"+userName1+";"+userName2+";"+message);
		ClientAccount ca = ConnectionsHandler.get().GetClientByName( userName2 );
		if( ca != null ) {
			ca.SendMessage("SAY;" + "from [" + userName1 + "];" + message);
			return true;
		}
		return false;
		//MessageHandler.get().SayTo("WIS;"+userName1+";"+userName2+";"+message);
	}
}