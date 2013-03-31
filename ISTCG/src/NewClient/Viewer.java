package NewClient;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;


public class Viewer extends Composite {
	Group cards_in_hand;
	public static final int WIDTH = 60;
	public static final int HEIGHT = 80;
	private static final int DESIRED_SPACING = 5;
	

	public Viewer( Composite parent, int style ) {
		super( parent, style );
		cards_in_hand = new Group(this, SWT.NONE);
		cards_in_hand.setText("HAND");
		Main.big_info = new Canvas( cards_in_hand, SWT.BORDER | SWT.ON_TOP | SWT.NO_FOCUS );
		Main.big_info.setSize( 200, 300 );
		Main.big_info.setVisible(false );
		
		this.addListener( SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event e) {
				cards_in_hand.setBounds( getClientArea() );
				OptimizeLayout();
			}
			
		});
	}
	
	public void AddCard() {
		Canvas newbie = new CardThing( cards_in_hand, SWT.BORDER ); 
		OptimizeLayout();
	}
	
	public void OptimizeLayout() {
		Rectangle space = cards_in_hand.getClientArea();
		int trim = 3;
		int trim_top = 13;
		Control[] items = cards_in_hand.getChildren();
		if( ((double) (space.width - trim * 2) / (WIDTH + DESIRED_SPACING )) > items.length ) {
			for( int i = 0; i < items.length; i++ ) {
				items[i].setLocation( ((i-1) * (WIDTH+DESIRED_SPACING)) + trim, trim_top);
			}
		} else {
			int available_spacing = (space.width - trim * 2 - (WIDTH * items.length))/ items.length;
			for( int i = 0; i < items.length; i++ ) {
				items[i].setLocation( (i-1) * (WIDTH+available_spacing) + trim, trim_top);
			}
		}
		
		cards_in_hand.redraw();
	}
}
