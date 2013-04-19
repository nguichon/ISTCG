package NewClient;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import NewClient.games.GameV2;
import OldClient.CardTemplateManager;
import OldClient.ImageManager;
import Shared.ClientResponses;
import Shared.ThreadedConnectionDevice;
//import server.games.cards.ServerCardTemplateManager;

public class ClientMain {

	private Display display = null;
	private Login Login = null;
	//private Lobby Lobby = null;
	private Shell shell = null;
	private Composite composite = null;
	private String ID = "";
	String deck = "";
	public ClientMain(){
		CardTemplateManager.get().Initialize();
		//ClientCardTemplateManager.get().Initialize();
		display = Display.getDefault();
		ImageManager.get().Initialize(display);
		
		ClientCardTemplateManager.get().Initialize();
		shell = new Shell( SWT.NO_REDRAW_RESIZE | SWT.SHELL_TRIM );
		shell.setSize(450, 300);
		shell.setMinimumSize( 450, 300 );
		shell.setText("Intersteller TCG");
		//shell.setBackgroundImage( ImageManager.get().GetImage( "Client_BG.png" ) );
		shell.setBackgroundMode( SWT.INHERIT_DEFAULT );
		Login = new Login(shell, SWT.NONE, this );
		//Lobby = new Lobby(shell, SWT.NONE,this);
		composite = Login;
		composite.setBounds(shell.getClientArea());
		
		shell.addDisposeListener( new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				m_Server.sendData( ClientResponses.LOGOUT.name() );
				System.exit( 0 );
			}
			
		});
		shell.addListener( SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
					Image toDraw = ImageManager.get().GetImage( "Client_BG.png" );
					Image new_bg = new Image(display, shell.getClientArea().width,  shell.getClientArea().height);
					GC gc = new GC(new_bg);
					
					int sh = new_bg.getBounds().height;
					int sw = new_bg.getBounds().width;
					int dh = toDraw.getBounds().height;
					int dw = toDraw.getBounds().width;
					
					int source_x = 0;
					int source_y = 0;
					int source_width = sw;
					int source_height = sh;
					
					double aspect_w = (double) dw / sw;
					double aspect_h = (double) dh / sh;
	
					double selected_aspect = Math.min( aspect_w, aspect_h );
					source_x = (int) ((dw - (dw / selected_aspect)) / 2);
					source_y = (int) ((dh - (dh / selected_aspect)) / 2);
					
					
					gc.drawImage( toDraw,
									0 + (int)((dw - (source_width * selected_aspect)) / 2),
									0 + (int)((dh - (source_height * selected_aspect)) / 2),
									(int)(source_width * selected_aspect),
									(int)(source_height * selected_aspect),
									0,
									0,
									sw,
									sh);
					
					shell.setBackgroundImage( new_bg );
					gc.dispose();
				if( !composite.isDisposed() ) {
					composite.setBounds(shell.getClientArea());
				}
			}
			
		});
		shell.pack();
		shell.open();
		shell.layout();
		this.MakeConnection();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				if(m_Server!=null && m_Server.hasData()){
					this.ParseMessage(m_Server.getData());
				}
				//display.sleep();
			}
		}
		try {
			m_Server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	public Composite rShell(){
		return shell;
	}
	
	public String getPID(){
		return ID;
	}
	public Point getSize(){
		return display.getActiveShell().getSize();
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		new ClientMain();
	}
	public Rectangle getBounds(){
		return shell.getBounds();
	}
	private ThreadedConnectionDevice m_Server;
	
	private boolean MakeConnection() {
		int port = 4567;
  		String host = Settings.get().getString( "ip" );
  		System.out.println( host );
		try {
			//m_Server = new ConnectionDevice( host, port );
			m_Server = new ThreadedConnectionDevice( host, port);
			return true;
		} catch (IOException e) {
			if(composite instanceof Login)
				((Login)composite).setLabel("NO SERVER FOUND");
				((Login)composite).disableLogin();
			return false;
		}
	}
	public void SendTextMessage( String text ){
		text.replace(";", ":");
		String s = "";
		try {
			if(text.length()>=5)
			s = text.substring(0,5);
			if(s.equals("/tell")){
				m_Server.sendData( "TELL;" + text.substring(6) );
			} else if(s.equals("/chal")){
				m_Server.sendData( "CHALLENGE;"+text.substring(6));
			} 
			else {
				m_Server.sendData( "SAY;" + text );
			}
		} catch (Exception e){
			
		}
		
		
	}
	public void sendData(String s){
		//s.replace(";",":");
		try{
			m_Server.sendData(s);
		} catch(Exception e){
			
		}
	}
	public void Login( String login, String password ) {
		shell.setText( "Coils TCG - " + login );
		if(MakeConnection())
			m_Server.sendData( "LOGIN;" + login + ";" + password );
	}
	public void ParseMessage( String input ) {
		//System.out.println( "Magic man! - " + input );
		System.out.println("INPUT RECEIVED: " + input);
		String[] inputs = input.split(";");
		switch( Shared.ClientMessages.valueOf(inputs[0].toUpperCase()) ) {
		case MESSAGE:
			if(composite instanceof Lobby){
			((Lobby)(composite)).addMessageBox(inputs[1]+": "+inputs[2]);
			}
			break;
		case LOGIN_SUCCESS:
			this.ID=inputs[1];
			composite.dispose();
			Rectangle shell_size = new Rectangle(50, 50, -100, -100);
			Rectangle window_size = display.getPrimaryMonitor().getBounds();
			shell_size.width += window_size.width;
			shell_size.height += window_size.height;
			shell.setBounds( shell_size );
			composite = new Lobby(shell, SWT.NONE,this);
			//shell.setBackgroundImage( null );
			((Lobby)composite).addDeckEditor();
			((Lobby)composite).addStore();
			Rectangle r = shell.getClientArea();
			composite.setBounds(r);
			break;
		case LOGIN_FAILED:
			//ERROR
			if(composite instanceof Login){
				((Login)(composite)).setLabel("LOGIN FAILED: " + inputs[1]);
			}
			break;
		case JOIN:
			//check the lobby for games
			if(composite instanceof Lobby)
				if(((Lobby)composite).findGameById( Integer.valueOf(inputs[1]) )!=null){
					//WUT
				} else {
					((Lobby)composite).addGame(inputs[1]);
					((Lobby)composite).getDeckEditor().endMatchFinding();
				}
			
			break;
		case COLLECTION:
			if(composite instanceof Lobby){
				if(((Lobby)composite).getDeckEditor()!=null)
					((Lobby)composite).getDeckEditor().addCollection(inputs.length>1?inputs[1]:"");
				else
					this.sendData("GETCOLLECTION");
			} break;
		case USER_LOGGED_IN:
			if(composite instanceof Lobby){
				((Lobby)(composite)).addMessageBox(inputs[1]+" has logged in.");
				}
			break;
		case SERVER:
			 break;
		case ACCOUNT_CREATION_STATUS:
			if(composite instanceof Login){
				if( Integer.valueOf( inputs[1] ) == -1 )
					((Login)composite).CreationFailed();
			}
			break;
		case PRIVATE_MESSAGE:
			if(composite instanceof Lobby){
				((Lobby)(composite)).addMessageBox(inputs[1]+": "+inputs[2]);
				}
			break;
		case PLAYER_JOINED: //Fall through
		case UPDATE_ZONE: //Fall through
		case PLAYER_TURN: //Fall through
		case WAITING: //Fall through
		case UPDATE_PLAYER: //Fall through
		case MOVE: //Fall through
		case HIDE: //Fall through
		case CARD_INFO: //Fall through
		case GAME_ERROR: //Fall through
		case GAME_STATE: //Fall through
		case PLAYER_STATE: //Fall through
		case STACK_OBJECT: //Fall through
		case REMOVE_STACK_OBJECT: //Fall through
		case GAME_RESULT: //Fall through
		case SET_CARD_DAMAGE: //Fall through
		case UNIT_ACTIVE_STATE:
			if( Lobby.class.isInstance( composite ) ) {
				GameV2 receiverGame = ((Lobby) composite).findGameById( Integer.valueOf( inputs[1] ) );
				if( receiverGame != null ) {
					receiverGame.HandleMessage( inputs );
				}
			}
			break;
		default:
			break;
		}
	}
	public void login(String user, String password){
		
		String u = ((Login)composite).getUsername().getText();
		String p = ((Login)composite).getPassword().getText();
		
		if(!u.isEmpty()&&!p.isEmpty()){
			
			m_Server.sendData("login;"+u+";"+p);
			
			
			
			
		}
		else {
			//ERROR
		}
		/*
		 * For now just move to next UI
		 * 
		 */
		
		
		
		
		
		//shell.pack();
		//shell.open();
		//shell.layout();
	}
	public String getDeck() {
		
		return deck;
	}
	public void createAccount(String login, String password) {
		if(MakeConnection())
			m_Server.sendData( "CREATE_ACCOUNT;" + login + ";" + password + ";none@void.com" );
	}
	public void closeTab(int m_GameID) {
		if(composite instanceof Lobby){
			if(((Lobby)composite).findGameById(m_GameID)!=null)
				((Lobby)composite).findGameById(m_GameID).m_Host.dispose();
		}
		
	}
}
