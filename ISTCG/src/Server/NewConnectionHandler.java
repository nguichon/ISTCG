package Server;

import java.io.IOException;
import java.net.ServerSocket;

import Shared.ConnectionDevice;

public class NewConnectionHandler extends Thread {
	private NewConnectionHandler() {
		int port = 4567;
		m_NewConnectionPort = null;
		
		try {
			m_NewConnectionPort = new ServerSocket( port );
		} catch (IOException e) {
			//ConsoleMessage( '!', "Failed" );
			//ConsoleMessage( '!', "The server failed to open a port at " + port + ". Quitting." );
			e.printStackTrace();
			System.exit( -1 );
		}
	}
	private static NewConnectionHandler m_Instance;
	public static NewConnectionHandler get() {
		if( m_Instance == null ) { m_Instance = new NewConnectionHandler(); }
		return m_Instance;
	}
	
	@Override
	public void run() {
		while( !m_Quit ) {
			ConnectionDevice client = null;
			
			try{
				client = new ConnectionDevice(m_NewConnectionPort.accept());
				//ConsoleMessage( '-', "Client Connected." );
				new ClientAccount(client);
			} catch (IOException e ) {
				//ConsoleMessage( '!', "Error when accepting Socket. Continuing." );
				e.printStackTrace();
			}
			
			if( client == null ) {
				//ConsoleMessage( '?', "Variable clientSocket was null. Waiting for connection." );
			}
		}

		try {
			m_NewConnectionPort.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Quit() {
		m_Quit = true;
	}
	
	private boolean m_Quit;
	private ServerSocket m_NewConnectionPort;
}
