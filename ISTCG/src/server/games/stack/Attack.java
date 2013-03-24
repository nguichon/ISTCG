package server.games.stack;

import java.util.ArrayList;

import server.games.GameInstance;
import server.games.cards.ServerCardInstance;
import server.games.events.ResolutionEvent;

public class Attack extends StackObject {
	private ServerCardInstance m_Attacker;
	private ServerCardInstance m_AttackTarget;
	private ArrayList<ServerCardInstance> m_Interceptors;
	
	public Attack( GameInstance gi ) { super(gi); }
	
	@Override
	public void Resolve(ResolutionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
