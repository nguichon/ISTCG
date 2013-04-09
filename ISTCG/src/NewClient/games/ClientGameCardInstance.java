package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import NewClient.ClientCardTemplate;
import NewClient.ClientCardTemplate.CardRenderSize;
import NewClient.ClientCardTemplateManager;
import Shared.ClientResponses;
import Shared.GameZones;

public class ClientGameCardInstance extends Composite {
	private int m_InstanceID;
	private int m_TemplateID = -1;
	private CardRenderSize m_Size = CardRenderSize.SMALL;
	private int m_DamageTaken = 0;
	private GameZones m_Zone = GameZones.UNKNOWN;
	private int m_Controller = -1;
	GameV2 m_HostGame;
	
	public ClientGameCardInstance(Composite parent, int style,
			int instanceID, GameV2 hostGame ) {
		super(parent, style);
		
		this.ChangeSize( CardRenderSize.SMALL );
		m_HostGame = hostGame;
		m_InstanceID = instanceID;
		m_HostGame.SendMessageFromGame( ClientResponses.GETCARDINFO, String.valueOf(m_InstanceID) );
		
		this.addPaintListener( new PaintListener() {
			@Override
			public void paintControl(PaintEvent arg0) {
				if( m_TemplateID == -1 ) {
					ClientCardTemplate.RenderBlack( arg0.gc, m_Size, null );
				} else  {
					ClientCardTemplateManager.get().
						GetClientCardTemplate( m_TemplateID ).
						Render( arg0.gc, m_Size, null, m_DamageTaken );
				}
			}
		});
		
		this.addMouseListener( new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				m_HostGame.clicked( (ClientGameCardInstance)e.getSource() );
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		this.addMouseTrackListener( new MouseTrackListener() {

			@Override
			public void mouseEnter(MouseEvent e) {
				m_HostGame.Focus( (ClientGameCardInstance)e.getSource() );
			}

			@Override
			public void mouseExit(MouseEvent e) {
				m_HostGame.Unfocus( (ClientGameCardInstance)e.getSource() );
			}

			@Override
			public void mouseHover(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void SetController( int newController ) {
		m_Controller = newController;
		MoveThisCard( m_Zone );
	}
	public void MoveThisCard( GameZones newZone ) {
		GameCardStorage gcz = m_HostGame.RemoveCardFrom( this, m_Zone, m_Controller  );
		
		m_Zone = newZone;
		m_HostGame.AddCardTo( this, newZone, m_Controller );
		
		gcz.RemoveCard( this );
	}
	public int GetTemplateID(  ) {
		return m_TemplateID;
	}
	public void RenderToSurface( GC gc, CardRenderSize large ) {
		if( m_TemplateID == -1 ) {
			ClientCardTemplate.RenderBlack( gc, large, null );
		} else  {
			ClientCardTemplateManager.get().
				GetClientCardTemplate( m_TemplateID ).
				Render( gc, large, null, m_DamageTaken );
		}
	}
	
	public void UpdateCardTemplate( int newTemplateID ) {
		m_TemplateID = newTemplateID;
		this.redraw();
	}
	
	public void ChangeSize( CardRenderSize newSize ) {
		m_Size = newSize;
		this.setSize( newSize.getWidth(), newSize.getHeight() );
	}
	
	public int GetID() {
		return m_InstanceID;
	}

	public GameZones getZone() {
		return m_Zone;
	}

	public int getController() {
		return m_Controller;
	}
}
