package server.games.cards.abilities;

import server.games.GamePlayer;
import server.games.cards.ServerCardInstance;
import server.games.stack.StackObject;

public class Target {
	public Object m_Target;
	public TargetType m_Type;
	public enum TargetType { CARD, PLAYER, STACK_OBJECT }
	
	public Target( ServerCardInstance ci ) { 
		m_Type = TargetType.CARD;
		m_Target = ci;
	}
	public Target( StackObject so ) { 
		m_Type = TargetType.STACK_OBJECT;
		m_Target = so;
	}
	public Target( GamePlayer gp ) { 
		m_Type = TargetType.PLAYER;
		m_Target = gp;
	}
	
	public TargetType getType() { return m_Type; }
	public GamePlayer getTargetPlayer() { if( m_Type == TargetType.PLAYER ) return (GamePlayer) m_Target; else return null; }
	public ServerCardInstance getTargetCard() { if( m_Type == TargetType.CARD) return (ServerCardInstance) m_Target; else return null; }
	public StackObject getTargetStackObject() { if( m_Type == TargetType.STACK_OBJECT ) return (StackObject) m_Target; else return null; }
}
