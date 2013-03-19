package NewClient;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import Client.ImageManager;
import Shared.CardTypes;
import Shared.StatBlock;

@XmlRootElement(namespace = "card template")
public class ClientCardTemplate {
    public enum CardRenderSize {
	    TINY(30, 40), SMALL(90, 120), MEDIUM(180, 240), LARGE(300, 400);
	
	    private int m_SizeX;
	    private int m_SizeY;
	
	    private CardRenderSize(int x, int y) {
	        m_SizeX = x;
	        m_SizeY = y;
	    }
	
	    public int getWidth() { return m_SizeX; }
	
	    public int getHeight() { return m_SizeY; }
    }
    private enum CardImageAssets {
    	CARD_CORNER_BOTTOM_LEFT( "card_border_corner_bottom_left.png" ), 
    	CARD_CORNER_BOTTOM_RIGHT( "card_border_corner_bottom_right.png" ), 
    	CARD_CORNER_TOP_LEFT( "card_border_corner_top_left.png" ), 
    	CARD_CORNER_TOP_RIGHT( "card_border_corner_top_right.png" ), 
    	CARD_EDGE_LEFT( "card_border_edge_left.png" ),
    	CARD_EDGE_RIGHT( "card_border_edge_right.png" ),
    	CARD_EDGE_TOP( "card_border_edge_top.png" ),
    	CARD_EDGE_BOTTOM( "card_border_edge_bottom.png" ),
    	TEXT_CORNER_BOTTOM_LEFT( "text_box_corner_bottom_left.png" ), 
    	TEXT_CORNER_BOTTOM_RIGHT( "text_box_corner_bottom_right.png" ), 
    	TEXT_CORNER_TOP_LEFT( "text_box_corner_top_left.png" ), 
    	TEXT_CORNER_TOP_RIGHT( "text_box_corner_top_right.png" ), 
    	TEXT_EDGE_LEFT( "text_box_edge_left.png" ),
    	TEXT_EDGE_RIGHT( "text_box_edge_right.png" ),
    	TEXT_EDGE_TOP( "text_box_edge_top.png" ),
    	TEXT_EDGE_BOTTOM( "text_box_edge_bottom.png" );
    	
    	private CardImageAssets( String path ) { m_ImagePath = path; }
    	private String m_ImagePath;
    	public String path() { return m_ImagePath; }
    }

    private CardTypes m_CardType;
    private int m_CardID;
    private ArrayList<StatBlock> m_Stats;
    private String m_CardName;
    private String m_CardFlavor;
    private String m_CardText;

    // private static Display display = null;
    
    private String m_BGImage;

    /**
     * This method draws a card using the passed GC. It will use the default
     * stat blocks stored in this class unless overwritten by passed StatBlock
     * 
     * @param targetGC
     *            GC to use when drawing this card.
     * @param stats
     *            StatBlocks to overwrite, can be empty or have previously
     *            unknown StatBlocks.
     */
    public void Render(GC targetGC, CardRenderSize size, ArrayList<StatBlock> stats) {
    	Image toDraw = ImageManager.get().GetImage( m_BGImage );
        targetGC.drawImage( toDraw, 
        		0, 0, toDraw.getBounds().width, toDraw.getBounds().height,
        		0, 0, size.getWidth(), size.getHeight());
        targetGC.drawText( m_CardName, 0, 0, SWT.NONE);//SWT.DRAW_TRANSPARENT);
    }
    public static void RenderBlack(GC targetGC, CardRenderSize size, ArrayList<StatBlock> stats) {
    	final Color black = new Color(targetGC.getDevice(), SWT.COLOR_BLACK, 0, 0);
    	targetGC.setForeground( black );
    	targetGC.setBackground( black );
    	targetGC.fillRectangle( 0, 0, size.getWidth(), size.getHeight() );
    }

    /**
     * Generates a full text version of this card.
     * 
     * @param overwriteStats
     *            StatBlocks to overwrite, can be empty or have previously
     *            unknown StatBlocks
     * @return Returns a String containing all the card information,
     *         Name/Type/Cost/Abilities/Flavor etc.
     */
    public String getCardTextCopy(StatBlock... overwriteStats) {
        return 
        		m_CardName + "\n" + 
        		m_CardType.toString() + "\n" + 
        		m_CardText;
    }
    
    public String getStat( String statType ) {
    	return getStat( StatBlock.StatType.valueOf( statType ) );
    }
    public String getStat( StatBlock.StatType statType ) {
    	for( StatBlock sb : m_Stats ) {
    		if ( sb.m_Type == statType ) { return String.valueOf( sb.m_Value ); }
    	}
    	
    	return null;
    }

    // ==============================
    // XML FUNCTIONS: Don't fuck them up.
    // ==============================
    @XmlElement(name = "cardID")
    public int getCardID() {
        return m_CardID;
    }

    public void setCardName(String name) {
        m_CardName = name;
    }

    public void setCardID(int id) {
        m_CardID = id;
    }

    @XmlElement(name = "cardType")
    public CardTypes getCardType() {
        return m_CardType;
    }

    public void setCardType(CardTypes type) {
        m_CardType = type;
    }

    @XmlElement(name = "cardArt")
    public String getBGImage() {
        return m_BGImage;
    }

    public void setBGImage(String path) {
        m_BGImage = path;
    }
    
    @XmlElement(name = "cardText")
    public String getCardText() {
        return m_CardText;
    }

    public void setCardText(String text) {
        m_CardText = text;
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
