package AutoClient;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import AutoClient.*;
import Shared.ThreadedConnectionDevice;

public class AutoClientMain {
	//***********************************
	// MAIN
	//***********************************
	public enum GameState{ NOCHANGE, LOGIN, GAME, MAIN, STORE; }
	private GameStateUI[] m_UIList;
	private GameState m_NextGameState;
	private GameState m_CurrentGameState;
	private boolean canLogin = false;
	private boolean canGetMain = false;
	private boolean canTalk = false;
	private boolean canGame = false;
	private int stage = 0; //at login
	
	public static void main(String[] args) {
		Display display = new Display();
        new AutoClientMain(display);
        display.dispose();
	}
	public AutoClientMain(Display display) {
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
        		//Try to log in
        		  if(stage == 0){
        			  System.out.println("TRYING TO LOG IN...");
            		  this.Login("TestUser", "password");
            		  //move stage
            		  stage++;
        		  } else {
        			  //Something has gone wrong.
        			  System.err.println("I have reverted to the login screen this should not happen.");
        		  }
        		  
        		  break;
        	  case GAME:
        		  m_UIList[m_CurrentGameState.ordinal()].Disable();
        		  m_CurrentGameState = GameState.GAME;
        		  
        		  m_UIList[m_NextGameState.ordinal()].Enable();
        		  m_NextGameState = GameState.NOCHANGE;
        		  if(stage == 2){
        			  System.out.println("MAIN WORKS.");
        			  canGetMain=true;
        		  }
        		  break;
        	  case MAIN:
        		  m_UIList[m_CurrentGameState.ordinal()].Disable();
        		  m_CurrentGameState = GameState.MAIN;
        		  m_UIList[m_NextGameState.ordinal()].Enable();
        		  //TESTING ABILITY TO GET MESSAGES
        		  //this.appendText("Testing this text box");
        		  //END TESTING
        		  m_NextGameState = GameState.NOCHANGE;
        		//try to confirm this is the main
        		  if(stage!=0){
        			  //We have logged in
        			  canLogin=true;
        		  }
        		  if(stage == 1){
        			  System.out.println("WE ARE IN THE MAIN");
        			  this.SendTextMessage("TESTING THAT I, THE AUTOCLIENT CAN TALK");
        			  //for loop for some stall until server can respond
        			  int t = 0; //T cycles
        			  while(((MainUI)this.m_UIList[m_NextGameState.MAIN.ordinal()]).getMessages().contains("TESTING THAT I")){
        				  t = (2*t+2);
        				  t/=2;
        				  if(t==Integer.MAX_VALUE)
        					  break;
        			  }
        			  System.out.println("T cycles to talk: "+t);
        			  canTalk=true;
        			  //change to game
        			  stage++;
        			  this.changeState(GameState.GAME);
        		  }
        		  else {
        			  //Something has gone wrong
        			  System.err.println("Error in Main");
        		  }
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
        	  /*
        	   * Attempt login test
        	   *
        	   */
            display.sleep();
          }
        }
    }
	
	//***********************************
	// NETWORK
	//***********************************
	//private ConnectionDevice m_Server;
	private ThreadedConnectionDevice m_Server;
	public enum MessageType { SAY, LOGIN_SUCCESS, LOGIN_FAILED, LOGGED_IN_MESSAGE, ALREADY_LOGGED_IN, NOPE, PLAYERJOINED, JOINED; }
	
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
}
