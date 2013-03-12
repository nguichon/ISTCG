package server;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import server.admin.AdminCommands;
import server.games.cards.CardTemplateManager;
import server.network.ClientAccount;
import server.network.ConnectionsHandler;

import Shared.CardTemplates;
import Shared.StatBlock;

/**
 * Main class for the Server.
 * 
 * @author Grogian
 */
public class ServerMain {
	static boolean m_Quit;

	/**
	 * Server starts here.
	 * 
	 * @param args
	 *            Command line arguments for the server
	 */
	public static void main(String[] args) {
		ConsoleMessage('-', "Server Starting...");

		// Initialize the Database connection.
		ConsoleMessage('-', "Connecting to Database...");

		try {
			Database.initialize();
		} catch (SQLException e) {
			// If it fails to initialize, exit program (game breaking).
			ConsoleMessage('!', "Failed");
			ConsoleMessage('!',
					"The connection to the database failed to initialize.");
			e.printStackTrace();
			System.exit(-1);
		}

		ConsoleMessage('-', "Successfully connected to Database...");

		// Initialize Card Templates
		ConsoleMessage('-', "Initalizing cards...");

		CardTemplateManager.get().Initialize();

		ConsoleMessage('-', "Finished intializing cards...");

		// Attempt to create the server port
		ConsoleMessage('-', "Creating server port...");

		ConnectionsHandler.get().start();

		ConsoleMessage('-', "Finished creating server port...");

		// Create something to accept console input
		Scanner consoleInput = new Scanner(System.in);

		// Start main thread loop
		m_Quit = false;
		ConsoleMessage('-', "Server Started!");
		while (!m_Quit) {
			if (consoleInput.hasNext()) {
				ParseConsoleCommand(consoleInput.nextLine());
			}
		}
		ConsoleMessage('-', "Server Stopping...");

		// Stop accepting new connections
		ConnectionsHandler.get().Quit();

		// Clean up
		consoleInput.close();
		System.exit(0);
	}

	public static void Quit() { m_Quit = true; }
	
	public static String RunCommand( String text ) {
		String[] command = text.split(" ");
		
		try {
			return AdminCommands.valueOf(command[0].toUpperCase()).Activate(command);
		} catch (IllegalArgumentException e) {
			return "Invalid command";
		}
	}
	private static void ParseConsoleCommand(String next) {
		ConsoleMessage('?',RunCommand(next));
	}

	public static void ConsoleMessage(char sym, String message) {
		Date date = new Date();
		System.out
				.println("[" + sym + "] " + date.toString() + " - " + message);
	}
}
