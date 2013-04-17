package client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import Shared.ClientMessages;

import client.guis.ClientGUI;
import client.guis.Lobby;
import client.guis.Login;
import client.network.InputObject;

public class ClientMain {
	private final static Point MINIMUM_WINDOW_SIZE = new Point( 700, 500 );

	private static Display m_Display = Display.getDefault();
	private static Shell m_Window;
	private static ClientGUI m_CurrentUI;
	private static Queue<InputObject> m_MessageQueue = new ConcurrentLinkedQueue<InputObject>();
	
	public static void main(String[] args) {
		//TODO Create widgets
		m_Window = new Shell( m_Display, SWT.SHELL_TRIM );
			m_Window.setMinimumSize( MINIMUM_WINDOW_SIZE );
			m_Window.setSize( MINIMUM_WINDOW_SIZE );
		
		//TODO Add widget listeners
		m_Window.addListener( SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if( m_CurrentUI != null ) { 
					m_CurrentUI.setBounds( m_Window.getClientArea() );
				}
			}
		});
		m_Window.addListener( SWT.Close, new Listener() {

			@Override
			public void handleEvent(Event event) {
				//TODO Close connection handler
				m_Window.dispose();
			}
			
		});
		
		m_CurrentUI = new Login( m_Window, SWT.NONE );
		m_Window.open();
		//TODO Threads here
		//TODO Main Loop
		while (!m_Window.isDisposed()) {
			InputObject next = m_MessageQueue.poll();
			if( m_CurrentUI != null && next != null ) {
				m_CurrentUI.HandleMessage( next.type, next.params );
			}
			if (!m_Display.readAndDispatch()) {
				m_Display.sleep();
			}
		}
	}

	public static void HandleMessage(ClientMessages valueOf, String[] input) {
		m_MessageQueue.add( new InputObject( valueOf, input ) );
	}

	public static void LogIn(Integer id) {
		m_CurrentUI.dispose();
		m_CurrentUI = new Lobby( m_Window, SWT.NONE, id );
		m_Window.setSize(1000, 600);
	}

}
