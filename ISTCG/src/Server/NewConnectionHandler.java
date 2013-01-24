package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

import Shared.ThreadedConnectionDevice;

public class NewConnectionHandler extends Thread {
	private ArrayList<ClientAccount> clients = null;
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
	private NewConnectionHandler(ArrayList<ClientAccount> clients) {
		int port = 4567;
		m_NewConnectionPort = null;
		this.clients = clients;
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
	public static NewConnectionHandler get(ArrayList<ClientAccount> clients) {
		if( m_Instance == null ) { m_Instance = new NewConnectionHandler(clients); }
		return m_Instance;
	}
	@Override
	public void run() {
		try {
			m_NewConnectionPort.setSoTimeout(1000);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while( !m_Quit ) {
			ThreadedConnectionDevice client = null;
			
			try{
				client = new ThreadedConnectionDevice(m_NewConnectionPort.accept());
				//ConsoleMessage( '-', "Client Connected." );
				if( client.isValid() ) {
					clients.add(new ClientAccount(client));
				}
			}	catch ( java.net.SocketTimeoutException e )  {
				//DO NOTHING
			}	catch (IOException e ) {
				//ConsoleMessage( '!', "Error when accepting Socket. Continuing." );
				e.printStackTrace();
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
