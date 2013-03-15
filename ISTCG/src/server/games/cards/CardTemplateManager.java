package server.games.cards;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import server.Database;
import server.ServerMain;


public class CardTemplateManager {
	//=========
	//CONSTANTS
	//=========
	private static String DEFAULT_CARD_PATH = System.getProperty("user.dir") + "/data/cards/";
	
	//CardTemplate collection variables
	private HashMap<Integer, ServerCardTemplate> m_LoadedCards;
	
	/**
	 * Kind of a constructor for CardTemplateManager. 
	 * <pre>
	 * 		Class Database has been initialized successfully.
	 * @return
	 * 		Whether initialization was successful or not
	 */
	public boolean Initialize() {
		m_LoadedCards = new HashMap<Integer, ServerCardTemplate>();
		return LoadAllCards();
	}
	
	/**
	 * Retrieves a card from the singleton
	 * <pre>
	 * 		LoadAllCards has been called.
	 */
	public ServerCardTemplate GetCardTemplate( int id ) {
		return m_LoadedCards.get(id);
	}
	
	/**
	 * Loads a specified card by card name.
	 * @param card_name
	 */
	private void LoadCard(String card_name) {
		ServerMain.ConsoleMessage( '-', "Loading card \"" + card_name + "\"..." );
		
		try {
			final Unmarshaller reader = JAXBContext.newInstance(
					ServerCardTemplate.class).createUnmarshaller();

			ServerCardTemplate scb = (ServerCardTemplate)reader.unmarshal(new File(DEFAULT_CARD_PATH + card_name + ".scf"));
			m_LoadedCards.put( scb.getCardTemplateID(), scb );
		} catch (JAXBException e) {
			e.printStackTrace();
		}				
	}
	private boolean LoadAllCards() {
		ResultSet rs = Database.get().quickQuery("SELECT name FROM cards");
		try {
			while(rs.next()) {
				LoadCard(rs.getString(1));
			}
		} catch (SQLException e) {
			ServerMain.ConsoleMessage('!', "A card failed to load, fatal error");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//=====
	//Singleton methods
	//=====
	private static CardTemplateManager m_Instance;
	private CardTemplateManager() {
		
	}
	public static CardTemplateManager get() {
		if( m_Instance == null ) { m_Instance = new CardTemplateManager(); }
		return m_Instance;
	}
}
