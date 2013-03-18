package NewClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabItem;

public class DeckEditor extends Composite {
	Spinner spinner;

	ClientMain main;
	List list;
	List list_1;
	Button btnAddCard;
	Label lblQuantity;
	Label lblCtotal;
	Button btnRemoveCard;
	TabItem tab;
	String[][] cards;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DeckEditor(Composite parent, int style,final ClientMain main, TabItem t) {
		super(parent, style);
		tab = t;
		this.main=main;
		list = new List(this, SWT.BORDER);
		list.setBounds(10, 0, 365, 679);
		
		list_1 = new List(this, SWT.BORDER);
		list_1.setBounds(381, 10, 529, 425);
		
		btnAddCard = new Button(this, SWT.NONE);
		btnAddCard.setBounds(381, 445, 94, 28);
		btnAddCard.setText("Add Card");
		
		spinner = new Spinner(this, SWT.BORDER);
		spinner.setBounds(391, 479, 51, 22);
		
		lblQuantity = new Label(this, SWT.NONE);
		lblQuantity.setAlignment(SWT.CENTER);
		lblQuantity.setBounds(448, 479, 59, 22);
		lblQuantity.setText("Quantity");
		
		lblCtotal = new Label(this, SWT.NONE);
		lblCtotal.setBounds(606, 441, 59, 14);
		lblCtotal.setText("CTOTAL");
		
		btnRemoveCard = new Button(this, SWT.NONE);
		btnRemoveCard.setBounds(477, 445, 103, 28);
		btnRemoveCard.setText("Remove Card");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public int getQuantity(){
		return spinner.getSelection();
	}
	public String[] getCollectionSelection(){
		return list.getSelection();
	}
	public String[] getDeckSelection(){
		return list_1.getSelection();
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
		list_1.add(s);
	}
	public void writeDeck(){
		//Deck writer method
		
	}
}
