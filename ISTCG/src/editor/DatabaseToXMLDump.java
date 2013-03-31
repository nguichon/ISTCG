package editor;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import NewClient.ClientCardTemplate;
import Shared.StatBlock;
import Shared.TargetingCondition;
import Shared.StatBlock.StatType;

import server.Database;
import server.ServerMain;

public class DatabaseToXMLDump {
	private static String DEFAULT_CARD_PATH = System.getProperty("user.dir") + "/data/cards/card_";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Database.initialize();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		ResultSet rs = Database.get().quickQuery( "SELECT * FROM CARDS;" );
		
		try {
			while(rs.next()) {
				CreateCard(rs);
			}
		} catch (SQLException e) {
			ServerMain.ConsoleMessage('!', "A card failed to load, fatal error");
			e.printStackTrace();
		}
	}
	
	public static void CreateCard( ResultSet rs ) throws SQLException {
		ClientCardTemplate nct = new ClientCardTemplate();
		nct.setBGImage( rs.getString( "name") + ".png" );
		nct.setCardName( rs.getString( "name") );
		nct.setCardFlavor( rs.getString( "flavor_text" ) );
		nct.setCardText( rs.getString( "description" ) );
		nct.setCardType( Shared.CardTypes.valueOf(rs.getString( "type" )) );
		nct.setCardID( rs.getInt( "id" ) );
		
		ArrayList<StatBlock> stats = new ArrayList<StatBlock>();
		
		ParseStat( stats, "attack", rs );
		ParseStat( stats, "power", rs );
		ParseStat( stats, "defense", rs );
		ParseStat( stats, "structure", rs );
		ParseStat( stats, "hard_points", rs );
		ParseStat( stats, "delay", rs );
		ParseStat( stats, "metal", rs );
		ParseStat( stats, "energy", rs );
		ParseStat( stats, "tech", rs );
		
		nct.setStats( stats );
		
		Marshaller writer;
		try {
			writer = JAXBContext.newInstance(
					ClientCardTemplate.class).createMarshaller();
			writer.marshal( nct, new File(DEFAULT_CARD_PATH + nct.getCardID() + ".xml" ) );
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void ParseStat( ArrayList<StatBlock> stats, String stat, ResultSet rs ) throws SQLException {
		int value = rs.getInt( stat );
		if( value != -1 ) {
			stats.add( new StatBlock( StatType.valueOf( stat.toUpperCase() ), value ) );
		}
	}

}
