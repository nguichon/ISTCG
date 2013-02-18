package Shared;

import java.util.HashMap;
import java.util.Map;

public class CardBaseManager {
	private Map<Integer, CardBase> m_CardBases;
	private static CardBaseManager m_Instance;
	
	private CardBaseManager() { m_CardBases = new HashMap<Integer, CardBase>(); }
	public CardBaseManager Get() {
		if( m_Instance == null ) { m_Instance = new CardBaseManager(); }
		return m_Instance;
	}
	
	public CardBase GetCard( int id ) {
		CardBase toReturn = null;
		return toReturn;
	}
}
