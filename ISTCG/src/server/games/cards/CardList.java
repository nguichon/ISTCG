package server.games.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import server.games.GamePlayer;
import Shared.GameZones;

/**
 * A collection of cards. Can be randomly reordered and freely searched, drawn
 * from, added to.
 * 
 * @author nguichon
 */
public class CardList {
	private ArrayList<ServerCardInstance> m_CardsInList;
	private GamePlayer m_Owner;
	private GameZones m_ListLocation;

	/**
	 * Default constructor, only initializes ArrayList collection
	 * @param gamePlayer 
	 */
	public CardList( GamePlayer owner, GameZones location ) {
		m_ListLocation = location;
		m_Owner = owner;
		m_CardsInList = new ArrayList<ServerCardInstance>();
	}

	/**
	 * Randomly reorders the ArrayList m_CardsInList.
	 */
	public void Shuffle() {
		Collections.shuffle(m_CardsInList, new Random());
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
	public void Add(ServerCardInstance toAdd) {
		toAdd.SetLocation( m_ListLocation );
		m_CardsInList.add(toAdd);
	}
	
	public ServerCardInstance GetTopCard() {
		if( !m_CardsInList.isEmpty() ) {
			return m_CardsInList.remove( 0 );
		}
		
		return null;
	}

	/**
	 * Removes and returns the first card from the Deck.
	 * 
	 * @return The first card in order, e.g. the card added the earliest through
	 *         AddCard, unless shuffled of course
	 */
	public ServerCardInstance DrawCard() {
		if( !m_CardsInList.isEmpty() ) {
			ServerCardInstance toReturn = m_CardsInList.remove(0);
			toReturn.SetLocation( GameZones.UNKNOWN );
			return toReturn;
		}
		
		return null;
	}
	
	public ServerCardInstance FindAndGetCard( int id ) {
		for( ServerCardInstance i : m_CardsInList ) {
			if( i.GetCardUID() == id ) { 
				m_CardsInList.remove( i );
				i.SetLocation( GameZones.UNKNOWN );
				return i; 
			}
		}
		
		return null;
	}

	/**
	 * Gets the number of cards in the deck.
	 * 
	 * @return Number of cards in the deck.
	 */
	public int Count() {
		return m_CardsInList.size();
	}

	/**
	 * Removes all cards from collection.
	 */
	public void Clear() {
		m_CardsInList.clear();
	}
	
	public boolean Validate() {
		// There are not requirements for deck construction at 
		// this time! Yay! Your deck is legal!
		
		//CORRECTION THERE IS ONE REQUIREMENT: At least 60 cards.
		if( m_CardsInList.size() < 60 ) { return false; }
		
		return true;
	}

	public void Remove(ServerCardInstance card) {
		card.SetLocation( GameZones.UNKNOWN );
		m_CardsInList.remove( card );
	}

	public boolean Contains(ServerCardInstance card) {
		return m_CardsInList.contains( card );
	}
}
