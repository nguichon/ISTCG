package server.games.cards;

import java.util.ArrayList;
import java.util.HashMap;

import server.games.Game;


import Shared.StatBlock;

public class CardInstance {
	private ServerCardTemplate m_CardTemplate;
	private HashMap<StatBlock.StatType, StatBlock> m_Stats;
	private int m_UID;
	
	public CardInstance( int cardTemplate, Game game ) {
		m_UID = game.getNewCardID();
		m_Stats = new HashMap<StatBlock.StatType, StatBlock>();
		m_CardTemplate = CardTemplateManager.get().GetCardTemplate( cardTemplate );
	}
	
	public int getUID() { return m_UID; }
	public void setStat( StatBlock.StatType type, int value ) {
		StatBlock sb = m_Stats.get( type );
		if( sb == null ) { 
			sb = new StatBlock( type, value ); 
			m_Stats.put( type, sb ); 
		} else {
			sb.m_Value = value;
		}
	}
	public final StatBlock getStat( StatBlock.StatType type ) {
		StatBlock sb = m_Stats.get( type );
		if( sb == null ) { m_CardTemplate.getStat( type ); }
		return sb;
	}
}
