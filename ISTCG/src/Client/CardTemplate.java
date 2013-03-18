package Client;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.swt.graphics.GC;

import Shared.CardTypes;
import Shared.StatBlock;

@XmlRootElement(namespace = "card template")
public class CardTemplate {

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

    private CardTypes m_CardType;
    private int m_CardID;
    private ArrayList<StatBlock> m_Stats;
    private String m_CardName;
    private String m_CardFlavor;

    // private static Display display = null;
    private static final String cardBL = "card_border_corner_bottom_left.png";
    private static final String cardBR = "card_border_corner_bottom_right.png";
    private static final String cardTL = "card_border_corner_top_left.png";
    private static final String cardTR = "card_border_corner_top_right.png";
    private static final String cardL = "card_border_edge_left.png";
    private static final String cardR = "card_border_edge_right.png";
    private static final String cardT = "card_border_edge_top.png";
    private static final String cardB = "card_border_edge_bottom.png";
    private static final String textBL = "text_box_corner_bottom_left.png";
    private static final String textBR = "text_box_corner_bottom_right.png";
    private static final String textTL = "text_box_corner_top_left.png";
    private static final String textTR = "text_box_corner_top_right.png";
    private static final String textL = "text_box_edge_left.png";
    private static final String textR = "text_box_edge_righ.png";
    private static final String textT = "text_box_edge_top.png";
    private static final String textB = "text_box_edge_bottom.png";
    private static final String textM = "text_box_body.png";
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
    public void Render(GC targetGC, CardRenderSize size,
            ArrayList<StatBlock> stats) {
        double factor = 1;
        // targetGC.drawRectangle(0, 0, size.getWidth(), size.getHeight() );
        // need a display to load images

        /*
         * String png = System.getProperty("user.dir") + "/data/" +
         * "text_box_body" + ".png"; Image back = new Image(display, png);
         */

        // targetGC.drawImage(ImageManager.get().GetImage(m_BGImage), 0, 0);
        // ImageManager.get().GetImage(cardBL);

        switch (size) {
        case TINY:
            factor = .03;
            /*
             * Image backT = new Image(display, back.getImageData().scaledTo(
             * (int) (300 * factor), (int) (400 * factor)));
             * targetGC.addMouseListener(new MouseListener);
             */
            targetGC.drawImage(ImageManager.get().GetImage(m_BGImage), 0, 0,
                    300, 400, 0, 0, (int) (300 * factor), (int) (400 * factor));
            // TODO stuff here
            break;
        case SMALL:
            factor = .1;
            targetGC.drawImage(ImageManager.get().GetImage(m_BGImage), 0, 0,
                    300, 400, 0, 0, (int) (300 * factor), (int) (400 * factor));
            /*
             * Image backS = new Image(display, back.getImageData().scaledTo(
             * (int) (300 * factor), (int) (400 * factor)));
             * targetGC.drawImage(backS, 0, 0);
             */
            // TODO stuff here
            break;
        case MEDIUM:
            factor = .3;
            targetGC.drawImage(ImageManager.get().GetImage(m_BGImage), 0, 0,
                    300, 400, 0, 0, (int) (300 * factor), (int) (400 * factor));
            /*
             * Image backM = new Image(display, back.getImageData().scaledTo(
             * (int) (300 * factor), (int) (400 * factor)));
             * targetGC.drawImage(backM, 0, 0);
             */
            // TODO stuff here
            break;
        case LARGE:

            // BG image
            factor = 1;
            targetGC.drawImage(ImageManager.get().GetImage(m_BGImage), 0, 0,
                    300, 400, 0, 0, (int) (300 * factor), (int) (400 * factor));
            // Name box
            targetGC.drawImage(ImageManager.get().GetImage(textL), 10, 10);
            targetGC.drawImage(ImageManager.get().GetImage(textBL), 10, 31);
            for (int i = 31; i < 300; i += 21) {
                targetGC.drawImage(ImageManager.get().GetImage(textM), i, 10);
                targetGC.drawImage(ImageManager.get().GetImage(textB), i, 31);
            }
            targetGC.drawText(m_CardName, 25, 20, true);
            // Stat boxes (empty)
            for (int i = 0; i < m_Stats.size() - 1; i++) {
                int x = i * 45 + 10;
                int y = 225;
                targetGC.drawImage(ImageManager.get().GetImage(textTL), x, y);
                targetGC.drawImage(ImageManager.get().GetImage(textT), x + 21,
                        y);
                targetGC.drawImage(ImageManager.get().GetImage(textTR), x + 42,
                        y);
                targetGC.drawImage(ImageManager.get().GetImage(textBL), x,
                        y + 21);
                targetGC.drawImage(ImageManager.get().GetImage(textB), x + 21,
                        y + 21);
                targetGC.drawImage(ImageManager.get().GetImage(textBR), x + 42,
                        y + 21);
            }
            // Text box
            targetGC.drawImage(ImageManager.get().GetImage(textTL), 10, 250);
            for (int i = 31; i < 300; i += 21) {
                targetGC.drawImage(ImageManager.get().GetImage(textT), i, 250);
            }
            for (int i = 271; i < 400; i += 21) {
                targetGC.drawImage(ImageManager.get().GetImage(textL), 10, i);
                for (int j = 31; j < 300; j += 21) {
                    targetGC.drawImage(ImageManager.get().GetImage(textM), j, i);
                }
            }
            // TODO Border
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
