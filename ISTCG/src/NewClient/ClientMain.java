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
		shell.setBackgroundImage( ImageManager.get().GetImage( "Client_BG.png" ) );
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
				display.sleep();
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
  		String host = "127.0.0.1";
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
		shell.setText( "Intersteller TCG - " + login );
		if(MakeConnection())
			m_Server.sendData( "LOGIN;" + login + ";" + password );
	}
	public void ParseMessage( String input ) {
		//System.out.println(input);
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
				if(((Lobby)composite).findGameById(inputs[1])!=null){
					//WUT
				} else {
					((Lobby)composite).addGame(inputs[1]);
				}
			break;
		case PLAYER_JOINED:
			if(composite instanceof Lobby)
			if(((Lobby)composite).findGameById(inputs[1])!=null){
				((Lobby)composite).findGameById(inputs[1]).setEnemy(inputs[2]);
				((Lobby)composite).findGameById(inputs[1]).setEnemyID(inputs[3]);
			} else {
				//Wut
			}
			break;
		case UPDATE_ZONE:
			if(composite instanceof Lobby)
				if(((Lobby)composite).findGameById(inputs[1])!=null){
					((Lobby)composite).findGameById(inputs[1]).updateZone(inputs[2], inputs[3], inputs[4]);
				} else {
					//Wut
				}
			break;
		case PRIVATE_MESSAGE:
			if(composite instanceof Lobby){
				((Lobby)(composite)).addMessageBox(inputs[1]+": "+inputs[2]);
				}
			break;
		case USER_LOGGED_IN:
			if(composite instanceof Lobby){
				((Lobby)(composite)).addMessageBox(inputs[1]+" has logged in.");
				}
			break;
		case SERVER:
			break;
		case PLAYER_TURN:
			if(composite instanceof Lobby)
				if(((Lobby)composite).findGameById(inputs[1])!=null){
					((Lobby)composite).findGameById(inputs[1]).setTurn(inputs[2]);
				} else {
					//Wut
				}
			break;
		case WAITING:
			if(composite instanceof Lobby){
				if(((Lobby)composite).findGameById(inputs[1])!=null){
					((Lobby)composite).findGameById(inputs[1]).enablePass();
				}
			}
		case UPDATE_PLAYER:
			if(composite instanceof Lobby){
				if(((Lobby)composite).findGameById(inputs[1])!=null){
					((Lobby)composite).findGameById(inputs[1]).setResource(inputs[2],inputs[3],inputs[4]);
				}
			}
			break;
		case MOVE:
			if(composite instanceof Lobby){
				((Lobby)composite).findGameById(inputs[1]).moveCard(inputs[2], inputs[3]);
			}
			break;
		case CARD_INFO:
			System.out.println("noooo");
			if(composite instanceof Lobby){
				((Lobby)composite).findGameById(inputs[1]).setCard(inputs[3], inputs[2],inputs[4],inputs[5]);
			}
			break;
		case GAME_ERROR:
			System.err.println("Game error in game "+inputs[1]+". Error: "+inputs[2]); break;
		case PLAYER_STATE:
			System.out.print( "Magic man! - " );
			for( String s : inputs ) {
				System.out.print( s + "..." );
			}
			System.out.println();
			if(composite instanceof Lobby){
				if(((Lobby)composite).findGameById(inputs[1])!=null){
					((Lobby)composite).findGameById(inputs[1]).setPlayerState(inputs[2],inputs[3]);
				}
			}
			break;
		case COLLECTION:
			if(composite instanceof Lobby){
				
				
			} break;
		case STACK_OBJECT:
			if(composite instanceof Lobby){
				if(((Lobby)composite).findGameById(inputs[1])!=null){
					
					
					((Lobby)composite).findGameById(inputs[1]).addStack(inputs[2],Arrays.copyOfRange(inputs,3,inputs.length-1));
				}
				
			} break;
		case REMOVE_STACK_OBJECT:
			System.out.println("REMOVE STACK OBJECT");
			if(composite instanceof Lobby){
				if(((Lobby)composite).findGameById(inputs[1])!=null){
					
					//((Lobby)composite).findGameById(inputs[1]).addStack(inputs[2],)
				}
				
			} break;
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
}
