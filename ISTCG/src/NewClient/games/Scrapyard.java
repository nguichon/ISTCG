package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import NewClient.ClientCardTemplate;

public class Scrapyard extends GameCardStorage {
	private static final int WIDTH = ClientCardTemplate.CardRenderSize.SMALL.getWidth();
	private static final int HEIGHT = ClientCardTemplate.CardRenderSize.SMALL.getHeight();
	
	private static final int DESIRED_SPACING = 5;
	
	public Scrapyard(Composite parent, int style, GameV2 host) {
		super( parent, style, host );
	}

	@Override
	public void AddCard( ClientGameCardInstance cgci ) {
		cgci.setParent( this );
		cgci.moveAbove( null );
		cgci.setLocation( 5, 5 );
		OptimizeLayout();
	}
	
	@Override
	protected void OptimizeLayout() {
		redraw();
	}
}
