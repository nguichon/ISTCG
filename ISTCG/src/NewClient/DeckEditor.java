package NewClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabItem;

import OldClient.CardTemplate;

public class DeckEditor extends Composite {

	ClientMain main;
	List m_MyCollectionList;
	Spinner m_SpinnerToMove;
	List m_MyDeckList;
	Button m_AddCardButton;
	Button m_RemoveCardButton;
	Label m_LabelDeckCount;
	Label m_LabelTotalCount;
	Canvas m_CardPreview;
	int m_CardPreviewType;
	
	TabItem tab;
	Composite m_Parent;
	String[][] cards;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DeckEditor(Composite parent, int style,final ClientMain main, TabItem t) {
		super(parent, style);
		tab = t;
		m_Parent = parent;
		
		this.main=main;
		m_MyCollectionList = new List(this, SWT.BORDER);
		m_MyDeckList = new List(this, SWT.BORDER);
		m_AddCardButton = new Button(this, SWT.NONE);
			m_AddCardButton.setText("Add Card");
		m_RemoveCardButton = new Button(this, SWT.NONE);
			m_RemoveCardButton.setText("Remove Card");
		m_LabelDeckCount = new Label(this, SWT.NONE);
			m_LabelDeckCount.setText("Deck Count");
		m_LabelTotalCount = new Label(this, SWT.NONE);
			m_LabelTotalCount.setText("Total Count");
		m_SpinnerToMove = new Spinner(this, SWT.BORDER);
		m_CardPreview = new Canvas( this, SWT.NONE );
			m_CardPreviewType = 3;
			m_CardPreview.addPaintListener( new PaintListener() {
	
				@Override
				public void paintControl(PaintEvent e) {
					if( m_CardPreviewType == -1 ) {
						ClientCardTemplate.RenderBlack( e.gc, ClientCardTemplate.CardRenderSize.LARGE, null );
					} else {
						ClientCardTemplateManager.get().GetClientCardTemplate( m_CardPreviewType ).Render( e.gc, ClientCardTemplate.CardRenderSize.LARGE, null, 5 );
					}
				}
			});
			
		
		
		
		
		
		this.addListener( SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Rectangle new_size = m_Parent.getClientArea();
				int width_1 = (int)((new_size.width - 5) * 0.5);
				int width_2 = (int)((new_size.width - 5) * 0.5);
				
				m_MyCollectionList.setBounds( 0, 0, width_1, new_size.height );
				m_MyDeckList.setBounds( new_size.width - width_2, 0, width_2, new_size.height - ClientCardTemplate.CardRenderSize.LARGE.getHeight() );
				m_CardPreview.setBounds( new_size.width -  ClientCardTemplate.CardRenderSize.LARGE.getWidth(), new_size.height- ClientCardTemplate.CardRenderSize.LARGE.getHeight(),  ClientCardTemplate.CardRenderSize.LARGE.getWidth(),  ClientCardTemplate.CardRenderSize.LARGE.getHeight() );
				m_AddCardButton.setBounds( new_size.width - width_2, new_size.height-ClientCardTemplate.CardRenderSize.LARGE.getHeight(), width_2- ClientCardTemplate.CardRenderSize.LARGE.getWidth() - 50, 22 );
				m_RemoveCardButton.setBounds( new_size.width - width_2, new_size.height-ClientCardTemplate.CardRenderSize.LARGE.getHeight() + 27, width_2-  ClientCardTemplate.CardRenderSize.LARGE.getWidth() - 10, 22 );
				m_SpinnerToMove.setBounds( new_size.width - (ClientCardTemplate.CardRenderSize.LARGE.getWidth() + 50), new_size.height-ClientCardTemplate.CardRenderSize.LARGE.getHeight(), 40, 22 );
			}
			
		});

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public int getQuantity(){
		return m_SpinnerToMove.getSelection();
	}
	public String[] getCollectionSelection(){
		return m_MyCollectionList.getSelection();
	}
	public String[] getDeckSelection(){
		return m_MyDeckList.getSelection();
	}
	public void addCollection(String input){
		String[] cg = input.split("|");
		cards = new String[cg.length][2];
		for(int i = 0;i<cg.length;i++){
			cards[i]=cg[i].split(",");
		}
		
	}
	public void addCardToDeck(String s){
		//confirm we have this card
		boolean confirm=false;
		for(String[] a:cards){
			if(a[0].equals(s)){
				confirm=true;
			}
		}
		if(!confirm)
			return;
		//add the card to list_1
		m_MyDeckList.add(s);
	}
	public void writeDeck(){
		//Deck writer method
		
	}
}
