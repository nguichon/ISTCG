package OldClient;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import Shared.ThreadedConnectionDevice;

public class ClientMain {
	//***********************************
	// MAIN
	//***********************************
	public enum GameState{ NOCHANGE, LOGIN, GAME, MAIN, STORE; }
	private GameStateUI[] m_UIList;
	private GameState m_NextGameState;
	private GameState m_CurrentGameState;
	private Display display;
	private int ID = 0;
	public static void main(String[] args) {
		Display display = new Display();
        new ClientMain(display);
        display.dispose();
	}
	public ClientMain(Display display) {
		this.display=display;
        m_ClientShell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE));
        m_ClientShell.setText("ISTCG");
        
		m_UIList = new GameStateUI[GameState.values().length];
		m_UIList[GameState.LOGIN.ordinal()] = new LoginUI( m_ClientShell, this );
		m_UIList[GameState.MAIN.ordinal()] = new MainUI( m_ClientShell, this );
		m_UIList[GameState.NOCHANGE.ordinal()] = new NoChangeUI( );
		m_UIList[GameState.STORE.ordinal()] = new StoreUI(m_ClientShell, this);
        m_UIList[GameState.GAME.ordinal()] = new GameUI(m_ClientShell, this);
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
        		  //this.appendText("Testing this text box");
        		  //END TESTING
        		  m_NextGameState = GameState.NOCHANGE;
        		  break;
        		  
        	  case STORE:
        		  m_UIList[m_CurrentGameState.ordinal()].Disable();
        		  m_CurrentGameState = GameState.STORE;
        		  m_UIList[m_NextGameState.ordinal()].Enable();
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
	public enum MessageType { SAY, LOGIN_SUCCESS, LOGIN_FAILED, LOGGED_IN_MESSAGE, ALREADY_LOGGED_IN, NOPE, PLAYERJOINED, JOIN, UPDATE, ADDCARD, REMOVECARD, CREATECARD; }
	
	private boolean MakeConnection() {
		int port = 4567;
  		String host = "192.168.0.21";
		try {
			//m_Server = new ConnectionDevice( host, port );
			m_Server = new ThreadedConnectionDevice( host, port);
			return true;
		} catch (IOException e) {
			((LoginUI)m_UIList[GameState.LOGIN.ordinal()]).setText("Failed to Connect!");
			return false;
		}
	}
	public void SendTextMessage( String text ){
		text.replace(";", ":");
		String s = "";
		try {
			s = text.substring(0, 6);
			s = s.substring(0,5);
		} catch (Exception e){
			
		}
		
		//System.out.println(s);
		if(s.equals("/tell"))
			m_Server.sendData( "TELL;" + text.substring(6) );
		else if(s.equals("/chal")){
			m_Server.sendData( "CHALLENGE;"+text.substring(6));
		}
		else
			m_Server.sendData( "SAY;" + text );
	}
	public void Login( String login, String password ) {
		if(MakeConnection())
			m_Server.sendData( "LOGIN;" + login + ";" + password );
	}
	public void ParseMessage( String input ) {
		String[] inputs = input.split(";");
		switch( MessageType.valueOf(inputs[0].toUpperCase()) ) {
		case LOGGED_IN_MESSAGE:
		case SAY:
			m_UIList[GameState.MAIN.ordinal()].HandleMessage( inputs );
			break;
		case LOGIN_SUCCESS:
			m_NextGameState = GameState.MAIN;
			ID = Integer.valueOf(inputs[1]);
			break;
		case LOGIN_FAILED:
			m_UIList[GameState.LOGIN.ordinal()].HandleMessage( inputs );
			break;
		case JOIN:
			m_NextGameState = GameState.GAME;
			break;
		case PLAYERJOINED:
			((GameUI)m_UIList[GameState.GAME.ordinal()]).initializeGame(inputs[3],Integer.valueOf(inputs[2]), Integer.valueOf(inputs[1]));
			break;
		case UPDATE:
			System.out.println(input);
			((GameUI)m_UIList[GameState.GAME.ordinal()]).updateZone(inputs[2],inputs[3],inputs[4]);
			break;
		case ADDCARD:
			((GameUI)m_UIList[GameState.GAME.ordinal()]).addCard(inputs[2],inputs[4],inputs[3]);
			break;
		case REMOVECARD:
			
			break;
		case CREATECARD:
			
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
    public Point getBounds() {
    	return m_ClientShell.getSize();
    }
    private void appendText(String s){
    	if(m_UIList[m_NextGameState.ordinal()] instanceof MainUI)
    		((MainUI)m_UIList[m_NextGameState.ordinal()]).appendMessage(s);
    }
    
    //****************************
    // CHANGE STATE
    //****************************
    
    public void changeState(GameState g){
    	m_NextGameState = g;
    }
    //TESTING METHODS
    public boolean isLoggedIn(){
    	return ((m_UIList[m_NextGameState.ordinal()] instanceof MainUI)?true:false);
    	
    }
    public String getTextContents() {
    	if(isLoggedIn()){
    		return ((MainUI)m_UIList[m_NextGameState.ordinal()]).getMessages();
    	}
    	return "";
    }
    public void setBounds(Point p){
    	m_ClientShell.setSize(p);
    }
    public void sendData(String s){
    	m_Server.sendData(s);
    }
    public int getID(){
    	return ID;
    }
    public Display getDisplay(){
    	return this.display;
    }
}
