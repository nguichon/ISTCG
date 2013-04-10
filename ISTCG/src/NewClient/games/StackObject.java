package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import NewClient.ClientCardTemplate.CardRenderSize;

public class StackObject extends Composite {
	ClientGameCardInstance m_Source;
	String m_Text;
	public StackObject( Composite parent, int style, ClientGameCardInstance source, String text ) {
		super(parent, style);

		m_Source = source;
		m_Text = text;
		
		this.addPaintListener( new PaintListener() {

			@Override
			public void paintControl(PaintEvent e) {
				RenderToSurface( e.gc, CardRenderSize.SMALL );
			}
			
		});
	}
	private static Font m_SOFont = new Font( Display.getDefault(), "MONOTYPE", 30, SWT.TRANSPARENT );
	public void RenderToSurface(GC gc, CardRenderSize size ) {
		m_Source.RenderToSurface( gc, size );
		gc.setFont( m_SOFont );
		gc.drawText( m_Text, 0, (size.getHeight() - gc.getFontMetrics().getHeight()) / 2 );
	}

}
