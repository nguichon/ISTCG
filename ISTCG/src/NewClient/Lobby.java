package NewClient;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

public class Lobby extends Composite {
	private Text text;
	private Text text_1;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public static String newline = String.valueOf(new char[]{SWT.CR});
	public Lobby(Composite parent, int style, ClientMain main) {
		super(parent, style);
		
		text = new Text(this, SWT.BORDER | SWT.READ_ONLY|SWT.WRAP|SWT.MULTI|SWT.V_SCROLL);
		text.setBounds(main.getBounds().width-500,main.getBounds().height-800, 250, 700);
		
		final Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				//onClick
				addMessageBox(getPlayerMessage().getText());
				getPlayerMessage().setText("");
			}
		});
		btnNewButton.setBounds(main.getBounds().width-500, main.getBounds().height-100, 235, 45);
		btnNewButton.setText("Send");
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setBounds(10, 10, 807, 630);
		
		text_1 = new Text(this, SWT.BORDER);
		text_1.addKeyListener( new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) { /*DO NOTHING*/ }
			@Override
			public void keyReleased(KeyEvent arg0) {
				if( arg0.character == SWT.CR ) {
					Event e = new Event();
					btnNewButton.notifyListeners( SWT.Selection, e );
				}
			}
		});
		text_1.setBounds(btnNewButton.getBounds().x-200, btnNewButton.getBounds().y, 200, 45);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public Text getPlayerMessage() {
		return text_1;
	}
	public Text getMessageBox() {
		return text;
	}
	public void addMessageBox(String s){
		text.setText(getMessageBox().getText()+"\n"+s);
	}
}
