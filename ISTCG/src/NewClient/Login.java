package NewClient;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

public class Login extends Composite {
	private Text text;
	private Text text_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Login(Composite parent, int style, ClientMain main) {
		super(parent, style);
		final ClientMain m_Host = main;
		text = new Text(this, SWT.BORDER);
		text.setBounds(174, 85, 152, 31);
		
		text_1 = new Text(this, SWT.BORDER);
		text_1.setBounds(174, 128, 152, 31);
		
		final Label lblUsername = new Label(this, SWT.NONE);
		lblUsername.setBounds(76, 85, 72, 31);
		lblUsername.setText("Username:");
		
		final Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setText("Password:");
		lblPassword.setBounds(76, 128, 72, 31);
		
		Button btnLogin = new Button(this, SWT.NONE);
		btnLogin.setBounds(156, 174, 94, 28);
		btnLogin.setText("Login");
		
		final Label label = new Label(this, SWT.NONE);
		label.setBounds(174, 208, 59, 14);
		btnLogin.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				String login = lblUsername.getText();
				String password = lblPassword.getText();
				
				if( login.equals("") ) {
					label.setText( "Enter a username." );
					lblUsername.setFocus();
					return;
				}
				if( password.equals("") ) {
					label.setText( "Enter a password." );
					lblPassword.setFocus();
					return;
				}
				m_Host.login( login, password );
			}
			
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
