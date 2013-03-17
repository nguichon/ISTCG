package NewClient;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import Client.GameUI;
import Client.LoginUI;
import Client.ClientMain.GameState;
import Client.ClientMain.MessageType;
import Shared.ThreadedConnectionDevice;

public class ClientMain {

	private Display display = null;
	private Login Login = null;
	private Lobby Lobby = null;
	private Shell shell = null;
	private Composite composite = null;
	private String ID = "";
	public ClientMain(){
		
		display = Display.getDefault();
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		Login = new Login(shell, SWT.NONE,this );
		Lobby = new Lobby(shell, SWT.NONE,this);
		composite = Login;
		composite.setBounds(shell.getBounds());
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
	public enum MessageType { JOIN,PLAYER_JOINED,UPDATE_ZONE,CREATE_TEMPLATE,REMOVE_TEMPLATE,CREATE_INSTANCE,UPDATE_INSTANCE,DELETE_INSTANCE,PRIVATE_MESSAGE,MESSAGE,LOGIN_FAILED,LOGIN_SUCCESS,USER_LOGGED_IN,SERVER,PLAYER_TURN }
	
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
			s = text.substring(0, 6);
			s = s.substring(0,5);
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
		if(MakeConnection())
			m_Server.sendData( "LOGIN;" + login + ";" + password );
	}
	public void ParseMessage( String input ) {
		//System.out.println(input);
		String[] inputs = input.split(";");
		switch( MessageType.valueOf(inputs[0].toUpperCase()) ) {
		case MESSAGE:
			if(composite instanceof Lobby){
			((Lobby)(composite)).addMessageBox(inputs[1]+": "+inputs[2]);
			}
			break;
		case LOGIN_SUCCESS:
			this.ID=inputs[1];
			composite.dispose();
			shell.setBounds(display.getPrimaryMonitor().getBounds());
			composite = new Lobby(shell, SWT.NONE,this);
			Rectangle r = shell.getBounds();
			composite.setBounds(r);
			break;
		case LOGIN_FAILED:
			//ERROR
			if(composite instanceof Login){
				((Login)(composite)).setLabel("LOGIN FAILED");
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
		case CREATE_TEMPLATE:
			//WAITING ON DAN
			break;
		case REMOVE_TEMPLATE:
			//WAITING ON DAN
			break;
		case CREATE_INSTANCE:
			//WAINTING ON DAN
			break;
		case UPDATE_INSTANCE:
			//WAITING ON DAN
			break;
		case DELETE_INSTANCE:
			//WAITING ON DAN
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
}
