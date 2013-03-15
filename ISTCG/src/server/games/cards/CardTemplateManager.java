package server.games.cards;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

	private URLClassLoader m_TemplateLoader;
	
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
	 * @param rs
	 */
	private void LoadCard(ResultSet card) {
		try {
			ServerMain.ConsoleMessage( '-', "Loading card \"" + card.getString("name") + "\" (" + card.getInt("id") + ")..." );
		} catch (SQLException e1) {
			ServerMain.ConsoleMessage( '!', "SQL Error, couldn't find column \"name\" in result." );
			e1.printStackTrace();
		}
	
		if( m_TemplateLoader == null ) {
			ClassLoader templateLoader = ServerCardTemplate.class.getClassLoader();
			URL uri = null;
			try {
				uri = new URL(  "file://" + System.getProperty("user.dir") + "/data/cards/" );
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			URL[] uris = new URL[1];
			uris[0] = uri;
			m_TemplateLoader = new URLClassLoader( uris, templateLoader);
		}
		
		try {
			Class<ServerCardTemplate> loadedCard = (Class<ServerCardTemplate>)m_TemplateLoader.loadClass( card.getString("name").replaceAll(" ", ""));
			ServerCardTemplate sct = loadedCard.newInstance();
			sct.Initialize( card );
			m_LoadedCards.put( sct.getCardTemplateID(), sct );
		} catch (ClassNotFoundException e1) {
			ServerMain.ConsoleMessage( '!', "Class was not found." );
			e1.printStackTrace();
		} catch (SQLException e1) {
			ServerMain.ConsoleMessage( '!', "SQL Error, couldn't find some column in result." );
			e1.printStackTrace();
		} catch (InstantiationException e) {
			ServerMain.ConsoleMessage( '!', "Failed to load class." );
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			ServerMain.ConsoleMessage( '!', "Bad class accessing, baddie." );
			e.printStackTrace();
		}		
	}
	private boolean LoadAllCards() {
		ResultSet rs = Database.get().quickQuery("SELECT * FROM cards");
		try {
			while(rs.next()) {
				LoadCard(rs);
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
