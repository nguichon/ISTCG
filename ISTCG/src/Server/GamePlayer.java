package Server;

import Shared.CardBase;
import Shared.Deck;

public class GamePlayer {
	Game m_Game;
	ClientAccount m_PlayerAccount;
	Deck m_PlayerDeck;
	Deck m_PlayerHand;

	public GamePlayer( Game g, ClientAccount acc ) {
		m_Game = g;
		m_PlayerAccount = acc;
	}
	public void SwitchAccountInstance( ClientAccount acc ) {
		m_PlayerAccount = acc;
	}
	public ClientAccount getClient() {
		return m_PlayerAccount;
	}
	public void DrawACard() {
		CardBase cardDrawn = m_PlayerDeck.DrawCard();
		m_PlayerHand.AddCard( cardDrawn );
		
		m_Game.SendMessageToAllPlayers( "COUNTCHANGE;" + m_PlayerAccount + ";DECK;" + m_PlayerDeck.DeckCount() );
		m_Game.SendMessageToAllPlayers( "COUNTCHANGE;" + m_PlayerAccount + ";HAND;" + m_PlayerHand.DeckCount() );
		
		m_PlayerAccount.SendMessage( "DRAW;" + cardDrawn.getID() );
	}
}
