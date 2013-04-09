package Shared;

public enum GameStates { 	
	CREATED, 			// Game has been created, waiting for players to submit decks.
	STARTING, 			// Decks submited, commencing setup.
	ACTIVE, 			// Turn is waiting for main player.
	STACKING, 			// Cards are on stack, waiting for responses
	WAITING, 			// Waiting for passed input from a player while stacking
	RESOLVING, 			// Resolving cards on the stack
	BETWEEN_TURNS, 		// Switching active players
	ENDED; 				// Game is over. Done. Finished.
}			