package server.games.events;

import server.games.GameInstance;
import server.games.GamePlayer;
import server.games.cards.ServerCardInstance;

public class GameEvent {
	public GameInstance m_HostGame;
	
	public GamePlayer m_SourcePlayer;
	public ServerCardInstance m_SourceCard;
	
	public GamePlayer m_TargetPlayer;
	public ServerCardInstance m_TargetCard;
	
	public String m_EventString;
	
	public GameEvent( GameInstance host ) { m_HostGame = host; }
}
