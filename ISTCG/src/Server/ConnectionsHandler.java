package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ConnectionsHandler extends Thread {
	private ArrayList<ClientAccount> m_ConnectedClients = null;
	private Map<String, ClientAccount> m_AuthenticatedClient = null;
	private static ConnectionsHandler m_Instance;
	private ConnectionsHandler() {
		int port = 4567;
		m_NewConnectionPort = null;
		
		try {
			m_NewConnectionPort = new ServerSocket( port );
		} catch (IOException e) {
			ServerMain.ConsoleMessage( '!', "Failed" );
			ServerMain.ConsoleMessage( '!', "The server failed to open a port at " + port + ". Quitting." );
			e.printStackTrace();
			System.exit( -1 );
		}
		
		m_ConnectedClients = new ArrayList<ClientAccount>();
		m_AuthenticatedClient = new HashMap<String, ClientAccount>();
	}
	public static ConnectionsHandler get() {
		if( m_Instance == null ) { m_Instance = new ConnectionsHandler(); }
		return m_Instance;
	}
	
	@Override
	public void run() {
		try {
			m_NewConnectionPort.setSoTimeout(1000);
		} catch (SocketException e1) {
			ServerMain.ConsoleMessage('!', "Error setting  Socket Timeout, how odd.");
			e1.printStackTrace();
		}
		
		while( !m_Quit ) {
			Socket client = null;
			
			try{
				client = m_NewConnectionPort.accept();
				ServerMain.ConsoleMessage( '-', "A client connected." );
				ClientAccount ca = new ClientAccount(client);
				ca.start();
				m_ConnectedClients.add(ca);
			}	catch ( java.net.SocketTimeoutException e )  {
				//DO NOTHING
			}	catch (IOException e ) {
				ServerMain.ConsoleMessage( '!', "Error when accepting Socket. Continuing." );
				e.printStackTrace();
			}
		}

		try {
			m_NewConnectionPort.close();
		} catch (IOException e) {
			ServerMain.ConsoleMessage('!', "Error while closing ServerSocket m_NewConnectionPort");
			e.printStackTrace();
		}
	}
	
	public void Quit() {
		m_Quit = true;
	}
	
	private boolean m_Quit;
	private ServerSocket m_NewConnectionPort;
	
	public ClientAccount GetClientByName( String name ) {
		return m_AuthenticatedClient.get(name);
	}
	
	public void Authenticated( ClientAccount ca, String name ) {
		m_AuthenticatedClient.put( name, ca );
	}
	
	public void RemoveAuthenticatedClientAccount( ClientAccount ca ) {
		m_AuthenticatedClient.remove( ca.getUserName() );
	}
	public void RemoveConnectedClientAccount( ClientAccount ca ) {
		RemoveAuthenticatedClientAccount( ca );
		m_ConnectedClients.remove( ca );
	}
	
	public void SendMessageToAll( ClientMessages messageType, String ... parameters ) {
		for(ClientAccount ca : m_ConnectedClients) {
			ca.SendMessage( messageType, parameters );
		}
	}
	public void SendMessageToAllAuthenticated( ClientMessages messageType, String ... parameters ) {
		for(ClientAccount ca : m_AuthenticatedClient.values()) {
			ca.SendMessage( messageType, parameters );
		}
	}
}
