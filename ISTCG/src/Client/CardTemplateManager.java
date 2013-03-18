package Client;

import java.io.File;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class CardTemplateManager {
    // =========
    // CONSTANTS
    // =========
    private static String DEFAULT_CARD_PATH = System.getProperty("user.dir")
            + "/data/cards/card_";

    // CardTemplate collection variables
    private HashMap<Integer, CardTemplate> m_LoadedCards;

    /**
     * Kind of a constructor for CardTemplateManager.
     * 
     * <pre>
     * 		Class Database has been initialized successfully.
     * @return
     * Whether initialization was successful or not
     */
    public boolean Initialize() {
        m_LoadedCards = new HashMap<Integer, CardTemplate>();
        return true;
    }

    /**
     * Retrieves a card from the singleton
     */
    public CardTemplate GetCardTemplate(int id) {
        CardTemplate toReturn = m_LoadedCards.get(id);
        if (toReturn == null) {
            try {
                final Unmarshaller reader = JAXBContext.newInstance(
                        CardTemplate.class).createUnmarshaller();
                CardTemplate ct = (CardTemplate) reader.unmarshal(new File(
                        DEFAULT_CARD_PATH + id + ".xml"));
                m_LoadedCards.put(ct.getCardID(), ct);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return toReturn;
    }

    // =====
    // Singleton methods
    // =====
    private static CardTemplateManager m_Instance;

    private CardTemplateManager() {

    }

    public static CardTemplateManager get() {
        if (m_Instance == null) {
            m_Instance = new CardTemplateManager();
        }
        return m_Instance;
    }
}
