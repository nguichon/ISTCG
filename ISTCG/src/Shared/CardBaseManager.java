package Shared;

import java.util.HashMap;
import java.util.Map;

public class CardBaseManager {
	private Map<Integer, CardTemplates> m_CardBases;
	private static CardBaseManager m_Instance;
	
	private CardBaseManager() { m_CardBases = new HashMap<Integer, CardTemplates>(); }
	public CardBaseManager Get() {
		if( m_Instance == null ) { m_Instance = new CardBaseManager(); }
		return m_Instance;
	}
	
	public CardTemplates GetCard( int id ) {
		CardTemplates toReturn = null;
		return toReturn;
	}
}
