package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import NewClient.ClientCardTemplate.CardRenderSize;

public class PreviewBox extends Composite {
	private Canvas m_Canvas;
	private GameV2 m_HostGame;
	
	private ClientGameCardInstance toRender;
	
	public PreviewBox(Composite parent, int style, GameV2 host ) {
		super(parent, style | SWT.ON_TOP );
		
		this.setSize( CardRenderSize.LARGE.getWidth(), CardRenderSize.LARGE.getHeight() );
		this.setVisible( false );
		
		m_Canvas = new Canvas( this, SWT.NONE );
		m_HostGame = host;
		
		this.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				if( toRender != null ) {
					toRender.RenderToSurface( e.gc, CardRenderSize.LARGE );
				}
			}
			
		});
	}
	
	public void SetFocus( ClientGameCardInstance cgci ) {
		toRender = cgci;
		
		//TODO Fix this method to move card to left/right/bottom/top edge depending on card location.
		//(Main.big_info.getBounds().width - location.width)/ 2
		
		Point p = cgci.toDisplay( 0, 0 );
		Point p2 = this.toDisplay( 0, 0 );
		Point location = this.getLocation();
		this.setLocation( location.x + (p.x - p2.x) - ( (this.getSize().x - cgci.getSize().x) / 2), location.y + (p.y - p2.y) - this.getSize().y );
		
		this.redraw();
		this.setVisible( true );
	}
	public void RemoveFocus() {
		toRender = null;
		this.setVisible( false );
	}

}
