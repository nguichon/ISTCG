package NewClient.games;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import NewClient.ClientCardTemplate;
import NewClient.ClientCardTemplate.CardRenderSize;
import NewClient.ClientMain;
import NewClient.ClientSoundManager;
import Shared.ClientMessages;
import Shared.ClientResponses;
import Shared.GameStates;
import Shared.GameZones;
import Shared.GameResources;
import Shared.PlayerStates;

public class GameV2 extends Composite {
	
	private static final int PLAYER_BAR_HEIGHT = 24;
	private static final int BOTTOM_BAR_AREA_HEIGHT = CardRenderSize.SMALL.getHeight() + 15;
	private static final int STACK_WIDTH = CardRenderSize.SMALL.getWidth() + 8;
	private static final int HELPER_HEIGHT = 24;
	
	private static final Point SCRAPYARD_SIZE = new Point( CardRenderSize.SMALL.getWidth() + 10, CardRenderSize.SMALL.getHeight() + 10 );
	private static final Point BUTTON_SIZE = new Point( BOTTOM_BAR_AREA_HEIGHT, BOTTOM_BAR_AREA_HEIGHT );
	private int m_GameID;
	private TabItem m_Host;
	private ClientMain m_MainClass;
	
	private GameStates m_State = GameStates.STARTING;
	private PlayerStates m_PlayerState = PlayerStates.JOINED;
	
	private HashMap< Integer, ClientGameCardInstance > m_LoadedCards = new HashMap< Integer, ClientGameCardInstance>();
	private HashMap< Integer, StackObject > m_StackObjects = new HashMap< Integer, StackObject>();
	private VisibleHand m_PlayerHand;
	private TheVoid m_Unknown = new TheVoid( this, SWT.NONE, this );
	private Scrapyard m_PlayerScrapyard = new Scrapyard( this, SWT.NONE, this );
	private BattleField m_PlayerField;
	private PlayerStatusBar m_PlayerBar;
	private BattleField m_OpponentField;
	private PlayerStatusBar m_OpponentBar;
	private PreviewBox m_Preview;
	private Button m_MainButton;
	private Stack m_Stack = new Stack( this, SWT.BORDER, this );
	private Label m_HelperText = new Label( this, SWT.BORDER | SWT.CENTER );
	
	private int m_OpponentID;
	
	public GameV2(Composite parent, int style, 
			int id, final ClientMain main, TabItem tab ) {
		super(parent, style);

		this.m_GameID = id;
		this.m_Host = tab;
		this.m_MainClass = main;
		
		System.out.println("Game " + id + " created." );
		m_PlayerHand = new VisibleHand( this, SWT.BORDER, this );
		m_PlayerField= new BattleField( this, SWT.BORDER, this );
		m_OpponentField= new BattleField( this, SWT.BORDER, this );
		m_PlayerBar = new PlayerStatusBar( this, SWT.NONE );
		m_Preview = new PreviewBox( this, SWT.NONE, this );
		m_OpponentBar = new PlayerStatusBar( this, SWT.NONE );
		m_MainButton = new Button( this, SWT.NONE );
		m_MainButton.setText("END TURN");
		m_PlayerScrapyard.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_BLACK ) );
		m_HelperText.setForeground( Display.getDefault().getSystemColor( SWT.COLOR_RED ) );
		m_HelperText.moveBelow( m_PlayerField );
		
		//m_OpponentField.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_RED ) );
		//m_PlayerField.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_BLUE ) );
		m_MainButton.addSelectionListener( new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				m_MainButton.setEnabled( false );
				MainButtonClicked();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//Does nothing
			}
		});
		
		this.addListener( SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Rectangle area = getClientArea();
				m_PlayerHand.setBounds( 0,
						area.height - BOTTOM_BAR_AREA_HEIGHT,
						area.width - BUTTON_SIZE.x - SCRAPYARD_SIZE.x,
						BOTTOM_BAR_AREA_HEIGHT);
				m_PlayerField.setBounds( STACK_WIDTH,
						PLAYER_BAR_HEIGHT + HELPER_HEIGHT + (area.height - (BOTTOM_BAR_AREA_HEIGHT + 2 * PLAYER_BAR_HEIGHT + HELPER_HEIGHT)) / 2,
						area.width - STACK_WIDTH,
						(area.height - (BOTTOM_BAR_AREA_HEIGHT + 2 * PLAYER_BAR_HEIGHT + HELPER_HEIGHT)) / 2);
				m_OpponentField.setBounds( STACK_WIDTH,
						PLAYER_BAR_HEIGHT,
						area.width - STACK_WIDTH,
						(area.height - (BOTTOM_BAR_AREA_HEIGHT + 2 * PLAYER_BAR_HEIGHT + HELPER_HEIGHT)) / 2);
				m_PlayerBar.setBounds( 0,
						area.height - BOTTOM_BAR_AREA_HEIGHT - PLAYER_BAR_HEIGHT,
						area.width,
						PLAYER_BAR_HEIGHT );
				m_OpponentBar.setBounds( 0,
						0,
						area.width,
						PLAYER_BAR_HEIGHT );
				m_PlayerScrapyard.setBounds( area.width - SCRAPYARD_SIZE.x,
						area.height - SCRAPYARD_SIZE.y,
						SCRAPYARD_SIZE.x,
						SCRAPYARD_SIZE.y );
				m_MainButton.setBounds( area.width - SCRAPYARD_SIZE.x - BUTTON_SIZE.x,
						area.height - BUTTON_SIZE.y,
						BUTTON_SIZE.x,
						BUTTON_SIZE.y);
				m_Stack.setBounds( 0,
						PLAYER_BAR_HEIGHT,
						STACK_WIDTH,
						area.height - PLAYER_BAR_HEIGHT * 2 - BOTTOM_BAR_AREA_HEIGHT );
				m_HelperText.setBounds(  STACK_WIDTH,
						PLAYER_BAR_HEIGHT + (area.height - (BOTTOM_BAR_AREA_HEIGHT + 2 * PLAYER_BAR_HEIGHT + HELPER_HEIGHT)) / 2,
						area.width - STACK_WIDTH,
						HELPER_HEIGHT );
			}
		});
	}
	
	protected void MainButtonClicked() {
		if( m_TargetingMode ) { Cancel(); return; }
		switch( m_State ) {
		case ACTIVE:
			SendMessageFromGame( ClientResponses.END );
			break;
		case WAITING:
		case STACKING:
		case RESOLVING:
			SendMessageFromGame( ClientResponses.PASS );
			break;
		}
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
	private boolean m_TargetingMode;
	
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
			case GAME_STATE:
				SetGameState(GameStates.valueOf( message[3].toUpperCase() ));
				break;
			case PLAYER_STATE:
				if( Integer.valueOf( message[3] ) == Integer.valueOf( m_MainClass.getPID() ) ) {
					SetPlayerState(PlayerStates.valueOf( message[4].toUpperCase() ));
				}
				break;
			case GAME_ERROR:
				m_HelperText.setText( message[3] );
				break;
			case PLAYER_JOINED:
				PlayerStatusBar psb = m_PlayerBar;
				
				if( Integer.valueOf( message[4] ) != Integer.valueOf( m_MainClass.getPID() ) ) {
					psb = m_OpponentBar;
				}
				
				psb.UpdateName( message[3] );
				break;
			case SET_CARD_DAMAGE:
				m_LoadedCards.get( Integer.valueOf( message[3] ) ).SetDamage( Integer.valueOf( message[4] ) );
				break;
			case STACK_OBJECT:
				if( message[4].equals("ATTACK") ) {
					//Type of stack object is an attack.
					StackObject so = new StackObject( m_Stack, SWT.NONE, m_LoadedCards.get( Integer.valueOf(message[5])), "Attacking");
					m_StackObjects.put( Integer.valueOf( message[3] ), so );
					m_Stack.OptimizeLayout();
				} else {
					System.out.println(" UNKNOWN STACK OBJECT TYPE " + message[4] );
				}
				break;
			case REMOVE_STACK_OBJECT:
				m_StackObjects.remove( Integer.valueOf( message[3] ) ).dispose();
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

	private void SetGameState(GameStates valueOf) {
		m_State = valueOf;
		System.out.println( "GAME SET TO " + valueOf.name() );
		switch( valueOf ) {
		case ACTIVE:
		case BETWEEN_TURNS:
		case STARTING:
		case CREATED:
		case ENDED:
			m_MainButton.setText( "End Turn" );
			break;
		case WAITING:
		case STACKING:
		case RESOLVING:
			m_MainButton.setText( "Pass" );
			break;
		}
	}
	
	private void SetPlayerState( PlayerStates ps ) {
		m_PlayerState = ps;
		System.out.println( "PLAYER SET TO " + ps.name() );
		switch( ps ) {
		case READING:
			m_HelperText.setText( "Pass priority to let card resolve." );
			m_MainButton.setEnabled( true );
			m_PlayerField.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_WHITE ));
			break;
		case ACTIVE:
			if( m_State == GameStates.ACTIVE ) {
				m_HelperText.setText( "Play a card, attack with a ship, or end your turn." );
				m_PlayerField.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_GREEN ));
				ClientSoundManager.get().play("YourTurn.mp3");
				
			} else {
				m_HelperText.setText( "Play a card or pass priority." );
				m_PlayerField.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_WHITE ));
			}
			m_MainButton.setEnabled( true );
			break;
		default:
			m_HelperText.setText( "Wait." );
			m_MainButton.setEnabled( false );
			m_PlayerField.setBackground(Display.getDefault().getSystemColor( SWT.COLOR_WHITE ));
			break;
		}
	}

	public GameCardStorage RemoveCardFrom(ClientGameCardInstance clientGameCardInstance,
			GameZones newZone, int controller) {
		GameCardStorage oldParent = m_Unknown;
		switch( newZone ) {
		case HAND:
			if( controller == Integer.valueOf(m_MainClass.getPID()) ) {
				oldParent = m_PlayerHand;
			}
			break;
		case FIELD:
			if( controller == Integer.valueOf(m_MainClass.getPID()) ) {
				oldParent = m_PlayerField;
			} else if ( controller != -1 ) {
				oldParent = m_OpponentField;
			}
			break;
		case STACK:
			oldParent = m_Stack;
			break;
		case GRAVEYARD:
			if( controller == Integer.valueOf(m_MainClass.getPID()) ) {
				oldParent = m_PlayerScrapyard;
			}
			break;
		default:
			break;
		}

		return oldParent;
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
			ClientSoundManager.get().play("asplode.mp3");
			break;
		case STACK:
			newParent = m_Stack;
			break;
		case GRAVEYARD:
			if( controller == Integer.valueOf(m_MainClass.getPID()) ) {
				newParent = m_PlayerScrapyard;
			}
			break;
		default:
			break;
		}
		newParent.AddCard( clientGameCardInstance );
	}

	public void Focus( ClientGameCardInstance object) {
		m_Preview.moveAbove( null );
		m_Preview.SetFocus( object );
	}
	public void Unfocus( ClientGameCardInstance object) {
		m_Preview.RemoveFocus();
	}

	public synchronized void clicked(ClientGameCardInstance source) {
		if( m_PlayerState == PlayerStates.ACTIVE ) {
			switch( m_State ) {
			case STACKING:
				if( m_TargetingMode ) { AddTarget( source ); break; }
				if( source.getController() == Integer.valueOf(m_MainClass.getPID())
						&& source.getZone()== GameZones.HAND ) { 
					PlayCard( source ); 
					break;
				}
			case ACTIVE:
				if( m_TargetingMode ) { AddTarget( source ); break; }
				if( source.getController() == Integer.valueOf(m_MainClass.getPID())
						&& source.getZone()== GameZones.HAND ) { 
					PlayCard( source ); 
					break;
				}
				if( source.getController() == Integer.valueOf(m_MainClass.getPID())
						&& source.getZone()== GameZones.FIELD ) { 
					AttackWith( source ); 
					break;
				}
			}
		}
	}

	private ClientResponses m_TargetingType;
	private int m_TargetCount;
	private ClientGameCardInstance m_SourceOfTargeting;
	private ArrayList< ClientGameCardInstance > m_Targets = new ArrayList< ClientGameCardInstance >();
	
	private void PlayCard(ClientGameCardInstance toPlay) {
		SendMessageFromGame( ClientResponses.PLAY, 
				String.valueOf(toPlay.GetID()));
	}
	private void AttackWith( ClientGameCardInstance toAttackWith ) {
		m_HelperText.setText( "Select a target to attack." );
		m_TargetingType = ClientResponses.ATTACK;
		m_SourceOfTargeting = toAttackWith;
		m_TargetCount = 1;
		m_Targets.clear();
		m_TargetingMode = true;
		m_MainButton.setText( "Cancel" );
	}
	private void AddTarget( ClientGameCardInstance toTarget ) {
		if( m_TargetingMode ) {
			m_Targets.add( toTarget );
			if( m_Targets.size() >= m_TargetCount ) {
				if( m_TargetingType == ClientResponses.ATTACK ) {
					SendMessageFromGame( ClientResponses.ATTACK, 
							String.valueOf(m_SourceOfTargeting.GetID()), 
							String.valueOf(toTarget.GetID()));
				}
				m_TargetingMode = false;
				SetGameState( m_State );
			}
		}
	}
	private void Cancel() {
		m_HelperText.setText( "Canceled." );
		m_TargetingMode = false;
		SetGameState( m_State );
	}
}
