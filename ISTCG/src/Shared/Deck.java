package Shared;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A collection of cards. Can be randomly reordered and freely searched, drawn
 * from, added to.
 * 
 * @author nguichon
 */
public class Deck {
	private ArrayList<CardBase> m_CardsInDeck;

	/**
	 * Default constructor, only initializes ArrayList collection
	 */
	public Deck() {
		m_CardsInDeck = new ArrayList<CardBase>();
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
	public void AddCard(CardBase toAdd) {
		m_CardsInDeck.add(toAdd);
	}

	/**
	 * Removes and returns the first card from the Deck.
	 * 
	 * @return The first card in order, e.g. the card added the earliest through
	 *         AddCard, unless shuffled of course
	 */
	public CardBase DrawCard() {
		return m_CardsInDeck.get(0);
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
		return true;
	}
}
