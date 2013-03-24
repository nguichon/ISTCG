package server.games.cards;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import server.games.cards.abilities.Ability;
import server.games.cards.abilities.TargetingCondition;
import server.games.events.ResolutionEvent;

import Shared.CardTypes;
import Shared.StatBlock;

public abstract class ServerCardTemplate {
	private CardTypes m_CardType;
	private int m_CardID;
	private HashMap<StatBlock.StatType, StatBlock> m_Stats;
	private ArrayList<Ability> m_Abilities;
	private ArrayList<TargetingCondition> m_Targets;
	
	public ServerCardTemplate( ) { m_Stats = new HashMap<StatBlock.StatType, StatBlock>();  }
	public void Initialize(ResultSet card) throws SQLException  {
		setCardTemplateID( card.getInt( "id" ) );
		setStat( StatBlock.StatType.ATTACK, card.getInt( "attack" ) );
		setStat( StatBlock.StatType.DEFENSE,  card.getInt( "defense" ) );
		setStat( StatBlock.StatType.POWER,  card.getInt( "power" ) );
		setStat( StatBlock.StatType.STRUCTURE,  card.getInt( "structure" ) );
		setStat( StatBlock.StatType.HARD_POINTS,  card.getInt( "hard_points" ) );
		setStat( StatBlock.StatType.DELAY,  card.getInt( "delay" ) );
		setStat( StatBlock.StatType.METAL,  card.getInt( "metal" ) );
		setStat( StatBlock.StatType.ENERGY,  card.getInt( "energy" ) );
		setStat( StatBlock.StatType.TECH,  card.getInt( "tech" ) );
		m_CardType = CardTypes.valueOf( card.getString( "type" ) );
	}
	
	public final int getCardTemplateID() { return m_CardID; }
	public final CardTypes getCardType() { return m_CardType; }
	public final StatBlock getStat( StatBlock.StatType type ) { return m_Stats.get( type ); }
	public final int getAbilityCount() { return m_Abilities.size(); }
	public final Ability getAbility( int abilIndex ) { return m_Abilities.get( abilIndex ); }
	
	protected final void setCardTemplateID( int id ) { m_CardID = id; }
	protected final void setStat( StatBlock.StatType type, int value ) { if( value != -1 ) { setStat( new StatBlock( type, value ) ); } }
	protected final void setStat( StatBlock sb ) { m_Stats.put( sb.m_Type, sb ); }
	protected final void setCardType(CardTypes type) { m_CardType = type; }
	
	public abstract void Resolve( ResolutionEvent e );
}
