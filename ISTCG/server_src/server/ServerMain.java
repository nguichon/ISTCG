package server;

import java.util.Date;

public class ServerMain {
	public static void main(String[] args) {
	}

	public static void DisplayMessage(String sym, String message) {
		Date date = new Date();
		System.out
				.println("[" + sym + "] " + date.toString() + " - " + message);
	}
}