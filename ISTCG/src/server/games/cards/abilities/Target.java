package server.games.cards.abilities;

import server.ServerMain;
import server.games.GamePlayer;
import server.games.cards.ServerCardInstance;
import server.games.stack.StackObject;

public class Target {
	private Object m_Target;
	private int m_Key;
	private TargetType m_Type;
	
	public enum TargetType { CARD, PLAYER, STACK_OBJECT }
	
	public Target( ServerCardInstance ci ) { 
		m_Type = TargetType.CARD;
		m_Target = ci;
		m_Key = ((ServerCardInstance)m_Target).TimesMoved();
	}
	public Target( StackObject so ) { 
		if( ServerCardInstance.class.isInstance( so ) ) {
			//???BAD
			ServerMain.ConsoleMessage( '?', "You done fucked up. Creating a target of a StackObject that's a really a ServerCardInstance." );
		} else {
			m_Type = TargetType.STACK_OBJECT;
			m_Target = so;
		}
	}
	public Target( GamePlayer gp ) { 
		m_Type = TargetType.PLAYER;
		m_Target = gp;
	}
	
	public TargetType getType() { return m_Type; }
	public boolean isValid() { 
		switch( m_Type ) {
		case CARD:
			return m_Key == ((ServerCardInstance)m_Target).TimesMoved();
		case PLAYER:
			return ((GamePlayer)m_Target).getState() != GamePlayer.PlayerStates.DEAD;
		case STACK_OBJECT:
			return ((StackObject)m_Target).isValid();
		default:
			break;
		}
		return false;
	}
	public GamePlayer getTargetPlayer() { if( m_Type == TargetType.PLAYER ) return (GamePlayer) m_Target; else return null; }
	public ServerCardInstance getTargetCard() { if( m_Type == TargetType.CARD) return (ServerCardInstance) m_Target; else return null; }
	public StackObject getTargetStackObject() { if( m_Type == TargetType.STACK_OBJECT ) return (StackObject) m_Target; else return null; }
}
