package server.games.stack;

import java.util.ArrayList;

import server.games.GameInstance;
import server.games.cards.abilities.Target;
import server.games.events.ResolutionEvent;

public class AbilityInstance extends StackObject {
	private ArrayList<Target> m_Targets;
	
	public AbilityInstance( GameInstance gi ) { super(gi); }
	
	public ArrayList<Target> getTargets( ) { return m_Targets; }

	@Override
	public void Resolve(ResolutionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
