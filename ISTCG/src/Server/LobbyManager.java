package Server;

public class LobbyManager {

	public static void say(String userName, String message) {
		MessageHandler.get().SayToAll("SAY;" + userName + ";" + message);
	}

	public static void loginMessage(String userName) {
		MessageHandler.get().SayToAll("LOGGED_IN_MESSAGE;" + userName);
	}
	
	public static void whisper(String userName1, String userName2, String message) {
		MessageHandler.get().SayTo("WIS;"+userName1+";"+userName2+";"+message);
	}
}
