package Shared;

public abstract class CardBase implements SharedObject {
	public enum CardType {
		COMMAND_UNIT, UNIT, GEAR, RESOURCE, OPERATION
	}

	private int m_CardID;
	private CardType m_CardType;

	private int m_Attack, m_Structure, m_Defense, m_GearPoints, m_Damage,
			m_Time, m_Metal, m_Energy, m_Tech;
	
	//private Vector<Ability> abilities go here
	
	public abstract void LoadCard( String path );
	
	public int GetAttack() { return m_Attack; };
	public int GetStructure() { return m_Structure; };
	public int GetDefense() { return m_Defense; };
	public int GetGearPoints() { return m_GearPoints; };
	public int GetDamage() { return m_Damage; };
	public int GetTime() { return m_Time; };
	public int GetMetal() { return m_Metal; };
	public int GetEnergy() { return m_Energy; };
	public int GetTech() { return m_Tech; };
}
