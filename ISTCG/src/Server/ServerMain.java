package Server;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

public class ServerMain {
	
	
	private enum Commands {
		QUIT, CREATE_USER, SELECT, USERS
	}
	
	static boolean m_Quit;
	
	public static void main(String[] args) {
		ConsoleMessage( '-', "Server Starting..." );
		
		//Initialize the Database connection.
		ConsoleMessage( '-', "Connecting to Database..." );
		ConsoleMessage( '-', "Successfully connected to Database..." );
		
		try {
			Database.initialize();
		} catch (SQLException e) {
			//If it fails to initialize, exit program (game breaking).
			ConsoleMessage( '!', "Failed" );
			ConsoleMessage( '!', "The connection to the database failed to initialize." );
			e.printStackTrace();
			System.exit(-1);
		}
		
		//Attempt to create the server port
		ConsoleMessage( '-', "Creating server port..." );
		
		NewConnectionHandler.get().start();
		
		ConsoleMessage( '-', "Finished creating server port..." );
		
		//Exit "while" loop setup
		MessageHandler.get().start();
		Scanner consoleInput = new Scanner(System.in);
		
		m_Quit = false;
		ConsoleMessage( '-', "Server Started!" );
		while( !m_Quit ) {
			if(consoleInput.hasNext()) {
				ParseConsoleCommand(consoleInput.nextLine());
			}
		}
		ConsoleMessage( '-', "Server Stopping..." );
		
		NewConnectionHandler.get().Quit();
		MessageHandler.get().Quit();
		
		System.exit(0);
	}
	private static void ParseConsoleCommand(String next) {
		String[] command = next.split(" ");
		switch( Commands.valueOf(command[0].toUpperCase()) ) {
		case QUIT:
			m_Quit = true;
			break;
		case CREATE_USER:
			try {
				ClientAccount.NewAccount( command[1], command[2], command[3] );
				ConsoleMessage('-',"User account " + command[1] + " created.");
			} catch (Exception e1) {
				ConsoleMessage('!',"Failed to create account");
				e1.printStackTrace();
			}
			break;
		case SELECT:
			ResultSet rs = Database.get().quickQuery(next);
			
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				for( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
					if( i != 1 ) {
						System.out.print( " | " );
					} else { System.out.println(); }
					System.out.print(rsmd.getColumnName(i));
				}
				while(rs.next()) {
					for( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
						if( i != 1 ) {
							System.out.print( " | " );
						} else { System.out.println(); }
						System.out.print(rs.getString(rsmd.getColumnName(i)));
					}
				}
				System.out.println();
			} catch (SQLException e) {
				ConsoleMessage('!',"Invalid Query");
				e.printStackTrace();
			}
			break;
		case USERS:
			//Lists users currently connected
			break;
		default:
			ConsoleMessage('?',"Unknown command \"" + command[0] + "\"");
			break;	
		}
	}
	public static void ConsoleMessage( char sym, String message ) {
		Date date = new Date();
		System.out.println( "[" + sym + "] " 
							+ date.toString()
							+ " - " + message );
	}
}
