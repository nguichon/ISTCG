package server.games.events;

import server.games.GameInstance;
import server.games.cards.ServerCardInstance;

public class ResolutionEvent extends GameEvent {
	public ServerCardInstance resolvingCard;
	
	public ResolutionEvent(GameInstance g) { super(g); }
}
