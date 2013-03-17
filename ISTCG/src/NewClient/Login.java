package NewClient;

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
	private Text text;
	private Text text_1;
	private Label label;
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
		text = new Text(this, SWT.BORDER);
		text.addKeyListener( new KeyListener() {
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
		text.setBounds(174, 85, 152, 31);
		
		text_1 = new Text( this, SWT.BORDER|SWT.PASSWORD );
		text_1.addKeyListener( new KeyListener() {
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
		text_1.setBounds(174, 128, 152, 31);
		
		final Label lblUsername = new Label(this, SWT.NONE);
		lblUsername.setBounds(76, 85, 72, 31);
		lblUsername.setText("Username:");
		
		final Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setText("Password:");
		lblPassword.setBounds(76, 128, 72, 31);
		label = new Label(this, SWT.NONE);
		label.setBounds(174, 208, 200, 20);
		label.setText("Please login");
		btnLogin = new Button(this, SWT.PUSH);
		btnLogin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String login = lblUsername.getText();
				String password = lblPassword.getText();
				
				if( getUsername().getText().equals("") ) {
					label.setText( "Enter a username." );
					lblUsername.setFocus();
					return;
				}
				if( getPassword().getText().equals("") ) {
					label.setText( "Enter a password." );
					lblPassword.setFocus();
					return;
				}
				m_Host.login( login, password );
			
			}
		});
		btnLogin.setBounds(156, 174, 94, 28);
		btnLogin.setText("Login");
		
		

	}

	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public Text getUsername() {
		return text;
	}
	public Text getPassword() {
		return text_1;
	}
	public void setLabel(String s){
		this.label.setText(s);
	}
	public void disableLogin(){
		btnLogin.setEnabled(false);
		server=false;
	}
}
