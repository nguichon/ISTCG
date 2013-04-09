package NewClient.games;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabItem;

import com.oracle.jrockit.jfr.InvalidValueException;

import NewClient.ClientCardTemplate;
import NewClient.ClientMain;
import Shared.ClientMessages;
import Shared.ClientResponses;
import Shared.GameZones;
import Shared.GameResources;

public class GameV2 extends Composite {
	private static final int PLAYER_BAR_HEIGHT = 24;
	private static final int BOTTOM_BAR_AREA = 150;
	private static final int STACK_WIDTH = 24;
	
	private int m_GameID;
	private TabItem m_Host;
	private ClientMain m_MainClass;
	
	private HashMap< Integer, ClientGameCardInstance > m_LoadedCards = new HashMap< Integer, ClientGameCardInstance>();
	private VisibleHand m_PlayerHand;
	private TheVoid m_Unknown = new TheVoid( this, SWT.None, this );
	private BattleField m_PlayerField;
	private PlayerStatusBar m_PlayerBar;
	private BattleField m_OpponentField;
	private PlayerStatusBar m_OpponentBar;
	
	private int m_OpponentID;
	
	public GameV2(Composite parent, int style, 
			int id, final ClientMain main, TabItem tab ) {
		super(parent, style);

		this.m_GameID = id;
		this.m_Host = tab;
		this.m_MainClass = main;
		
		System.out.println("Game " + id + " created." );
		m_PlayerHand = new VisibleHand( this, SWT.NONE, this );
		m_PlayerField= new BattleField( this, SWT.NONE, this );
		m_OpponentField= new BattleField( this, SWT.NONE, this );
		m_PlayerBar = new PlayerStatusBar( this, SWT.NONE );
		m_OpponentBar = new PlayerStatusBar( this, SWT.NONE );
		
		m_OpponentField.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_RED ) );
		m_PlayerField.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_BLUE ) );
		
		this.addListener( SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Rectangle area = getClientArea();
				m_PlayerHand.setBounds( ClientCardTemplate.CardRenderSize.SMALL.getWidth(),
						area.height - ClientCardTemplate.CardRenderSize.SMALL.getHeight() - 25,
						area.width - 2 * ClientCardTemplate.CardRenderSize.SMALL.getWidth() - 50,
						ClientCardTemplate.CardRenderSize.SMALL.getHeight() + 25);
				m_PlayerField.setBounds( STACK_WIDTH,
						PLAYER_BAR_HEIGHT + (area.height - (BOTTOM_BAR_AREA + 2 * PLAYER_BAR_HEIGHT)) / 2,
						area.width - STACK_WIDTH,
						(area.height - (BOTTOM_BAR_AREA + 2 * PLAYER_BAR_HEIGHT)) / 2);
				m_OpponentField.setBounds( STACK_WIDTH,
						PLAYER_BAR_HEIGHT,
						area.width - STACK_WIDTH,
						(area.height - (BOTTOM_BAR_AREA + 2 * PLAYER_BAR_HEIGHT)) / 2);
				m_PlayerBar.setBounds( 0,
						area.height - ClientCardTemplate.CardRenderSize.SMALL.getHeight() - 25 - PLAYER_BAR_HEIGHT,
						area.width,
						PLAYER_BAR_HEIGHT );
				m_OpponentBar.setBounds( 0,
						0,
						area.width,
						PLAYER_BAR_HEIGHT );
			}
		});
	}
	
	public int getID() {
		return m_GameID;
	}
	public void LoadDeck( int commandUnit, String list ) {
		SendMessageFromGame( ClientResponses.DECKLIST, String.valueOf(commandUnit), list );
	}
	
	public void SendMessageFromGame( ClientResponses messageType, String...params ) {
		String toSend =  messageType.name() + ";" + String.valueOf( m_GameID ) + ";";
		for( String s : params ) {
			toSend += s + ";";
		}
		m_MainClass.sendData( toSend );
	}

	//===
	//NETWORK
	//===
	Queue< String[] > m_UnreadMessages = new PriorityQueue< String[] >( 10, new Comparator<String[]>() {
		@Override
		public int compare(String[] lhs, String[] rhs) {
			int lhsid = Integer.valueOf( lhs[2] );
			int rhsid = Integer.valueOf( rhs[2] );
			
			return lhsid - rhsid;
		}
	});
	int m_MessagesRead = 1;
	
	public synchronized void HandleMessage( String[] message ) {
		if( Integer.valueOf(message[1]) != m_GameID ) {
			// Message was somehow sent to the wrong game! Baddie.
			return;
		}
		if( Integer.valueOf(message[2]) != m_MessagesRead ) {
			//Put message into queue, continue on our way.
			m_UnreadMessages.add( message );
			return;
		}
		
		ExecuteMessage( message );
		m_MessagesRead++;
	
		while( m_UnreadMessages.size() > 0 && Integer.valueOf(m_UnreadMessages.peek()[2]) == m_MessagesRead ) {
			String[] toExecute = m_UnreadMessages.poll();
			ExecuteMessage( toExecute );
			m_MessagesRead++;
		}
	}
	private ClientGameCardInstance CreateCard( int id ) {
		ClientGameCardInstance newbie = new ClientGameCardInstance( this, SWT.NONE, id, this );
		m_LoadedCards.put( id, newbie );
		return newbie;
	}
	private void ExecuteMessage( String[] message ) {
		try {
			switch( ClientMessages.valueOf( message[0].toUpperCase() ) ){
			case MOVE:
				ClientGameCardInstance toMove = m_LoadedCards.get( Integer.valueOf( message[3] ) );
				if( toMove == null ) {
					toMove = CreateCard( Integer.valueOf( message[3] ) );
				}
				toMove.MoveThisCard( GameZones.valueOf( message[4].toUpperCase() ) );
				break;
			case CARD_INFO:
				ClientGameCardInstance toUpdate = m_LoadedCards.get( Integer.valueOf( message[3] ) );
				toUpdate.UpdateCardTemplate( Integer.valueOf( message[4] ) );
				toUpdate.SetController( Integer.valueOf( message[6] ) );
				break;
			case PLAYER_JOINED:
				PlayerStatusBar psb = m_PlayerBar;
				
				if( Integer.valueOf( message[4] ) != Integer.valueOf( m_MainClass.getPID() ) ) {
					psb = m_OpponentBar;
				}
				
				psb.UpdateName( message[3] );
				break;
			case UPDATE_ZONE:
				psb = m_PlayerBar;
				
				if( Integer.valueOf( message[3] ) != Integer.valueOf( m_MainClass.getPID() ) ) {
					psb = m_OpponentBar;
				}
				
				psb.UpdateZoneCount( GameZones.valueOf( message[4] ), Integer.valueOf( message[5] ) );
				break;
			case UPDATE_PLAYER:
				psb = m_PlayerBar;
				
				if( Integer.valueOf( message[3] ) != Integer.valueOf( m_MainClass.getPID() ) ) {
					psb = m_OpponentBar;
				}
				
				psb.UpdateResourceCount( GameResources.valueOf( message[4] ), Integer.valueOf( message[5] ) );
				break;
			default:
				System.out.println( message[0] + " type message unhandled." );
				break;
			}
		} catch( IllegalArgumentException e ) {
			e.printStackTrace();
		}
	}

	public void RemoveCardFrom(ClientGameCardInstance clientGameCardInstance,
			GameZones newZone, int m_Controller) {
		// DO NOTHING;
	}

	public void AddCardTo(ClientGameCardInstance clientGameCardInstance,
			GameZones newZone, int controller) {
		GameCardStorage newParent = m_Unknown;
		switch( newZone ) {
		case HAND:
			if( controller == Integer.valueOf(m_MainClass.getPID()) ) {
				newParent = m_PlayerHand;
			}
			break;
		case FIELD:
			if( controller == Integer.valueOf(m_MainClass.getPID()) ) {
				newParent = m_PlayerField;
			} else if ( controller != -1 ) {
				newParent = m_OpponentField;
			}
		default:
			break;
		}
		clientGameCardInstance.setParent(newParent);
		newParent.OptimizeLayout();
	}

	public void Focus( ClientGameCardInstance object) {
		//TODO 
	}
	public void Unfocus( ClientGameCardInstance object) {
		//TODO
	}
}
