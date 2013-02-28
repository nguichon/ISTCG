package Shared;

public abstract class CardTemplates implements SharedObject {
	public enum CardType {
		COMMAND_UNIT, UNIT, GEAR, RESOURCE, ORDER, EVENT
	}

	private int m_CardID;
	private CardType m_CardType;
	
	//private Vector<Ability> abilities go here
	
	public abstract CardTemplates LoadCard( String path );

	public int getID() { return m_CardID; };
}
