package Client;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.eclipse.swt.graphics.GC;
import Shared.StatBlock;
import Shared.CardTemplates.CardType;

public class CardTemplate {
	private String m_BGImage;
	private CardType m_CardType;
	private int m_CardID;
	private ArrayList<StatBlock> m_Stats;
	private String m_CardName;
	private String m_CardFlavor;
	
	public enum CardRenderSize {
		TINY(9,12), SMALL(30,40), MEDIUM(90,120), LARGE(300,400);
		
		private int m_SizeX;
		private int m_SizeY;
		private CardRenderSize( int x, int y ) {
			m_SizeX = x;
			m_SizeY = y;
		}
		
		public int getWidth() { return m_SizeX; }
		public int getHeight() { return m_SizeY; }
	}
	
	/**
	 * This method draws a card using the passed GC. It will use the default 
	 * stat blocks stored in this class unless overwritten by passed StatBlock
	 * @param targetGC
	 * 		GC to use when drawing this card.
	 * @param overwriteStats
	 * 		StatBlocks to overwrite, can be empty or have previously unknown StatBlocks.
	 */
	public void Render( GC targetGC, CardRenderSize size, StatBlock...overwriteStats ) {
		targetGC.drawRectangle(0, 0, size.getWidth(), size.getHeight() );
	}
	
	/**
	 * Generates a full text version of this card.
	 * @param overwriteStats
	 * 		StatBlocks to overwrite, can be empty or have previously unknown StatBlocks
	 * @return
	 * 		Returns a String containing all the card information, Name/Type/Cost/Abilities/Flavor etc.
	 */
	public String GetCardTextCopy( StatBlock...overwriteStats) {
		return "";
	}
	
	
	//==============================
	//XML FUNCTIONS: Don't fuck them up.
	//==============================
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
	
	@XmlElement(name = "cardArt")
	public String getBGImage() {
		return m_BGImage;
	}

	public void setBGImage(String path) {
		m_BGImage = path;
	}
	
	@XmlElement(name = "cardName")
	public String getCardName() {
		return m_CardName;
	}

	public void setCardType(String name) {
		m_CardName = name;
	}

	@XmlElement(name = "cardFlavor")
	public String getCardFlavor() {
		return m_CardFlavor;
	}

	public void setCardFlavor(String flavorText) {
		m_CardFlavor = flavorText;
	}

	@XmlElementWrapper(name = "statList")
	@XmlElement(name = "stat")
	public void setStats(ArrayList<StatBlock> stats) {
		this.m_Stats = stats;
	}

	public ArrayList<StatBlock> getStats() {
		return m_Stats;
	}
}
