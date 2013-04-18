package NewClient;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

public class Settings {
	private HashMap<String,String> m_LoadedSettings = new HashMap<String,String>();
	private static Settings m_Instance = null;
	private Settings() { 
		try {
			FileReader f = new FileReader( "settings.txt" );
			
			Scanner s = new Scanner( f );
			
			while( s.hasNextLine() ) {
				String text = s.nextLine();
				if( text.trim().length() > 0 && !( text.charAt(0) == '#' ) )
					m_LoadedSettings.put( text.substring( 0, text.indexOf( ':' ) ).trim(),  text.substring( text.indexOf( ':' ) + 1 ).trim() );
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Settings get() {
		if( m_Instance == null ) 
			m_Instance = new Settings();
		return m_Instance;
	}
	
	public int getInteger( String key ) {
		return Integer.valueOf( m_LoadedSettings.get( key ) );
	}
	public String getString( String key ) {
		return m_LoadedSettings.get( key );
	}
}
