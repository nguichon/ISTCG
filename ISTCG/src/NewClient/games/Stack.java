package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import NewClient.ClientCardTemplate;

public class Stack extends GameCardStorage {
	private static final int CARD_SHIFT = 45;
	
	public Stack(Composite parent, int style, GameV2 host) {
		super( parent, style, host );
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
			items[i].setLocation( 5, (items.length - i - 1) * CARD_SHIFT );
		}
		
		redraw();
	}
}
