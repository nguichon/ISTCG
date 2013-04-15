package NewClient.games;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class IconLabel extends Composite {
	Canvas m_Icon; Image m_IconImage;
	Label m_Label;
	
	
	public IconLabel(Composite parent, int style, Image labelImage, String labelText) {
		super(parent, style);
		
		m_Label = new Label( this, SWT.NONE );
		m_Icon = new Canvas( this, SWT.NONE );
		
		m_IconImage = labelImage;
		m_Label.setText( labelText );
		m_Label.setAlignment( SWT.LEFT );
		
		this.addListener( SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Rectangle area = getClientArea();
				m_Icon.setBounds( 0, 0, area.height, area.height );
				m_Label.setBounds( area.height, (area.height - 12) / 2, area.width - area.height, (area.height - 12) / 2 + 12 );
			}
			
		});
		
		m_Icon.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				e.gc.drawImage( m_IconImage, 0, 0, m_IconImage.getBounds().width, m_IconImage.getBounds().height, 
						0, 0, e.width, e.height );
			}
			
		});
	}
	
	public void setText( String s ) {
		m_Label.setText( s );
	}

}
