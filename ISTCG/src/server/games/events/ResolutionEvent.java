package server.games.events;

import java.util.ArrayList;

import Shared.GameZones;
import server.games.GameInstance;
import server.games.cards.ServerCardInstance;
import server.games.cards.abilities.Target;

public class ResolutionEvent extends GameEvent {
	public ServerCardInstance resolvingCard;
	public GameZones locationAfterResolution = GameZones.GRAVEYARD;
	public ArrayList<Target> targets;
	
	public ResolutionEvent(GameInstance g) { super(g); }
}
