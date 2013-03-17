package server.games.events;

import server.games.Game;
import server.games.GamePlayer;
import server.games.cards.CardInstance;

public class GameEvent {
	private Game m_HostGame;
	
	private GamePlayer m_SourcePlayer;
	private CardInstance m_SourceCard;
	
	private GamePlayer m_TargetPlayer;
	private CardInstance m_TargetCard;
	
	private String m_EventString;
	
	public GamePlayer getSourcePlayer() { return m_SourcePlayer; }
	public CardInstance getSourceCard() { return m_SourceCard; }
	public Game getHostGame() { return m_HostGame; }
}
