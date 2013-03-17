package server.games.cards;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import server.games.events.AttackEvent;
import server.games.events.DamageEvent;
import server.games.events.GameEvent;

import Shared.CardTypes;
import Shared.GameResources;
import Shared.StatBlock;

public abstract class ServerCardTemplate {
	private CardTypes m_CardType;
	private int m_CardID, m_AbilityCount;
	private HashMap<StatBlock.StatType, StatBlock> m_Stats;
	
	public ServerCardTemplate( ) {  }
	public void Initialize(ResultSet card) throws SQLException  {
		setCardTemplateID( card.getInt( "id" ) );
		setStat( StatBlock.StatType.ATTACK, card.getInt( "attack" ) );
		setStat( StatBlock.StatType.DEFENSE,  card.getInt( "defense" ) );
		setStat( StatBlock.StatType.DAMAGE,  card.getInt( "power" ) );
		setStat( StatBlock.StatType.STRUCTURE,  card.getInt( "structure" ) );
		setStat( StatBlock.StatType.GEAR_POINTS,  card.getInt( "hard_points" ) );
		setStat( StatBlock.StatType.DELAY,  card.getInt( "delay" ) );
		setStat( StatBlock.StatType.METAL,  card.getInt( "metal" ) );
		setStat( StatBlock.StatType.ENERGY,  card.getInt( "energy" ) );
		setStat( StatBlock.StatType.TECH,  card.getInt( "tech" ) );
	}
	
	public final int getCardTemplateID() { return m_CardID; }
	public final CardTypes getCardType() { return m_CardType; }
	public final StatBlock getStat( StatBlock.StatType type ) { return m_Stats.get( type ); }
	
	protected final void setCardTemplateID( int id ) { m_CardID = id; }
	protected final void setStat( StatBlock.StatType type, int value ) { if( value != -1 ) { setStat( new StatBlock( type, value ) ); } }
	protected final void setStat( StatBlock sb ) { m_Stats.put( sb.m_Type, sb ); }
	protected final void setCardType(CardTypes type) { m_CardType = type; }
	protected final void setAbilityCount( int abilities ) { m_AbilityCount = abilities; }
	
	public abstract void onEnter( GameEvent e );
	public abstract void onExit( GameEvent e );
	public abstract void onDeath( GameEvent e );
	public abstract void onActivate(  GameEvent e, int index );
	public abstract void onGlobalUpdate( GameEvent e );
	public abstract void preAttacked( AttackEvent e );
	public abstract void postAttacked( AttackEvent e );
	public abstract void preAttack( AttackEvent e );
	public abstract void postAttack( AttackEvent e );
	public abstract void preDamage( DamageEvent e );
	public abstract void postDamage( DamageEvent e);
	public abstract void onPlay( GameEvent e );
}
