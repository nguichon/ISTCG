package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

import Shared.ConnectionDevice;

public class ServerMain {
	public static void main(String[] args) {
		ConsoleMessage( '-', "Server Starting..." );
		
		//Initialize the Database connection.
		ConsoleMessage( '-', "Connecting to Database..." );
		
		try {
			Database.initialize();
		} catch (SQLException e) {
			//If it fails to initialize, exit program (game breaking).
			ConsoleMessage( '!', "Failed" );
			ConsoleMessage( '!', "The connection to the database failed to initialize." );
			e.printStackTrace();
			System.exit(-1);
		}
		
		ConsoleMessage( '-', "Done" );
		
		//Attempt to create the server port
		ConsoleMessage( '-', "Creating server port..." );
		
		NewConnectionHandler.get().start();
		
		ConsoleMessage( '-', "Done" );
		
		//Exit "while" loop setup
		boolean shutDownServer = false;
		MessageHandler.get().start();
		Scanner consoleInput = new Scanner(System.in);
		
		//Wait for connections, and connect them
		//TODO: Wait for console command and execute them
		while( shutDownServer == false ) {
			if(consoleInput.hasNext()) {
				ParseConsoleCommand(consoleInput.nextLine());
			}
		}
		
		NewConnectionHandler.get().Quit();
		MessageHandler.get().Quit();
		
		System.exit(0);
	}
	private static void ParseConsoleCommand(String next) {
		// TODO Auto-generated method stub
		ConsoleMessage('r', next);
	}
	public static void ConsoleMessage( char sym, String message ) {
		Date date = new Date();
		System.out.println( "[" + sym + "] " 
							+ date.toString()
							+ " - " + message );
	}
	public static void ConsoleMessageNoNewLine( char sym, String message ) {
		Date date = new Date();
		System.out.print( "[" + sym + "] " 
							+ date.toString()
							+ " - " + message );
	}

}
