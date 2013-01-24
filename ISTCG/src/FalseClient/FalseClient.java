package FalseClient;

import java.io.IOException;
import java.util.Scanner;

import Client.LoginUI;
import Client.ClientMain.GameState;
import Shared.ConnectionDevice;
import Shared.ThreadedConnectionDevice;

public class FalseClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner consoleInput = new Scanner(System.in);
		
		int port = 4567;
  		String host = "127.0.0.1";
		ThreadedConnectionDevice m_Server = null;
		try {
			m_Server = new ThreadedConnectionDevice( host, port );
		} catch (IOException e) {
			System.exit(2000);
		}
		
		boolean m_Quit = false;
		while( !m_Quit ) {
			if(consoleInput.hasNext()) {
				String s = consoleInput.nextLine();
				if( s.equals( "quit" ) ) {
					m_Quit = true;
				} else  {
					m_Server.sendData( s );
				}
			}

			String serverData;
			if( m_Server.hasData() ) {
				if( ( serverData = m_Server.getData() ) != "" ) {
					System.out.println( serverData );
				}
			}
		}
		
		
	}

}
