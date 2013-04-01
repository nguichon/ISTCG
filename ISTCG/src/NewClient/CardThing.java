package NewClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


public class CardThing extends Canvas {

	public CardThing(Composite parent, int style, final Canvas big_info) {
		super(parent, style);
		this.addMouseTrackListener( new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent arg0) {
				Rectangle location = getBounds();
				big_info.setLocation( location.x - (big_info.getBounds().width - location.width)/ 2, location.y + location.height );
				big_info.setVisible( true );
				//setBackground( Main.display.getSystemColor( SWT.COLOR_BLACK ));
				
			}

			@Override
			public void mouseExit(MouseEvent arg0) {
				big_info.setVisible( false );
				setBackground( null );
			}

			@Override
			public void mouseHover(MouseEvent arg0) {
			}
			
		});
		setSize( Viewer.WIDTH, Viewer.HEIGHT );
	}

}
