package Client;

import java.io.IOException;
import Shared.ConnectionDevice;
import Shared.ThreadedConnectionDevice;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ClientMain {
	//***********************************
	// MAIN
	//***********************************
	public enum GameState{ NOCHANGE, LOGIN, GAME, MAIN, ALREADY_LOGGED_IN; }
	private GameStateUI[] m_UIList;
	private GameState m_NextGameState;
	private GameState m_CurrentGameState;
	
	public static void main(String[] args) {
		Display display = new Display();
        new ClientMain(display);
        display.dispose();
	}
	public ClientMain(Display display) {
        m_ClientShell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE));
        m_ClientShell.setText("ISTCG");
        
		m_UIList = new GameStateUI[GameState.values().length];
		m_UIList[GameState.LOGIN.ordinal()] = new LoginUI( m_ClientShell, this );
		m_UIList[GameState.MAIN.ordinal()] = new MainUI( m_ClientShell, this );
		m_UIList[GameState.NOCHANGE.ordinal()] = new NoChangeUI( );
        
        WindowSize( 800, 600 );
        CenterWindow();

        m_ClientShell.open();
        m_NextGameState = GameState.LOGIN;
        m_CurrentGameState = GameState.NOCHANGE;
        String data = "";
        while (!m_ClientShell.isDisposed()) {
          if (!display.readAndDispatch()) {
        	  if( m_Server != null ) {
        		  if (m_Server.hasData()) {
        			  String s = m_Server.getData();
        			  //System.out.println("Data: "+s);
        			  this.ParseMessage(s);
        		  }
        	  }
        	  
        	  switch( m_NextGameState ) {
        	  case LOGIN:
        		  m_UIList[m_CurrentGameState.ordinal()].Disable();
        		  m_CurrentGameState = GameState.LOGIN;
        		  
        		  m_UIList[m_NextGameState.ordinal()].Enable();
        		  m_NextGameState = GameState.NOCHANGE;
        		  
        		  break;
        	  case GAME:
        		  m_UIList[m_CurrentGameState.ordinal()].Disable();
        		  m_CurrentGameState = GameState.GAME;
        		  
        		  m_UIList[m_NextGameState.ordinal()].Enable();
        		  m_NextGameState = GameState.NOCHANGE;
        		  break;
        	  case MAIN:
        		  m_UIList[m_CurrentGameState.ordinal()].Disable();
        		  m_CurrentGameState = GameState.MAIN;
        		  m_UIList[m_NextGameState.ordinal()].Enable();
        		  //TESTING ABILITY TO GET MESSAGES
        		  this.appendText("Testing this text box");
        		  //END TESTING
        		  m_NextGameState = GameState.NOCHANGE;
        		  break;
       		  default:
       			  break;
        	  }
            display.sleep();
          }
        }
    }
	
	//***********************************
	// NETWORK
	//***********************************
	//private ConnectionDevice m_Server;
	private ThreadedConnectionDevice m_Server;
	public enum MessageType { CHAT, LOGIN_SUCCESS, LOGIN_FAILED; }
	
	private boolean MakeConnection() {
		int port = 4567;
  		String host = "127.0.0.1";
		try {
			//m_Server = new ConnectionDevice( host, port );
			m_Server = new ThreadedConnectionDevice( host, port);
			return true;
		} catch (IOException e) {
			((LoginUI)m_UIList[GameState.LOGIN.ordinal()]).setText("Failed to Connect!");
			return false;
		}
	}
	public void SendTextMessage( String target, String text ){
		text.replace(";", "\\;");
		m_Server.sendData( "SAY;" + target + ";" + text );
	}
	public void Login( String login, String password ) {
		if(MakeConnection())
			m_Server.sendData( "LOGIN;" + login + ";" + password );
	}
	public void ParseMessage( String input ) {
		String[] inputs = input.split(";");
		switch( MessageType.valueOf(inputs[0].toUpperCase()) ) {
		case CHAT:
			m_UIList[GameState.MAIN.ordinal()].HandleMessage( inputs );
			break;
		case LOGIN_SUCCESS:
			m_NextGameState = GameState.MAIN;
			break;
		case LOGIN_FAILED:
			m_UIList[GameState.LOGIN.ordinal()].HandleMessage( inputs );
			break;
		default:
			break;
		}
	}

	//***********************************
	// WINDOW
	//***********************************
	private Shell m_ClientShell;
	
	private void WindowSize( int x, int y ) {
        m_ClientShell.setSize(x, y);
		m_ClientShell.setSize( 
				2*x - m_ClientShell.getClientArea().width,
				2*y - m_ClientShell.getClientArea().height);		
	}
    private void CenterWindow( ) {
        Rectangle bds = m_ClientShell.getDisplay().getBounds();
        Point p = m_ClientShell.getSize();
        
        int nLeft = (bds.width - p.x) / 2;
        int nTop = (bds.height - p.y) / 2;
        
        m_ClientShell.setBounds(nLeft, nTop, p.x, p.y);
    }
    private void appendText(String s){
    	if(m_UIList[m_NextGameState.ordinal()] instanceof MainUI)
    		((MainUI)m_UIList[m_NextGameState.ordinal()]).appendMessage(s);
    }
}
