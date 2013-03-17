package server.games.events;

import server.games.GameInstance;
import server.games.GamePlayer;
import server.games.cards.ServerCardInstance;

public class GameEvent {
	private GameInstance m_HostGame;
	
	private GamePlayer m_SourcePlayer;
	private ServerCardInstance m_SourceCard;
	
	private GamePlayer m_TargetPlayer;
	private ServerCardInstance m_TargetCard;
	
	private String m_EventString;
	
	public GamePlayer getSourcePlayer() { return m_SourcePlayer; }
	public ServerCardInstance getSourceCard() { return m_SourceCard; }
	public GameInstance getHostGame() { return m_HostGame; }
}
