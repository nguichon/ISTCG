package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import NewClient.ClientCardTemplate;

public class BattleField extends GameCardStorage {
	private static final int WIDTH = ClientCardTemplate.CardRenderSize.SMALL.getWidth();
	private static final int HEIGHT = ClientCardTemplate.CardRenderSize.SMALL.getHeight();
	
	private static final int DESIRED_SPACING = 5;
	
	public BattleField(Composite parent, int style, GameV2 host) {
		super( parent, style, host );
	}

	@Override
	protected void OptimizeLayout() {
		Rectangle space = getClientArea();
		int trim = 3;
		int trim_top = 13;
		Control[] items = getChildren();
		for( int i = 0; i < items.length; i++ ) {
			items[i].setLocation( i * 100 + 100, 100 );
		}
		
		redraw();
	}
}
