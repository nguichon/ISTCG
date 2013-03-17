package Shared;

public class StatBlock {
	public enum StatType { ATTACK, DEFENSE, DAMAGE, STRUCTURE, GEAR_POINTS, DELAY, METAL, ENERGY, TECH }

	
	public StatType m_Type;
	public int m_Value;

	public StatBlock(StatType type, int value) {
		m_Type = type;
		m_Value = value;
	}
}
