package Server;

import Shared.CardTemplates;
import Shared.Deck;
import Shared.GameZones;

public class GamePlayer {
	Game m_Game;
	ClientAccount m_PlayerAccount;
	Deck m_PlayerDeck;
	Deck m_PlayerHand;
	boolean m_Ready;

	public GamePlayer( Game g, ClientAccount acc ) {
		m_Game = g;
		m_PlayerAccount = acc;
		m_Ready = false;
	}
	public void SwitchAccountInstance( ClientAccount acc ) {
		m_PlayerAccount = acc;
	}
	public ClientAccount getClient() {
		return m_PlayerAccount;
	}
	
	public void DrawCards( int number ) {
		//Draw the cards from the deck and place them in hand.
		for( int i = 0; i < number; i++ ) {
			CardTemplates cardDrawn = m_PlayerDeck.DrawCard();
			m_PlayerHand.AddCard( cardDrawn );
	        m_PlayerAccount.SendMessage( "ADDCARD;" + 
					m_Game.GetID() + ";" + 
					m_PlayerAccount.getUserID() + ";" + 
					GameZones.HAND.toString() + ";" + 
					cardDrawn.getID() );
		}
		
		//Notify clients
		m_Game.SendMessageToAllPlayers("UPDATE;" + 
											m_Game.GetID() + ";" + 
											m_PlayerAccount.getUserID() + ";" + 
											GameZones.DECK.toString() + ";" +
											m_PlayerDeck.DeckCount());
		m_Game.SendMessageToAllPlayers("UPDATE;" + 
											m_Game.GetID() + ";" + 
											m_PlayerAccount.getUserID() + ";" + 
											GameZones.HAND.toString() + ";" +
											m_PlayerHand.DeckCount());
	}
	
	public boolean isReady() {
		return m_Ready;
	}

	public ClientAccount getAccount() {
		return m_PlayerAccount;
	}
	
	public void LoadDeck( String deckList ) {
		if( m_PlayerDeck == null ) {
			Deck newDeck = new Deck();
			
			ServerMain.ConsoleMessage('?', deckList);
			
			//TODO Load the deck from string.
			String[] cards = deckList.split("\\|");
			for( String s : cards ) {
				ServerMain.ConsoleMessage('?', s);
				String[] values = s.split(",");
				ServerCardTemplate toAdd = CardTemplateManager.get().GetCardTemplate( Integer.valueOf(values[0]) );
				for( int i = 0; i < Integer.valueOf(values[1]); i++ ) {
					newDeck.AddCard(toAdd);
				}
			}
			
			newDeck.Shuffle();
			
			if( newDeck.Validate() ) {
				m_PlayerDeck = newDeck;
				m_Ready = true;
				m_Game.Ready();
			} 
		}
		//Do nothing
	}
	public int GetPlayerID() {
		return m_PlayerAccount.getUserID();
	}
}
