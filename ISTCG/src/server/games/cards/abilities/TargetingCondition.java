package server.games.cards.abilities;

import java.util.ArrayList;

import Shared.CardTypes;
import Shared.GameZones;

public class TargetingCondition {
	private ArrayList<GameZones> m_Location;
	private ArrayList<CardTypes> m_CardType;
	private boolean m_Mandatory;
	private boolean m_CanTargetPlayer;
	private boolean m_CanTargetStackObjects;
}
