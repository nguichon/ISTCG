package Shared;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "StatBlock")
public class StatBlock {
	public enum StatType { ATTACK, DEFENSE, POWER, STRUCTURE, HARD_POINTS, DELAY, METAL, ENERGY, TECH, UNKNOWN }


    @XmlElement(name = "StatType")
	public StatType m_Type;
    @XmlElement(name = "Value")
	public int m_Value;

    public StatBlock() {
    	m_Type = StatType.UNKNOWN;
    	m_Value = -1;
    }
	public StatBlock(StatType type, int value) {
		m_Type = type;
		m_Value = value;
	}
}
