package Shared;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "StatBlock")
public class StatBlock {
	public enum StatType { ATTACK, DEFENSE, DAMAGE, STRUCTURE, GEAR_POINTS, DELAY, METAL, ENERGY, TECH }
	
	@XmlElement(name = "StatType")
	public StatType m_Type;
	@XmlElement(name = "Value")
	public int m_Value;
}
