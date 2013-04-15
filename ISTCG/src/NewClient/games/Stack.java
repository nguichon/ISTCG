package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import NewClient.ClientCardTemplate;
import OldClient.ImageManager;

public class Stack extends GameCardStorage {
	private static final int CARD_SHIFT = 45;
	private static final int HEADER = 15;
	
	public Stack(Composite parent, int style, GameV2 host) {
		super( parent, style, host );
		
		this.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				Image toDraw = ImageManager.get().GetImage("UI_Game_Stack.png");
				e.gc.drawImage( toDraw, 0, 0, toDraw.getBounds().width, toDraw.getBounds().height,
						0, 0, getClientArea().width, 122 );
			}
			
		});
	}

	@Override
	public void AddCard( ClientGameCardInstance cgci ) {
		cgci.setParent( this );
		cgci.moveAbove( null );
		OptimizeLayout();
	}
	
	@Override
	protected void OptimizeLayout() {
		Control[] items = getChildren();
		for( int i = 0; i < items.length; i++ ) {
			items[i].setLocation( 2, (items.length - i - 1) * CARD_SHIFT + HEADER );
		}
		
		redraw();
	}
}
