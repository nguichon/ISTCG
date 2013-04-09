package NewClient.games;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import OldClient.ImageManager;
import Shared.GameResources;
import Shared.GameZones;

public class PlayerStatusBar extends Composite {
	private static final String HAND_SIZE_LABEL = "Hand Size: ";
	private static final String DECK_SIZE_LABEL = "Deck Size: ";
	private static final String SCRAP_YARD_SIZE_LABEL = "Scrapyard Size: ";
	private static final String METAL_LABEL = "Metal: ";
	private static final String TECH_LABEL = "Tech: ";
	private static final String ENERGY_LABEL = "Energy: ";
	
	IconLabel m_PlayerHandSize = new IconLabel( this, SWT.NONE, ImageManager.get().GetImage( "resource_hand_icon.png" ), HAND_SIZE_LABEL + 0);
	IconLabel m_PlayerDeckSize = new IconLabel( this, SWT.NONE, ImageManager.get().GetImage( "resource_deck_icon.png" ), DECK_SIZE_LABEL + 0);
	IconLabel m_PlayerSYSize = new IconLabel( this, SWT.NONE, ImageManager.get().GetImage( "resource_deck_icon.png" ), SCRAP_YARD_SIZE_LABEL + 0);
	
	IconLabel m_PlayerMetal = new IconLabel( this, SWT.NONE, ImageManager.get().GetImage( "resource_metal_icon.png" ), METAL_LABEL + 0);
	IconLabel m_PlayerEnergy = new IconLabel( this, SWT.NONE, ImageManager.get().GetImage( "resource_energy_icon.png" ), ENERGY_LABEL + 0);
	IconLabel m_PlayerTech = new IconLabel( this, SWT.NONE, ImageManager.get().GetImage( "resource_tech_icon.png" ), TECH_LABEL + 0);
	
	Label m_PlayerName = new Label( this, SWT.NONE );
	
	public PlayerStatusBar(Composite parent, int style) {
		super(parent, style);
		
		m_PlayerName.setFont( new Font( Display.getDefault(), "MONOSPACE", 18, SWT.BOLD ) );
		
		this.addListener( SWT.Resize, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Rectangle area = getClientArea();
				m_PlayerName.setBounds( 0, 0, area.width/9, area.height );
		
				m_PlayerHandSize.setBounds( (area.width / 9) * 2, 0, area.width / 9, area.height );
				m_PlayerDeckSize.setBounds( (area.width / 9) * 3, 0, area.width / 9, area.height );
				m_PlayerSYSize.setBounds( (area.width / 9) * 4, 0, area.width / 9, area.height );
				
				m_PlayerMetal.setBounds( (area.width / 9) * 6, 0, area.width / 9, area.height );
				m_PlayerEnergy.setBounds( (area.width / 9) * 7, 0, area.width / 9, area.height );
				m_PlayerTech.setBounds( (area.width / 9) * 8, 0, area.width / 9, area.height );
			}
			
		});
	}

	public void UpdateZoneCount(GameZones valueOf, Integer valueOf2) {
		switch( valueOf ) {
			case HAND:
				m_PlayerHandSize.setText( HAND_SIZE_LABEL + valueOf2 );
				break;
			case DECK:
				m_PlayerDeckSize.setText( DECK_SIZE_LABEL + valueOf2 );
				break;
			case GRAVEYARD:
				m_PlayerSYSize.setText( SCRAP_YARD_SIZE_LABEL + valueOf2 );
				break;
			default:
				break;
		}
	}

	public void UpdateResourceCount(GameResources valueOf, Integer valueOf2) {

		switch( valueOf ) {
			case METAL:
				m_PlayerHandSize.setText( HAND_SIZE_LABEL + valueOf2 );
				break;
			case ENERGY:
				m_PlayerDeckSize.setText( DECK_SIZE_LABEL + valueOf2 );
				break;
			case TECH:
				m_PlayerSYSize.setText( SCRAP_YARD_SIZE_LABEL + valueOf2 );
				break;
			default:
				break;
		}
	}

	public void UpdateName(String string) {
		m_PlayerName.setText( string );
	}

}
