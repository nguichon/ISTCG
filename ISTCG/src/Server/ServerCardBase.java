package Server;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import Shared.CardBase;
import Shared.StatBlock;

@XmlRootElement(namespace = "de.vogella.xml.jaxb.model")
public class ServerCardBase extends Shared.CardBase {
	private CardType m_CardType;
	private int m_CardID;

	@Override
	public CardBase LoadCard(String path) {
		try {
			final Unmarshaller reader = JAXBContext.newInstance(
					ServerCardBase.class).createUnmarshaller();

			return (ServerCardBase) reader.unmarshal(new File(path));
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

	private ArrayList<StatBlock> m_Stats;

	@XmlElementWrapper(name = "statList")
	@XmlElement(name = "stat")
	public void setStats(ArrayList<StatBlock> stats) {
		this.m_Stats = stats;
	}

	public ArrayList<StatBlock> getStats() {
		return m_Stats;
	}
}
