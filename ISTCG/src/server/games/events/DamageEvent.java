package server.games.events;

import server.games.GameInstance;
import server.games.cards.ServerCardInstance;
import server.games.stack.AbilityInstance;

public class DamageEvent extends GameEvent {
	public int amount;
	public ServerCardInstance sourceCard;
	public AbilityInstance sourceAbility;
	public ServerCardInstance damagedCard;

	public DamageEvent(GameInstance g) {
		super(g);
	}
}
