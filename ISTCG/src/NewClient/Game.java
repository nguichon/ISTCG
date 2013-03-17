package NewClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Button;

import Shared.GameZones;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Game(Composite parent, int style, final ClientMain main, TabItem tab) {
		super(parent, style);
		this.main=main;
		this.tab=tab;
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

		/*
		 * Finally start game stuff
		 */
		//this.loadDeck();
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
	//3,100
	public void loadDeck(){
		//default for now
		main.sendData("DECKLIST;"+this.getID()+";3,100");
	}
}
