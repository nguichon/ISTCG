package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import NewClient.ClientCardTemplate;

public abstract class GameCardStorage extends Composite {
	protected GameV2 m_Host;
	
	public GameCardStorage(Composite parent, int style, GameV2 host) {
		super(parent, style);
		
		this.m_Host = host;
		
		this.addListener( SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				OptimizeLayout();
			}
			
		});
	}
	
	public void AddCard( ClientGameCardInstance cgci ) {
		cgci.setParent( this );
		OptimizeLayout();
	}
	
	public void RemoveCard( ClientGameCardInstance cgci ) {
		OptimizeLayout();
	}

	protected abstract void OptimizeLayout();
}
