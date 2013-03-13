package server.games.cards;

import java.util.HashMap;

import server.games.AttackEvent;
import server.games.DamageEvent;
import server.games.GameEvent;

import Shared.CardTypes;
import Shared.GameResources;
import Shared.StatBlock;

public abstract class ServerCardTemplate {
	private CardTypes m_CardType;
	private int m_CardID, m_AbilityCount;
	private HashMap<StatBlock.StatType, StatBlock> m_Stats;
	private int[] m_Cost = new int[3];
	
	public ServerCardTemplate( int id, int atk, int pow, int def, int str, int hp, int delay, int[] cost ) { 
		Initialize( id, atk, pow, def, str, hp, delay, cost ); }
	protected abstract void Initialize( int id, int atk, int pow, int def, int str, int hp, int delay, int[] cost );
	
	public final int getCardTemplateID() { return m_CardID; }
	public final CardTypes getCardType() { return m_CardType; }
	public final StatBlock getStat( StatBlock.StatType type ) { return m_Stats.get( type ); }
	public final int[] getCost() { return m_Cost; }
	
	protected final void setCardTemplateID( int id ) { m_CardID = id; }
	protected final void setStat( StatBlock.StatType type, int value ) { setStat( new StatBlock( type, value ) ); }
	protected final void setStat( StatBlock sb ) { m_Stats.put( sb.m_Type, sb ); }
	protected final void setCardType(CardTypes type) { m_CardType = type; }
	protected final void setAbilityCount( int abilities ) { m_AbilityCount = abilities; }
	protected final void setCost( GameResources res, int value ) { m_Cost[res.ordinal()] = value; }
	
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
