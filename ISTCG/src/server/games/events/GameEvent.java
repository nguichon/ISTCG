package server.games.events;

import server.games.GameInstance;

public class GameEvent {
	public GameInstance game;
	
	public GameEvent( GameInstance g ) { game = g; }
}
