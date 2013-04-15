package OldClient;

import java.util.HashMap;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ImageManager {
	private static String OS = System.getProperty("os.name").toLowerCase();
	private static final String DEFAULT_PATH =  System.getProperty("user.dir") + (OS.indexOf("mac")>=0?"/ISTCG/data/images/":"/data/images/");
	//System.getProperty("user.dir") + (OS.indexOf("mac")>=0?"/ISTCG/data/cards/":"/data/cards/");
		
	private HashMap< String, Image > m_LoadedImages = new HashMap< String, Image >();
	private Display m_Display;
	
	
	public Image GetImage( String image_name ) throws NullPointerException {
		Image toReturn = m_LoadedImages.get( image_name );
		
		if( toReturn == null ) {
			try {
				toReturn = new Image( m_Display, DEFAULT_PATH + image_name );
			} catch (Exception e) {
				toReturn = new Image( m_Display, DEFAULT_PATH + "error.png" );
				System.err.println( "Error: Failed to load image \"" + image_name + "\"." );
			}
			m_LoadedImages.put( image_name, toReturn );
		}
		
		return toReturn;
	}
	
	public void Initialize( Display display ) {
		m_Display = display;
	}
	
	//=====
	//Singleton methods
	//=====
	private static ImageManager m_Instance;
	private ImageManager() {
		
	}
	public static ImageManager get() {
		if( m_Instance == null ) { m_Instance = new ImageManager(); }
		return m_Instance;
	}
}
