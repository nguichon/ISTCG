package Server;

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

import Shared.CardBase;
import Shared.StatBlock;

public class ServerMain {
	static boolean m_Quit;
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
		
		//Initalize cardbase list
		ConsoleMessage('-', "Initalizing cards...");
		
		CardBaseStorage.get().Initialize();
		
		ConsoleMessage('-', "Finished intializing cards...");

		// Attempt to create the server port
		ConsoleMessage('-', "Creating server port...");

		ConnectionsHandler.get().start();

		ConsoleMessage('-', "Finished creating server port...");

		// Exit "while" loop setup
		Scanner consoleInput = new Scanner(System.in);

		m_Quit = false;
		ConsoleMessage('-', "Server Started!");
		while (!m_Quit) {
			if (consoleInput.hasNext()) {
				ParseConsoleCommand(consoleInput.nextLine());
			}
		}
		ConsoleMessage('-', "Server Stopping...");

		ConnectionsHandler.get().Quit();

		consoleInput.close();
		System.exit(0);
	}

	private static void ParseConsoleCommand(String next) {
		String[] command = next.split(" ");
		switch (ClientResponses.valueOf(command[0].toUpperCase())) {
		case QUIT:
			m_Quit = true;
			break;
		case CREATE_USER:
			try {
				ClientAccount.NewAccount(command[1], command[2], command[3]);
				ConsoleMessage('-', "User account " + command[1] + " created.");
			} catch (Exception e1) {
				ConsoleMessage('!', "Failed to create account");
				e1.printStackTrace();
			}
			break;
		case SELECT:
			ResultSet rs = Database.get().quickQuery(next);

			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (i != 1) {
						System.out.print(" | ");
					} else {
						System.out.println();
					}
					System.out.print(rsmd.getColumnName(i));
				}
				while (rs.next()) {
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						if (i != 1) {
							System.out.print(" | ");
						} else {
							System.out.println();
						}
						System.out.print(rs.getString(rsmd.getColumnName(i)));
					}
				}
				System.out.println();
			} catch (SQLException e) {
				ConsoleMessage('!', "Invalid Query");
				e.printStackTrace();
			}
			break;
		case USERS:
			// Lists users currently connected
			System.out.println("Getting List of All Connected Users:");
			
			break;
		case SHRINK:
			//TO DO: ADD CODE TO MINIMIZE CLIENTS
		default:
			ConsoleMessage('?', "Unknown command \"" + command[0] + "\"");
			break;
		}
	}

	public static void ConsoleMessage(char sym, String message) {
		Date date = new Date();
		System.out
				.println("[" + sym + "] " + date.toString() + " - " + message);
	}
}
