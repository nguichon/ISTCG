package server.games.cards;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import Shared.CardTemplates;
import Shared.StatBlock;
import Shared.StatBlock.StatType;

@XmlRootElement(namespace = "de.vogella.xml.jaxb.model")
public class ServerCardTemplate extends Shared.CardTemplates {
	private CardType m_CardType;
	private int m_CardID;

	@Override
	public CardTemplates LoadCard(String path) {
		try {
			final Unmarshaller reader = JAXBContext.newInstance(
					ServerCardTemplate.class).createUnmarshaller();

			return (ServerCardTemplate) reader.unmarshal(new File(path));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

	@XmlElement(name = "cardID")
	public int getCardID() {
		return m_CardID;
	}

	public void setCardID(int id) {
		m_CardID = id;
	}

	@XmlElement(name = "cardType")
	public CardType getCardType() {
		return m_CardType;
	}

	public void setCardType(CardType type) {
		m_CardType = type;
	}

	private HashMap<StatBlock.StatType, StatBlock> m_Stats;

	@XmlElementWrapper(name = "statList")
	@XmlElement(name = "stat")
	public void setStats(Collection<StatBlock> stats) {
		for( StatBlock sb : stats ) {
			m_Stats.put( sb.m_Type, sb );
		}
	}

	public Collection<StatBlock> getStats() {
		return m_Stats.values();
	}

	public final StatBlock getStat(StatType type) {
		return m_Stats.get( type );
	}
}
