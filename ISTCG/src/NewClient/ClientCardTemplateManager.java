package NewClient;

import java.io.File;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class ClientCardTemplateManager {
    // =========
    // CONSTANTS
    // =========
    private static String DEFAULT_CARD_PATH = System.getProperty("user.dir") + "/data/cards/card_";

    // ClientCardTemplate collection variables
    private HashMap<Integer, ClientCardTemplate> m_LoadedCards;

    /**
     * Kind of a constructor for ClientCardTemplateManager.
     * 
     * <pre>
     * 		Class Database has been initialized successfully.
     * @return
     * Whether initialization was successful or not
     */
    public boolean Initialize() {
        m_LoadedCards = new HashMap<Integer, ClientCardTemplate>();
        return true;
    }

    /**
     * Retrieves a card from the singleton
     */
    public ClientCardTemplate GetClientCardTemplate(int id) {
        ClientCardTemplate toReturn = m_LoadedCards.get(id);
        if (toReturn == null) {
            try {
                final Unmarshaller reader = JAXBContext.newInstance(
                        ClientCardTemplate.class).createUnmarshaller();
                ClientCardTemplate ct = (ClientCardTemplate) reader.unmarshal(new File(
                        DEFAULT_CARD_PATH + id + ".xml"));
                toReturn = ct;
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
    private static ClientCardTemplateManager m_Instance;

    private ClientCardTemplateManager() { }

    public static ClientCardTemplateManager get() {
        if (m_Instance == null) {
            m_Instance = new ClientCardTemplateManager();
        }
        return m_Instance;
    }
}
