package NewClient;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import NewClient.games.GameV2;

public class Lobby extends Composite {
	private StyledText m_ReceivedMessagesStyledText;
	private Text m_SendChatText;
	private TabFolder m_TabFolder;
	private Button m_SendChatButton;
	private Button m_FindMatchButton;
	
	DeckEditor d;
	ArrayList<GameV2> games;
	ClientMain main;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public static String newline = String.valueOf(new char[]{SWT.CR});
	public Lobby(Composite parent, int style, final ClientMain main) {
		super(parent, style);
		this.main = main;
		games = new ArrayList<GameV2>();
		m_ReceivedMessagesStyledText = new StyledText(this, SWT.BORDER | SWT.READ_ONLY|SWT.WRAP|SWT.MULTI|SWT.V_SCROLL);
		
		m_SendChatButton = new Button(this, SWT.NONE);
		m_SendChatButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				//onClick
				main.SendTextMessage(getPlayerMessage().getText());
				getPlayerMessage().setText("");
			}
		});
		m_SendChatButton.setText("Send");
		
		m_TabFolder = new TabFolder(this, SWT.NONE );
		
		m_SendChatText = new Text(this, SWT.BORDER | SWT.WRAP );
		m_SendChatText.addKeyListener( new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) { /*DO NOTHING*/ }
			@Override
			public void keyReleased(KeyEvent arg0) {
				if( arg0.character == SWT.CR ) {
					Event e = new Event();
					m_SendChatButton.notifyListeners( SWT.Selection, e );
				}
			}
		});

		
		this.addListener( SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Rectangle new_size = main.rShell().getClientArea();
				int width_1 = (int)((new_size.width - 30) * 0.8);
				int width_2 = (int)((new_size.width - 30) * 0.2);
				
				m_TabFolder.setBounds( 10, 10, width_1, new_size.height - 20);
				m_ReceivedMessagesStyledText.setBounds( new_size.width - width_2, 10, width_2 - 20, new_size.height - 74);
				m_SendChatText.setBounds( new_size.width - width_2, new_size.height - 54, width_2 - 105, 44);
				m_SendChatButton.setBounds( new_size.width - width_2 + (width_2 - 100), new_size.height - 54, 80, 44);
				
			}
			
		});
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public Text getPlayerMessage() {
		return m_SendChatText;
	}
	public StyledText getMessageBox() {
		return m_ReceivedMessagesStyledText;
	}
	public void addMessageBox(String s){
		m_ReceivedMessagesStyledText.setText(getMessageBox().getText()+"\n"+s);
	}
	//add deck editor
	public void addDeckEditor(){
		TabItem t = new TabItem(m_TabFolder, SWT.NULL);
		t.setText("Deck Editor");
		d = new DeckEditor(t.getParent(),SWT.None,main,t);
		t.setControl(d);
	}
	public DeckEditor getDeckEditor(){
		return d;
	}
	public void addStore(){
		TabItem t = new TabItem(m_TabFolder, SWT.NULL);
		t.setText("Store");
		StoreUI s = new StoreUI(t.getParent(), SWT.None, main, t);
	}
	public void addGame(String gID){
		//Make a game
		TabItem t = new TabItem(m_TabFolder,SWT.NULL);
		t.setText("Game "+gID);
		GameV2 g = new GameV2(t.getParent(), SWT.None, Integer.valueOf( gID ), main, t);
		g.LoadDeck( 3, "6,4|8,2|15,4|17,2|19,4|21,3|24,2|9,4|0,15|1,10|2,10" );
		games.add(g);
		t.setControl(g);
		
		m_TabFolder.setSelection( t );
	}
	public GameV2 findGameById(Integer integer){
		for(GameV2 g : games){
			if(g.getID() == integer){
				return g;
			}
		}
		return null;
	}
}
