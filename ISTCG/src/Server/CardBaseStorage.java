package Server;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class CardBaseStorage {
	HashMap<Integer, ServerCardBase> m_LoadedCards;
	
	public void Initialize() {
		m_LoadedCards = new HashMap<Integer, ServerCardBase>();
		LoadAllCards();
	}
	private void LoadCard(String card_name) {
		ServerMain.ConsoleMessage( '-', "Loading card \"" + card_name + "\"..." );
		
		try {
			final Unmarshaller reader = JAXBContext.newInstance(
					ServerCardBase.class).createUnmarshaller();

			ServerCardBase scb = (ServerCardBase)reader.unmarshal(new File("ISTCG/data/cards/" + card_name + ".scf"));
			m_LoadedCards.put( scb.getCardID(), scb );
		} catch (JAXBException e) {
			e.printStackTrace();
		}				
	}
	private void LoadAllCards() {
		ResultSet rs = Database.get().quickQuery("SELECT card_name FROM cards");
		try {
			while(rs.next()) {
				LoadCard(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//=====
	//Singleton methods
	//=====
	private static CardBaseStorage m_Instance;
	private CardBaseStorage() {
		
	}
	public static CardBaseStorage get() {
		if( m_Instance == null ) { m_Instance = new CardBaseStorage(); }
		return m_Instance;
	}
}
