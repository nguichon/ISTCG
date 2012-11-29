package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.Date;

import Shared.ConnectionDevice;

public class ServerMain {
	public static void main(String[] args) {
		ConsoleMessage( '-', "Server Started" );
		
		//Initialize the Database connection.
		try {
			Database.initialize();
		} catch (SQLException e) {
			//If it fails to initialize, exit program (game breaking).
			ConsoleMessage( '!', "The connection to the database failed to initialize." );
			e.printStackTrace();
			System.exit(-1);
		}
		
		/*
		 * Server's port data
		 * TODO: Configurable via configuration file
		 */
		int port = 4567;
		ServerSocket newConnectionPort = null;
		
		//Attempt to create the server port
		try {
			newConnectionPort = new ServerSocket( port );
		} catch (IOException e) {
			ConsoleMessage( '!', "The server failed to open a port at " + port + ". Quitting." );
			e.printStackTrace();
			System.exit( -1 );
		}
		
		//If the server port ever ends up null, something is very very odd.
		if( newConnectionPort == null ) {
			ConsoleMessage( '?', "Variable newConnectionPort was null. Quitting." );
			System.exit( -1 );
		}
		
		//Exit "while" loop
		boolean shutDownServer = false;
		MessageHandler.get().run();
		
		//Wait for connections, and connect them
		//TODO: Wait for console command and execute them
		while( shutDownServer == false ) {
			ConnectionDevice client = null;
			try{
				client = new ConnectionDevice(newConnectionPort.accept());
				ConsoleMessage( '-', "Client Connected." );
				new ClientAccount(client);
			} catch (IOException e ) {
				ConsoleMessage( '!', "Error when accepting Socket. Continuing." );
				e.printStackTrace();
			}
			
			if( client == null ) {
				ConsoleMessage( '?', "Variable clientSocket was null. Waiting for connection." );
			}
		}
		
		try {
			newConnectionPort.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	public static void ConsoleMessage( char sym, String message ) {
		Date date = new Date();
		System.out.println( "[" + sym + "] " 
							+ date.toString()
							+ " - " + message );
	}

}
