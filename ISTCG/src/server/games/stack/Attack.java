package server.games.stack;

import java.util.ArrayList;

import Shared.GameZones;

import server.games.GameInstance;
import server.games.cards.ServerCardInstance;
import server.games.cards.abilities.Target;
import server.games.events.ResolutionEvent;

public class Attack extends StackObject {
	private Target m_Attacker;
	private Target m_AttackTarget;
	private ArrayList<ServerCardInstance> m_Interceptors;
	
	public Attack( GameInstance gi, ServerCardInstance attacker, ServerCardInstance defender ) { 
		super(gi); 
		
		m_Attacker = new Target( attacker );
		m_AttackTarget = new Target( defender );
	}
	
	@Override
	public void Resolve(ResolutionEvent e) {
		if( isValid() ) {
			getAttacker().MakeAttack( getDefender() );
			getDefender().MakeAttack( getAttacker() );
		}
		
		getAttacker().CheckStatus();
		getDefender().CheckStatus();
	}
	
	public ServerCardInstance getAttacker() { return m_Attacker.getTargetCard(); }
	public ServerCardInstance getDefender() { return m_AttackTarget.getTargetCard(); }

	@Override
	public boolean isValid() {
		return  m_Attacker.isValid() && m_AttackTarget.isValid();
	}

}
