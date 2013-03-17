package Shared;

public enum GameZones { UNKNOWN( true ), HAND( true ), DECK( true ), GRAVEYARD( false ), FIELD( false ), DELAYED( false ), STACK( false ); 

	private boolean m_IsHiddenZone;
	
	private GameZones( boolean hiddenZone ) { m_IsHiddenZone = hiddenZone; }
	public boolean isHiddenZone() { return m_IsHiddenZone; }
}

//WHY WONT YOU COMMIT
