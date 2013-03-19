package NewClient;

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class Login extends Composite {
	private Text text_user_name;
	private Text text_password;
	private Label label_login_status;
	Button btnLogin;
	boolean server = true;
	final ClientMain m_Host;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Login(Composite parent, int style, ClientMain main) {
		super(parent, style);
		m_Host = main;
		
		
		text_user_name = new Text(this, SWT.BORDER);
		text_user_name.addKeyListener( new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) { /*DO NOTHING*/ }
			@Override
			public void keyReleased(KeyEvent arg0) {
				if( arg0.character == SWT.CR && server) {
					Event e = new Event();
					btnLogin.notifyListeners( SWT.Selection, e );
				}
			}
		});
		text_user_name.setBounds(174, 85, 152, 31);
		
		text_password = new Text( this, SWT.BORDER|SWT.PASSWORD );
		text_password.addKeyListener( new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) { /*DO NOTHING*/ }
			@Override
			public void keyReleased(KeyEvent arg0) {
				if( arg0.character == SWT.CR && server) {
					Event e = new Event();
					btnLogin.notifyListeners( SWT.Selection, e );
				}
			}
		});
		text_password.setBounds(174, 128, 152, 31);
		
		final Label lblUsername = new Label(this, SWT.NONE);
		lblUsername.setBounds(76, 85, 72, 31);
		lblUsername.setText("Username:");
		
		final Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setText("Password:");
		lblPassword.setBounds(76, 128, 72, 31);
		label_login_status = new Label(this, SWT.CENTER);
		label_login_status.setBounds(174, 208, 200, 20);
		label_login_status.setText("Please login");
		btnLogin = new Button(this, SWT.PUSH);
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String login = lblUsername.getText();
				String password = lblPassword.getText();
				
				if( getUsername().getText().equals("") ) {
					label_login_status.setText( "Enter a username." );
					lblUsername.setFocus();
					return;
				}
				if( getPassword().getText().equals("") ) {
					label_login_status.setText( "Enter a password." );
					lblPassword.setFocus();
					return;
				}
				m_Host.login( login, password );
			
			}
		});
		btnLogin.setBounds(156, 174, 94, 28);
		btnLogin.setText("Login");
		
		this.addListener( SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Rectangle bounds = m_Host.rShell().getClientArea();
				text_user_name.setBounds( (bounds.width / 2) - 65, (bounds.height / 2) - 55, 190, 22 );
				lblUsername.setBounds( (bounds.width / 2) - 125, (bounds.height / 2) - 53, 55, 22 );
				text_password.setBounds( (bounds.width / 2) - 65, (bounds.height / 2) -25, 190, 22 );
				lblPassword.setBounds( (bounds.width / 2) - 125, (bounds.height / 2) -23, 55, 22 );
				btnLogin.setBounds( (bounds.width / 2) - 40, (bounds.height / 2) + 5, 80, 22 );
				label_login_status.setBounds( 0, (bounds.height / 2) + 35, bounds.width, 22 );
			}
			
		});
		

	}

	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public Text getUsername() {
		return text_user_name;
	}
	public Text getPassword() {
		return text_password;
	}
	public void setLabel(String s){
		this.label_login_status.setText(s);
	}
	public void disableLogin(){
		btnLogin.setEnabled(false);
		server=false;
	}
}
