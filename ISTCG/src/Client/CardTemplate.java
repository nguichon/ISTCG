package Client;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import Client.ImageManager;
import Shared.CardTypes;
import Shared.StatBlock;

public class CardTemplate {
    private String m_BGImage;
    private CardTypes m_CardType;
    private int m_CardID;
    private ArrayList<StatBlock> m_Stats;
    private String m_CardName;
    private String m_CardFlavor;

    private static Display display = null;

    private static final String CARD_BL = "card_border_corner_bottom_left.png";
    private static Image cardBR = new Image(display,
            System.getProperty("user.dir")
                    + "/data/card_border_corner_bottom_right.png");
    private static Image cardTL = new Image(display,
            System.getProperty("user.dir")
                    + "/data/card_border_corner_top_left.png");
    private static Image cardTR = new Image(display,
            System.getProperty("user.dir")
                    + "/data/card_border_corner_top_right.png");
    private static Image cardL = new Image(display,
            System.getProperty("user.dir") + "/data/card_border_edge_left.png");
    private static Image cardR = new Image(display,
            System.getProperty("user.dir") + "/data/card_border_edge_right.png");
    private static Image cardT = new Image(display,
            System.getProperty("user.dir") + "/data/card_border_edge_top.png");
    private static Image cardB = new Image(display,
            System.getProperty("user.dir")
                    + "/data/card_border_edge_bottom.png");
    private static Image textBL = new Image(display,
            System.getProperty("user.dir")
                    + "/data/text_box_corner_bottom_left.png");
    private static Image textBR = new Image(display,
            System.getProperty("user.dir")
                    + "/data/text_box_corner_bottom_right.png");
    private static Image textTL = new Image(display,
            System.getProperty("user.dir")
                    + "/data/text_box_corner_top_left.png");
    private static Image textTR = new Image(display,
            System.getProperty("user.dir")
                    + "/data/text_box_corner_top_right.png");
    private static Image textL = new Image(display,
            System.getProperty("user.dir") + "/data/text_box_edge_left.png");
    private static Image textR = new Image(display,
            System.getProperty("user.dir") + "/data/text_box_edge_righ.png");
    private static Image textT = new Image(display,
            System.getProperty("user.dir") + "/data/text_box_edge_top.png");
    private static Image textB = new Image(display,
            System.getProperty("user.dir") + "/data/text_box_edge_bottom.png");
    private static Image textM = new Image(display,
            System.getProperty("user.dir") + "/data/text_box_body.png");

    public enum CardRenderSize {
    TINY(9, 12), SMALL(30, 40), MEDIUM(90, 120), LARGE(300, 400);

    private int m_SizeX;
    private int m_SizeY;

    private CardRenderSize(int x, int y) {
        m_SizeX = x;
        m_SizeY = y;
    }

    public int getWidth() {
        return m_SizeX;
    }

    public int getHeight() {
        return m_SizeY;
    }
    }

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
    public void Render(GC targetGC, CardRenderSize size,
            ArrayList<StatBlock> stats) {
        double factor = 1;
        // targetGC.drawRectangle(0, 0, size.getWidth(), size.getHeight() );
        // need a display to load images

        String png = System.getProperty("user.dir") + "/data/"
                + "text_box_body" + ".png";
        Image back = new Image(display, png);

        ImageManager.get().GetImage(CARD_BL);

        switch (size) {
        case TINY:
            factor = .03;
            Image backT = new Image(display, back.getImageData().scaledTo(
                    (int) (300 * factor), (int) (400 * factor)));
            // targetGC.addMouseListener(new MouseListener);
            targetGC.drawImage(backT, 0, 0);
            // TODO stuff here
            break;
        case SMALL:
            factor = .1;
            Image backS = new Image(display, back.getImageData().scaledTo(
                    (int) (300 * factor), (int) (400 * factor)));
            targetGC.drawImage(backS, 0, 0);
            // TODO stuff here
            break;
        case MEDIUM:
            factor = .3;
            Image backM = new Image(display, back.getImageData().scaledTo(
                    (int) (300 * factor), (int) (400 * factor)));
            targetGC.drawImage(backM, 0, 0);
            // TODO stuff here
            break;
        case LARGE:
            // Loading Images needed
            for (int i = 0; i < m_Stats.size() - 1; i++) {
                int x = 10;
                int y = 225;
            }
            // TODO stuff here
            break;
        }
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
    public String GetCardTextCopy(StatBlock... overwriteStats) {
        return "";
    }

    // ==============================
    // XML FUNCTIONS: Don't fuck them up.
    // ==============================
    @XmlElement(name = "cardID")
    public int getCardID() {
        return m_CardID;
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
