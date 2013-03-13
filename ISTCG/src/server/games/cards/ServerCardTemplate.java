package server.games.cards;

import java.util.Collection;
import java.util.HashMap;

import server.games.AttackEvent;
import server.games.DamageEvent;
import server.games.GameEvent;

import Shared.CardTemplates;
import Shared.StatBlock;
import Shared.StatBlock.StatType;

public abstract class ServerCardTemplate extends Shared.CardTemplates {
	private CardType m_CardType;
	private int m_CardID, m_AbilityCount;
	private HashMap<StatBlock.StatType, StatBlock> m_Stats;
	
	public int getCardTemplateID() { return m_CardID; }
	public CardType getCardType() { return m_CardType; }
	public final StatBlock getStat( StatBlock.StatType type ) { return m_Stats.get( type ); }
	
	protected void setCardTemplateID( int id ) { m_CardID = id; }
	protected void setStat( StatBlock.StatType type, int value ) { setStat( new StatBlock( type, value ) ); }
	protected void setStat( StatBlock sb ) { m_Stats.put( sb.m_Type, sb ); }
	protected void setCardType(CardType type) { m_CardType = type; }
	protected void setAbilityCount( int abilities ) { m_AbilityCount = abilities; }
	
	public abstract void onEnter();
	public abstract void onExit();
	public abstract void onDeath();
	public abstract void onActivate( int index );
	public abstract void onGlobalUpdate( GameEvent e );
	public abstract void preAttacked( AttackEvent e );
	public abstract void postAttacked( AttackEvent e );
	public abstract void preAttack( AttackEvent e );
	public abstract void postAttack( AttackEvent e );
	public abstract void preDamage( DamageEvent e );
	public abstract void postDamage( DamageEvent e);
}
