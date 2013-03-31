package server.games.cards.abilities;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import Shared.CardTypes;
import Shared.GameZones;

@XmlRootElement(name = "TargetingCondition")
public class TargetingCondition {

    @XmlElement(name = "locations")
	public boolean[] m_Location = new boolean[GameZones.values().length];
    
    @XmlElement(name = "types")
	public boolean[] m_CardType = new boolean[CardTypes.values().length];

    @XmlElement(name = "mandatory")
	public boolean m_Mandatory;

    @XmlElement(name = "player_is_targetable")
	public boolean m_CanTargetPlayer;

    @XmlElement(name = "stack_is_targetable")
	public boolean m_CanTargetStackObjects;
}
