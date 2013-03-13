package server.games;

import server.games.cards.CardInstance;

public class GameEvent {
	private GamePlayer m_SourcePlayer;
	private CardInstance m_SourceCard;
	
	private GamePlayer m_TargetPlayer;
	private CardInstance m_TargetCard;
	
	private String m_EventString;
	
	public GamePlayer getSourcePlayer() { return m_SourcePlayer; }
}
