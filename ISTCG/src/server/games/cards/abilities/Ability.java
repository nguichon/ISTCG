package server.games.cards.abilities;

import java.util.ArrayList;

import server.games.stack.AbilityInstance;

public abstract class Ability {
	ArrayList<TargetingCondition> m_AbilityTargets;
	
	public Ability() {
		m_AbilityTargets = new ArrayList<TargetingCondition>();
	}
	
	public boolean ValidateTargets( ArrayList<Target> targets ) {
		return false;
	}
	public boolean ValidateTargets( AbilityInstance ai ) {
		return ValidateTargets( ai.getTargets() );
	}
	protected void AddNewTargetingCondition( TargetingCondition tc ) {
		m_AbilityTargets.add( tc );
	}
}


/* TODO
 * Validate a set of targets
 * Resolve
 */