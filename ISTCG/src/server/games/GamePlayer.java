package server.games;

import server.games.cards.CardTemplateManager;
import server.games.cards.CardList;
import server.games.cards.ServerCardInstance;
import server.games.cards.ServerCardTemplate;
import server.network.ClientAccount;
import Shared.ClientMessages;
import Shared.GameResources;
import Shared.GameZones;

public class GamePlayer {
	private GameInstance m_Game;
	private ClientAccount m_PlayerAccount;
	private CardList m_PlayerDeck;
	private CardList m_PlayerHand;
	private boolean m_Ready;
	private int[] m_Resources = new int[GameResources.values().length];
	private GamePlayerStates m_State;

	//Constructors and administrative methods
	public GamePlayer( GameInstance g, ClientAccount acc ) {
		m_Game = g;
		m_PlayerAccount = acc;
		m_Ready = false;
		
		m_PlayerHand = new CardList( this, GameZones.HAND );
		m_State = GamePlayerStates.WAITING;
	}
	public void SwitchAccountInstance( ClientAccount acc ) { m_PlayerAccount = acc; }
	public void LoadDeck( String deckList ) {
		if( m_PlayerDeck == null ) {
			CardList newDeck = new CardList( this, GameZones.DECK );
			
			String[] cards = deckList.split("\\|");
			for( String s : cards ) {
				String[] values = s.split(",");
				for( int i = 0; i < Integer.valueOf(values[1]); i++ ) {
					ServerCardInstance toAdd = new ServerCardInstance( m_Game, this, Integer.valueOf(values[0]) );
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

	//Data getters
	public ClientAccount getAccount() { return m_PlayerAccount; }
	public boolean isReady() { return m_Ready; }
	public ClientAccount getClient() { return m_PlayerAccount; }
	public GamePlayerStates getState() { return m_State; }
	public int GetPlayerID() { return m_PlayerAccount.getUserID(); }
	
	//Game play methods
	public void DrawCards( int number ) {
		//Draw the cards from the deck and place them in hand.
		for( int i = 0; i < number; i++ ) {
			ServerCardInstance cardDrawn = m_PlayerDeck.DrawCard();
			m_PlayerHand.AddCard( cardDrawn );
	        m_PlayerAccount.SendMessage( ClientMessages.MOVE,
					m_Game.GetGameID() + "",
					cardDrawn.GetCardUID() + "",
					GameZones.HAND.name() );
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
	public void AddResource( GameResources res, int value ) {
		m_Resources[ res.ordinal() ] += value;
		m_Game.SendMessageToAllPlayers( ClientMessages.UPDATE_PLAYER, res.name(), "" + m_Resources[ res.ordinal() ] );
	}
	public void Pass() { m_State = GamePlayerStates.WAITING; }
}
