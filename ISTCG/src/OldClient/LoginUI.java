package OldClient;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import NewClient.ClientCardTemplate;
import NewClient.ClientCardTemplateManager;
import OldClient.ClientMain.MessageType;

public class LoginUI extends GameStateUI {
	Label m_Error;
	Label labelError = null;
	public LoginUI( Shell client, ClientMain main ) {
		m_Host = main;
		m_UIObjects = new Vector<Control>();

		ImageManager.get().Initialize( client.getDisplay() );
		ClientCardTemplateManager.get().Initialize();
		client.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				ClientCardTemplateManager.get().GetClientCardTemplate( 2 ).Render( e.gc, ClientCardTemplate.CardRenderSize.MEDIUM, null );
				
			}
			
		});
		
		final Button loginButton = new Button(client, SWT.PUSH);
		loginButton.setBounds( 350, 307, 100, 25 );
		loginButton.setText("Login");
		labelError = new Label( client, SWT.READ_ONLY );
		labelError.setBounds( 0, 332, 800, 25 );
		labelError.setForeground( client.getDisplay().getSystemColor(SWT.COLOR_RED) );
		labelError.setAlignment( SWT.CENTER );
		
		final Text userName = new Text( client, SWT.BORDER );
		Label labelName = new Label( client, SWT.READ_ONLY );
		userName.setBounds(310,261,250,22);
		labelName.setBounds(240,263,70,22);
		labelName.setText("Username: ");
		
		final Text passwordBox = new Text( client, SWT.BORDER|SWT.PASSWORD );
		Label labelPass = new Label( client, SWT.READ_ONLY );
		passwordBox.setBounds(310,283,250,22);
		labelPass.setBounds(240,285,70,22);
		labelPass.setText("Password: ");
		
		loginButton.addListener( SWT.Selection ,  new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				String login = userName.getText();
				String password = passwordBox.getText();
				
				if( login.equals("") ) {
					labelError.setText( "Enter a username." );
					userName.setFocus();
					return;
				}
				if( password.equals("") ) {
					labelError.setText( "Enter a password." );
					passwordBox.setFocus();
					return;
				}
				m_Host.Login( login, password );
			}
		});
		userName.addKeyListener( new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) { /*DO NOTHING*/ }
			@Override
			public void keyReleased(KeyEvent arg0) {
				if( arg0.character == SWT.CR ) {
					passwordBox.setFocus();
				}
			}
		});
		passwordBox.addKeyListener( new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) { /*DO NOTHING*/ }
			@Override
			public void keyReleased(KeyEvent arg0) {
				if( arg0.character == SWT.CR ) {
					Event e = new Event();
					loginButton.notifyListeners( SWT.Selection, e );
				}
			}
		});
		
		m_UIObjects.add(labelPass);
		m_UIObjects.add(labelName);
		m_UIObjects.add(passwordBox);
		m_UIObjects.add(userName);
		m_UIObjects.add(loginButton);
		m_UIObjects.add(labelError);
		
		m_Error = labelError;
		
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
		case LOGIN_FAILED:
			m_Error.setText( "Login has failed. "+inputs[1] );
			break;
		default:
			break;
		}
	}
	public void setText(String s){
		labelError.setText(s);
	}
}
