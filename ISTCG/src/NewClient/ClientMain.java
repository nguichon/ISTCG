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
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
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
	public enum MessageType { SAY, LOGIN_SUCCESS, LOGIN_FAILED, LOGGED_IN_MESSAGE, ALREADY_LOGGED_IN, NOPE, PLAYERJOINED, JOIN, UPDATE, ADDCARD, REMOVECARD, CREATECARD; }
	
	private boolean MakeConnection() {
		int port = 4567;
  		String host = "127.0.0.1";
		try {
			//m_Server = new ConnectionDevice( host, port );
			m_Server = new ThreadedConnectionDevice( host, port);
			return true;
		} catch (IOException e) {
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
			
			break;
		case LOGIN_SUCCESS:
			this.ID=inputs[1];
			shell.setBounds(display.getPrimaryMonitor().getBounds());
			composite = new Lobby(shell, SWT.NONE,this);
			Rectangle r = shell.getBounds();
			composite.setBounds(r);
			break;
		case LOGIN_FAILED:
			//ERROR
			break;
		case JOIN:
			
			break;
		case PLAYERJOINED:
			
			break;
		case UPDATE:
			
			break;
		case ADDCARD:
			
			break;
		case REMOVECARD:
			
			break;
		case CREATECARD:
			
			break;
		default:
			break;
		}
	}
	public void login(String user, String password){
		
		String u = ((Login)composite).getUsername().getText();
		String p = ((Login)composite).getPassword().getText();
		
		if(!u.isEmpty()&&!p.isEmpty()){
			if(this.MakeConnection()){
			m_Server.sendData("login;"+u+";"+p);
			composite.dispose();
			}
			
			
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
