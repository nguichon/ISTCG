package NewClient;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;

import Client.CardInstance;
import Client.CardTemplate.CardRenderSize;
import Shared.GameResources;
import Shared.GameZones;

public class Game extends Composite {

	ClientMain main;
	String ID="";
	String eID="";
	TabItem tab;
	Label lblUsername;
	Label lblEnemy;
	Label lblHand;
	Label lblEhandsize;
	Button btnPass;
	Button btnEnd;
	Label lblHandsize;
	Label lblDecksize;
	Label lblGravesize;
	Label lblEdecksize;
	Label lblEgravesize;
	Label lblEmetal;
	Label lblEenergy;
	Label lblEtech;
	Label lblMetal;
	Label lblEnergy;
	Label lblTech;
	//start at 100 x 465
	Point handPos;
	Point fieldPos;
	Point stackPos;
	ArrayList<ClientCardInstance> cards;
	Label lblStack;
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Game(Composite parent, int style, final ClientMain main, TabItem tab) {
		super(parent, style);
		this.main=main;
		this.tab=tab;
		handPos = new Point(0,0);
		fieldPos = new Point(100, 365);
		stackPos = new Point(10,33);
		lblUsername = new Label(this, SWT.NONE);
		lblUsername.setBounds(10, 451, 59, 14);
		lblUsername.setText("USERNAME");
		
		lblEnemy = new Label(this, SWT.NONE);
		lblEnemy.setBounds(675, 43, 59, 14);
		lblEnemy.setText("ENEMY");
		
		lblHand = new Label(this, SWT.NONE);
		lblHand.setBounds(333, 451, 59, 14);
		lblHand.setText("HAND");
		
		lblEhandsize = new Label(this, SWT.NONE);
		lblEhandsize.setBounds(675, 74, 59, 14);
		lblEhandsize.setText("EHANDSIZE");
		
		btnPass = new Button(this, SWT.NONE);
		btnPass.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				main.sendData("PASS;"+getID());
				disablePass();
			}
		});
		btnPass.setBounds(10, 10, 94, 28);
		btnPass.setText("PASS");
		
		btnEnd = new Button(this, SWT.NONE);
		btnEnd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				main.sendData("END;"+getID());
			}
		});
		btnEnd.setBounds(10, 43, 94, 28);
		btnEnd.setText("END");
		
		lblHandsize = new Label(this, SWT.NONE);
		lblHandsize.setBounds(10, 465, 59, 14);
		lblHandsize.setText("HANDSIZE");
		
		lblDecksize = new Label(this, SWT.NONE);
		lblDecksize.setBounds(10, 486, 59, 14);
		lblDecksize.setText("DECKSIZE");
		
		lblGravesize = new Label(this, SWT.NONE);
		lblGravesize.setBounds(10, 510, 59, 14);
		lblGravesize.setText("GRAVESIZE");
		
		lblEdecksize = new Label(this, SWT.NONE);
		lblEdecksize.setBounds(675, 95, 59, 14);
		lblEdecksize.setText("EDECKSIZE");
		
		lblEgravesize = new Label(this, SWT.NONE);
		lblEgravesize.setBounds(675, 115, 59, 14);
		lblEgravesize.setText("EGRAVESIZE");
		
		lblEmetal = new Label(this, SWT.NONE);
		lblEmetal.setBounds(675, 137, 59, 14);
		lblEmetal.setText("EMETAL");
		
		lblEenergy = new Label(this, SWT.NONE);
		lblEenergy.setBounds(675, 157, 59, 14);
		lblEenergy.setText("EENERGY");
		
		lblEtech = new Label(this, SWT.NONE);
		lblEtech.setBounds(675, 180, 59, 14);
		lblEtech.setText("ETECH");
		
		lblMetal = new Label(this, SWT.NONE);
		lblMetal.setBounds(675, 439, 59, 14);
		lblMetal.setText("METAL");
		
		lblEnergy = new Label(this, SWT.NONE);
		lblEnergy.setBounds(675, 465, 59, 14);
		lblEnergy.setText("ENERGY");
		
		lblTech = new Label(this, SWT.NONE);
		lblTech.setBounds(675, 486, 59, 14);
		lblTech.setText("TECH");
		
		lblStack = new Label(this, SWT.NONE);
		lblStack.setBounds(10, 77, 59, 14);
		lblStack.setText("STACK");

		/*
		 * Finally start game stuff
		 */
		//this.loadDeck();
		this.disablePass();
		cards = new ArrayList<ClientCardInstance>();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	public void setID(String s){
		ID=s;
	}
	public String getID(){
		return ID;
	}
	public void setEnemy(String name){
		lblEnemy.setText(name);
	}
	public void setEnemyHandSize(String num){
		lblEhandsize.setText(num);
	}
	public void setEnemyDeckSize(String num){
		lblEdecksize.setText(num);
	}
	public void setEnemyGraveSize(String num){
		lblEgravesize.setText(num);
	}
	public void setPlayer(String name){
		lblUsername.setText(name);
	}
	public void setHandSize(String num){
		lblHandsize.setText(num);
	}
	public void setDeckSize(String num){
		lblDecksize.setText(num);
	}
	public void setGraveSize(String num){
		lblGravesize.setText(num);
	}
	public void setEnemyID(String ID){
		eID=ID;
	}
	public void updateZone(String player, String zone, String val){
		if(player.equals(main.getPID())){
			//This guy
			switch(GameZones.valueOf(zone)){
			case HAND:
				setHandSize(val); break;
			case DECK:
				setDeckSize(val); break;
			case GRAVEYARD:
				setGraveSize(val); break;
			default:
				break;
			}
		} else if(player.equals(eID)){
			switch(GameZones.valueOf(zone)){
			case HAND:
				setEnemyHandSize(val); break;
			case DECK:
				setEnemyDeckSize(val); break;
			case GRAVEYARD:
				setEnemyGraveSize(val); break;
			default:
				break;
			}
		}
	}
	public void setTurn(String player){
		if(player.equals(main.getPID())){
			btnEnd.setEnabled(true);
		} else if(player.equals(eID)){
			btnEnd.setEnabled(false);
		}
	}
	public void addToStack(String cardID){
		if(!hasCardLoaded(cardID))
			createCard(cardID);
	}
	public void enablePass(){
		btnPass.setEnabled(true);
	}
	public void disablePass() {
		btnPass.setEnabled(false);
	}
	//3,100
	public void loadDeck(){
		//default for now
		main.sendData("DECKLIST;"+this.getID()+";3,100");
	}

	public void addToHand(String cardID){
		if(!hasCardLoaded(cardID))
			createCard(cardID);
		findCardById(cardID).setBounds(handPos.x, handPos.y, ClientCardTemplate.CardRenderSize.SMALL.getWidth(), ClientCardTemplate.CardRenderSize.SMALL.getHeight());
		handPos.x+=64;
	}
	
	public boolean hasCardLoaded(String id){
		return (findCardById(id)!=null);
	}
	CardInstance tmp = null;
	public void addToField(String cardID){
		if(!hasCardLoaded(cardID))
			createCard(cardID);
		
	}
	public ClientCardInstance findCardById(String id){
		for(ClientCardInstance c:cards){
			if(id.equals(String.valueOf(c.getID())))
					return c;
		}
		return null;
	}
	public GameZones getPosition(String cardID){
		return null;
	}
	public void moveCard(String cardID, String zone){
		if(!hasCardLoaded(cardID))
			main.sendData("GETCARDINFO;"+this.getID()+";"+cardID);
		switch(GameZones.valueOf(zone)){
			case HAND:
				addToHand(cardID); break;
			case FIELD:
				addToField(cardID);
				break;
			case STACK:
				addToStack(cardID); break;
			default: 
				break;
		}
	}
	
	
	public void createCard(String cardID){
		//CardTemplate template = CardTemplateManager.get().GetCardTemplate(Integer.valueOf(templateID));
		ClientCardInstance card = new ClientCardInstance(this, Integer.valueOf(cardID), main, cardID);
		//card.SetTemplate(Integer.valueOf(templateID));
		cards.add(card);
	}
	public void setCard(String templateID, String cardID){
		if(hasCardLoaded(cardID)){
			//CardTemplateManager.get().GetCardTemplate(Integer.valueOf(templateID));
			findCardById(cardID).setTemplate(templateID);
		}
	}
	
	public void setResource(String player, String res, String val) {
		if(player.equals(main.getPID())){
			switch(GameResources.valueOf(res)){
			case METAL:
				lblMetal.setText(val);
				break;
			case ENERGY:
				lblEnergy.setText(val);
				break;
			case TECH:
				lblTech.setText(val);
				break;
			default: break;
			}
		} else if(player.equals(eID)){
			switch(GameResources.valueOf(res)){
			case METAL:
				lblEmetal.setText(val);
				break;
			case ENERGY:
				lblEenergy.setText(val);
				break;
			case TECH:
				lblEtech.setText(val);
				break;
			default: break;
			}
		}
		
	}
}
