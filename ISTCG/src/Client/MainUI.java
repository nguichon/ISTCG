package Client;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import Client.ClientMain.GameState;
import Client.ClientMain.MessageType;

public class MainUI extends GameStateUI {
	private StyledText m_Messages;
	
	public MainUI( Shell client, final ClientMain main ) {
		m_Host = main;
		m_UIObjects = new Vector<Control>();

		
		final StyledText messages = new StyledText( client, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL );
		final Text sendMessageText = new Text( client, SWT.BORDER | SWT.WRAP | SWT.SINGLE );
		final Button sendMessageButton = new Button( client, SWT.PUSH );
		messages.setBounds(500,0,300,550);
		sendMessageText.setBounds(500,550,240,50);
		sendMessageButton.setBounds(740,550,60,50);
		final Label advert = new Label(client, SWT.READ_ONLY);
		advert.setBounds(0, 0, 500, 550);
		advert.setText("THIS WILL BE STORE ADS AND NEWS");
		advert.addMouseListener(new MouseListener(){

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent arg0) {
				// TODO Auto-generated method stub
				//main.changeState(GameState.STORE);
				main.changeState(GameState.GAME);
			}
			
		});
		
		sendMessageButton.setText( "Send" );
		
		sendMessageText.addKeyListener( new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) { /*DO NOTHING*/ }
			@Override
			public void keyReleased(KeyEvent arg0) {
				if( arg0.character == SWT.CR ) {
					Event e = new Event();
					sendMessageButton.notifyListeners( SWT.Selection, e );
				}
			}
		});
		sendMessageButton.addListener( SWT.Selection ,  new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				String textToSend = sendMessageText.getText();
				
				if( !textToSend.equals( "" ) ) {
					m_Host.SendTextMessage( textToSend );
					sendMessageText.setText("");
				}
				
				//sendMessageText.setText( "Welcome to this TCG Server!" );
				sendMessageText.setFocus();
			}
		});
		messages.addListener(SWT.Modify, new Listener(){
		    public void handleEvent(Event e){
		        messages.setTopIndex(messages.getLineCount() - 1);
		    }
		});
		
		m_UIObjects.add(messages);
		m_UIObjects.add(sendMessageText);
		m_UIObjects.add(sendMessageButton);
		m_UIObjects.add(advert);
		
		
		m_Messages = messages;
		m_Messages.setText( "" );
		
		for( Control w : m_UIObjects ) {
			w.setVisible(false);
		}
	}
	@Override
	public void Disable() {
		for( Control w : m_UIObjects ) {
			w.setVisible(false);
		}
	}

	@Override
	public void Enable() {
		for( Control w : m_UIObjects ) {
			w.setVisible(true);
		}
	}
	@Override
	public void HandleMessage(String[] inputs) {
		switch( MessageType.valueOf(inputs[0].toUpperCase()) ) {
		case SAY:
			m_Messages.setText( m_Messages.getText() + "\n" + inputs[1] + ":  " + inputs[2] );
			break;
		case LOGGED_IN_MESSAGE:
			m_Messages.setText( m_Messages.getText() + "\n" + inputs[1] + " logged in.");
		default:
			break;
		}
	}
	public void appendMessage(String s) {
		m_Messages.setText(m_Messages.getText()+"\n"+s);
	}
	public String getMessages(){
		return m_Messages.getText();
	}
}
