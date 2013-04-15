package NewClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class ClientCardTemplateManager {
    // =========
    // CONSTANTS
    // =========
	private static String OS = System.getProperty("os.name").toLowerCase();
    private static String DEFAULT_CARD_PATH = System.getProperty("user.dir") + (OS.indexOf("mac")>=0?"/ISTCG/data/cards/card_":"/data/cards/card_");
    
    //System.getProperty("user.dir") + (OS.indexOf("mac")>=0?"/ISTCG/data/cards/":"/data/cards/");

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

	public ArrayList<ClientCardTemplate> GetAllCards() {
		return new ArrayList<ClientCardTemplate>(m_LoadedCards.values());
	}

	public ClientCardTemplate NewTemplate(int i) {
		ClientCardTemplate ct = new ClientCardTemplate();
		ct.setCardID( i );
		m_LoadedCards.put( i, ct );
		return ct;
	}
}
