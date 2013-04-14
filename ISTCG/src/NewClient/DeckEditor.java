package NewClient;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
	String[][] cardsStrings;
	TabItem tab;
	Composite m_Parent;
	String[][] cards;
	String[][] raw;
	ArrayList<String[]> deck;
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
		deck = new ArrayList<String[]>();
		m_MyCollectionList = new List(this, SWT.BORDER);
			m_MyCollectionList.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					
					
				}

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					
					m_CardPreviewType = Integer.valueOf(cards[m_MyCollectionList.getSelectionIndex()][0]);
					m_CardPreview.redraw();
				}
				
			});
		m_MyDeckList = new List(this, SWT.BORDER);
//			m_MyDeckList.addSelectionListener(new SelectionListener(){
//
//				@Override
//				public void widgetDefaultSelected(SelectionEvent arg0) {
//					// TODO Auto-generated method stub
//					
//				}
//
//				@Override
//				public void widgetSelected(SelectionEvent arg0) {
//					// TODO Auto-generated method stub
//					m_CardPreviewType = Integer.valueOf(cards[m_MyCollectionList.get][0]);
//					m_CardPreview.redraw();
//				}
//				
//			});
		m_AddCardButton = new Button(this, SWT.NONE);
			m_AddCardButton.setText("Add Card");
			m_AddCardButton.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					// TODO Auto-generated method stub
					String[] s = m_MyCollectionList.getSelection();
					for(String c:s){
						addCardToDeck(c);
					}
				}
				
			});
		m_RemoveCardButton = new Button(this, SWT.NONE);
			m_RemoveCardButton.setText("Remove Card");
			m_RemoveCardButton.addMouseListener(new MouseListener(){

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
					
				}
				
			});
		m_LabelDeckCount = new Label(this, SWT.NONE);
			m_LabelDeckCount.setText("Deck Count");
		m_LabelTotalCount = new Label(this, SWT.NONE);
			m_LabelTotalCount.setText("Total Count");
		m_SpinnerToMove = new Spinner(this, SWT.BORDER);
		m_CardPreview = new Canvas( this, SWT.NONE );
			m_CardPreviewType = 5;
			m_CardPreview.addPaintListener( new PaintListener() {
	
				@Override
				public void paintControl(PaintEvent e) {
					if( m_CardPreviewType == -1 ) {
						ClientCardTemplate.RenderBlack( e.gc, ClientCardTemplate.CardRenderSize.LARGE, null );
					} else {
						ClientCardTemplateManager.get().GetClientCardTemplate( m_CardPreviewType ).Render( e.gc, ClientCardTemplate.CardRenderSize.LARGE, null, 0 );
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

		main.sendData("GETCOLLECTION");
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
		System.out.println(input);
		String[] cg = input.split("\\|");
		System.out.println(Arrays.toString(cg));
		cards = new String[cg.length][2];
		for(int i = 0;i<cg.length;i++){
			cards[i]=cg[i].split(",");
		}
		
		//add to list
		for(String[] c:cards){
			if(!c[0].equals(""))
				this.m_MyCollectionList.add(ClientCardTemplateManager.get().GetClientCardTemplate(Integer.valueOf(c[0])).getCardName());
		}
		this.m_MyCollectionList.redraw();
		
	}
	public void addCardToDeck(String s){
		//confirm we have this card
		boolean confirm=false;
		String s0="";
		System.out.println("Adding");
		for(String[] a:cards){
			if(ClientCardTemplateManager.get().GetClientCardTemplate(Integer.valueOf(a[0])).getCardName().equals(s)){
				System.out.println("has card");
				if(Integer.valueOf(a[1])>=this.m_SpinnerToMove.getSelection()){
					confirm = true;
					System.out.println("Has amount");
					s0=a[0];
				}
			}
		}
		if(!confirm)
			return;
		//add the card to list_1
		m_MyDeckList.add(s);
		deck.add(new String[]{s0,String.valueOf(m_SpinnerToMove.getSelection())});
	}
	
	public void removeCardFromDeck(){
		String[] s = this.m_MyDeckList.getSelection();
		for(String c:s){
			m_MyDeckList.remove(c);
			for(int i =0;i<deck.size();i++){
				if(deck.get(i)[0].equals(c)){
					deck.remove(i);
				}
			}
		}
	}
	
	public void writeDeck(){
		//Deck writer method
		try {
			FileWriter fw = new FileWriter("deck.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			String tmp = "";
			int i=0;
			for(String[] c:deck){
				tmp+=(i==0?"":"|")+c[0]+","+c[1];
			}
			bw.write(tmp);
			bw.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
