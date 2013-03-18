package server.games.cards;

import java.util.ArrayList;
import java.util.Collections;

import server.games.GamePlayer;

import Shared.GameZones;

/**
 * A collection of cards. Can be randomly reordered and freely searched, drawn
 * from, added to.
 * 
 * @author nguichon
 */
public class CardList {
	private ArrayList<ServerCardInstance> m_CardsInDeck;
	private GamePlayer m_Owner;
	private GameZones m_ListLocation;

	/**
	 * Default constructor, only initializes ArrayList collection
	 * @param gamePlayer 
	 */
	public CardList( GamePlayer owner, GameZones location ) {
		m_ListLocation = location;
		m_Owner = owner;
		m_CardsInDeck = new ArrayList<ServerCardInstance>();
	}

	/**
	 * Randomly reorders the ArrayList m_CardsInDeck.
	 */
	public void Shuffle() {
		Collections.shuffle(m_CardsInDeck);
	}

	/**
	 * Adds a card to the end of the deck's list. This will be the last card
	 * drawn from the deck, unless the deck is shuffled or another card is
	 * added.
	 * 
	 * @param toAdd
	 *            The cardBase to add, there can be multiple of the same
	 *            cardBase in a deck. e.g. multiple copies of the same card
	 */
	public void AddCard(ServerCardInstance toAdd) {
		toAdd.SetLocation( m_ListLocation );
		m_CardsInDeck.add(toAdd);
	}

	/**
	 * Removes and returns the first card from the Deck.
	 * 
	 * @return The first card in order, e.g. the card added the earliest through
	 *         AddCard, unless shuffled of course
	 */
	public ServerCardInstance DrawCard() {
		if( !m_CardsInDeck.isEmpty() ) {
			ServerCardInstance toReturn = m_CardsInDeck.remove(0);
			toReturn.SetLocation( GameZones.UNKNOWN );
			return toReturn;
		}
		
		return null;
	}

	/**
	 * Gets the number of cards in the deck.
	 * 
	 * @return Number of cards in the deck.
	 */
	public int DeckCount() {
		return m_CardsInDeck.size();
	}

	/**
	 * Removes all cards from collection.
	 */
	public void Clear() {
		m_CardsInDeck.clear();
	}
	
	public boolean Validate() {
		// There are not requirements for deck construction at 
		// this time! Yay! Your deck is legal!
		
		//CORRECTION THERE IS ONE REQUIREMENT: At least 60 cards.
		if( m_CardsInDeck.size() < 60 ) { return false; }
		
		return true;
	}
}
