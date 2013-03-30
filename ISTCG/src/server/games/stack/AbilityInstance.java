package server.games.stack;

import java.util.ArrayList;

import server.games.GameInstance;
import server.games.cards.ServerCardInstance;
import server.games.cards.abilities.Ability;
import server.games.cards.abilities.Target;
import server.games.events.ResolutionEvent;

public class AbilityInstance extends StackObject {
	private Ability m_Ability;
	private ServerCardInstance m_Source;
	private ArrayList<Target> m_Targets;
	
	public AbilityInstance( GameInstance gi, Ability ability, ServerCardInstance source ) { 
		super(gi); 
		m_Ability = ability;
		m_Source = source;
	}
	
	public ArrayList<Target> getTargets( ) { return m_Targets; }

	@Override
	public void Resolve(ResolutionEvent e) {
		m_Ability.resolve( m_Source, m_Targets );
	}

	@Override
	public boolean isValid() {
		for( Target t : m_Targets ) {
			if (!t.isValid()) return false;
		}
		return true;
	}
}
