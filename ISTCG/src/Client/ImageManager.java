package Client;

import java.util.HashMap;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ImageManager {
	private static final String DEFAULT_PATH =  System.getProperty("user.dir") + "/data/images/";
		
	private HashMap< String, Image > m_LoadedImages = new HashMap< String, Image >();
	private Display m_Display;
	
	
	public Image GetImage( String image_name ) throws NullPointerException {
		Image toReturn = m_LoadedImages.get( image_name );
		
		if( toReturn == null ) {
			toReturn = new Image( m_Display, DEFAULT_PATH + image_name );
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
