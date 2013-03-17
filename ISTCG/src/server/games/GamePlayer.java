package server.games;

import server.games.cards.CardTemplateManager;
import server.games.cards.Deck;
import server.games.cards.ServerCardTemplate;
import server.network.ClientAccount;
import server.network.ClientMessages;
import Shared.GameResources;
import Shared.GameZones;

public class GamePlayer {
	private Game m_Game;
	private ClientAccount m_PlayerAccount;
	private Deck m_PlayerDeck;
	private Deck m_PlayerHand;
	private boolean m_Ready;
	private int[] m_Resources = new int[GameResources.values().length];
	private GamePlayerStates m_State;

	public GamePlayer( Game g, ClientAccount acc ) {
		m_Game = g;
		m_PlayerAccount = acc;
		m_Ready = false;
		
		m_PlayerHand = new Deck();
		m_State = GamePlayerStates.WAITING;
	}
	public void SwitchAccountInstance( ClientAccount acc ) { m_PlayerAccount = acc; }
	public ClientAccount getClient() { return m_PlayerAccount; }
	public GamePlayerStates getState() { return m_State; }
	
	public void DrawCards( int number ) {
		//Draw the cards from the deck and place them in hand.
		for( int i = 0; i < number; i++ ) {
			ServerCardTemplate cardDrawn = m_PlayerDeck.DrawCard();
			m_PlayerHand.AddCard( cardDrawn );
	        m_PlayerAccount.SendMessage( ClientMessages.CREATE_TEMPLATE,
					"" + m_Game.GetGameID(),
					"" + m_PlayerAccount.getUserID(),
					GameZones.HAND.name(),
					"" + cardDrawn.getCardTemplateID() );
		}
		
		//Notify clients
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_ZONE,
											"" + m_Game.GetGameID(), 
											"" + m_PlayerAccount.getUserID(), 
											GameZones.DECK.name(),
											"" + m_PlayerDeck.DeckCount());
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_ZONE,
											"" + m_Game.GetGameID(),
											"" + m_PlayerAccount.getUserID(),
											GameZones.HAND.name(),
											"" + m_PlayerHand.DeckCount());
	}
	
	public boolean isReady() {
		return m_Ready;
	}

	public ClientAccount getAccount() {
		return m_PlayerAccount;
	}
	
	public void AddResource( GameResources res, int value ) {
		m_Resources[ res.ordinal() ] += value;
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_PLAYER, res.name(), "" + m_Resources[ res.ordinal() ] );
	}
	
	public void LoadDeck( String deckList ) {
		if( m_PlayerDeck == null ) {
			Deck newDeck = new Deck();
			
			String[] cards = deckList.split("\\|");
			for( String s : cards ) {
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
	}
	public int GetPlayerID() {
		return m_PlayerAccount.getUserID();
	}
}
