package Shared;

public enum PlayerStates { 
	JOINED, 		// Player joined, needs to submit decklist.
	READY, 			// Decks submitted, waiting for other players.
	ACTIVE, 		// Waiting for this player to do things.
	WAITING, 		// This player is waiting for things to happen.
	DONE,			// Player is ready to let stack resolve.
	DISCONNECTED, 	// Disconnected, do not send messages pl0x.
	READING,
	DEAD; 			// This guy is dead, what a scrub like that Magnus.
}			