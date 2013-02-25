package Client;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import Shared.GameZones;


/*
 * GAME UI
 * FOR GAMING
 * 
 */

public class GameUI extends GameStateUI {
	
	/*
	 * GAME STATE STUFF HERE
	 */
	
	int graveCount = 0;
	int deckCount = 0;
	int handCount = 0;
	int gameID = 0;
	int foeID = 0;
	/*
	 * END GAME STATE STUFF
	 */
	
	private enum Zones {
		GRAVEYARD, DECK, HAND;
	}

	
	public GameUI( Shell client, ClientMain main ) {
		
		m_Host = main;
		final GameUI m_This = this;
		m_UIObjects = new Vector<Control>();
		final Label gameTitle = new Label(client, SWT.READ_ONLY);
		gameTitle.setAlignment(SWT.CENTER);
		gameTitle.setBounds(250,0,100,50);
		gameTitle.setText("GAME");
		/*
		 * Add more stuff here later, will need to implement points                                                                                                                                                                                                                                                                                                                                                                 
		 * 
		 */

		
		
		/*
		 * Add draw card button
		 * add end turn button
		 */
		
		final Button drawCard = new Button (client, SWT.PUSH);
		final Button endTurn = new Button(client, SWT.PUSH);
		
		drawCard.setBounds(450, 450, 60, 25);
		drawCard.setAlignment(SWT.CENTER);
		drawCard.setText("Draw");
		
		endTurn.setBounds(450, 500, 50, 25);
		endTurn.setAlignment(SWT.CENTER);
		endTurn.setText("End Turn");
		
		
		drawCard.addListener(SWT.Selection,new Listener() {

			
			@Override
			public void handleEvent(Event arg0) {
				m_This.drawCard();
				
			}
			
		});
		endTurn.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				// TODO Auto-generated method stub
				m_This.endTurn();
			}
			
		});
		
		
		Label grave = new Label(client,SWT.READ_ONLY);
		grave.setAlignment(SWT.CENTER);
		grave.setText(Integer.toString(graveCount));
		grave.setBounds(10,430,100,100);
		
		Label deck = new Label(client,SWT.READ_ONLY);
		deck.setAlignment(SWT.CENTER);
		deck.setText(Integer.toString(deckCount));
		deck.setBounds(25,430,100,100);
		
		Label hand = new Label(client,SWT.READ_ONLY);
		hand.setAlignment(SWT.CENTER);
		hand.setText(Integer.toString(handCount));
		hand.setBounds(40,430,100,100);
		
		Label foeName = new Label(client, SWT.READ_ONLY);
		foeName.setAlignment(SWT.CENTER);
		Label foeHand = new Label(client, SWT.READ_ONLY);
		foeHand.setAlignment(SWT.CENTER);
		Label foeDeck = new Label(client, SWT.READ_ONLY);
		foeDeck.setAlignment(SWT.CENTER);
		Label foeGrave = new Label(client, SWT.READ_ONLY);
		foeGrave.setAlignment(SWT.CENTER);
		
		m_UIObjects.add(gameTitle); //0
		m_UIObjects.add(drawCard); //1
		m_UIObjects.add(endTurn); //2
		m_UIObjects.add(grave); //3
		m_UIObjects.add(deck); //4
		m_UIObjects.add(hand);//5
		
		/*
		 * Your mortal enemy's board state
		 * 
		 */
		m_UIObjects.add(foeName); //6
		m_UIObjects.add(foeDeck); //7
		m_UIObjects.add(foeGrave); //8
		m_UIObjects.add(foeHand); //9
		
		for( Control w : m_UIObjects ) {
			w.setVisible(false);
		}
	}
	
	@Override
	public void Disable() {
		for( Control w : m_UIObjects ) {
			w.setVisible(false);
		}
	}

	@Override
	public void Enable() {
		for( Control w : m_UIObjects ) {
			w.setVisible(true);
		}
	}
	@Override
	public void HandleMessage(String[] inputs) {
		
	}
	
	public void initializeGame(String client2,int foeID, int gameID){
		/*
		 * Make game
		 */
		((Label) m_UIObjects.get(6)).setText(client2);
		((Label) m_UIObjects.get(6)).setBounds(25, 50, 45, 150);
		this.foeID=foeID;
		
	}
	
	/*
	 * LOAD DECK
	 * CLIENT ONLY CARES ABOUT HIS/HER OWN DECK
	 */
	public void loadDeck(String pathToDeck){
		/*
		 * CODE TO LOAD DECKS
		 */
		
		
	}
	public void drawCard(){
		/*
		 * CODE TO ASK SERVER FOR NEXT CARD
		 */
		handCount++;
		updateStuff();
	}
	
	public void endTurn() {
		/*
		 * CODE TO END TURN
		 */
	}
	
	public void updateStuff(){
		Label t = (Label) m_UIObjects.get(5);
		t.setText(Integer.toString(handCount));
		t = (Label) m_UIObjects.get(4);
		t.setText(Integer.toString(deckCount));
		t = (Label) m_UIObjects.get(3);
		t.setText(Integer.toString(graveCount));
	}

	public void updateZone(String player, String zone, String val) {
	int pid = Integer.valueOf(player);
	switch (GameZones.valueOf(zone)){
	case HAND:
		if(pid==foeID){
			((Label)m_UIObjects.get(9)).setText(val);
		}
		if(pid==m_Host.getID()){
			((Label)m_UIObjects.get(5)).setText(val);
		}
		break;
	case DECK:
		if(pid==foeID){
			((Label)m_UIObjects.get(7)).setText(val);
		}
		if(pid==m_Host.getID()){
			((Label)m_UIObjects.get(4)).setText(val);
		}
	}
		
	}
	
	
}
