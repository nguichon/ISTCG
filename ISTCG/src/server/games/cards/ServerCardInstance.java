package server.games.cards;

import java.util.ArrayList;

import server.games.GameInstance;
import server.games.GamePlayer;
import server.games.cards.abilities.Target;
import server.games.events.GameEvent;
import server.games.events.ResolutionEvent;
import server.games.stack.StackObject;


import NewClient.ClientCardInstance;
import Shared.CardTypes;
import Shared.ClientMessages;
import Shared.GameZones;

public class ServerCardInstance extends StackObject {
	private static final int CARD_TEMPLATE_INVISIBLE = -1;
	
	//Instance information
	private ServerCardTemplate m_Template;
	private ArrayList<Target> m_Targets = new ArrayList<Target>();
	
	//Game information
	private GameZones m_Location;
	
	//Player information
	private GamePlayer m_Owner, m_Controller;

	private int m_TimesMoved = 0;
	
	public ServerCardInstance( GameInstance host, GamePlayer owner, int template_id ) {
		super( host );
		m_Owner = owner; m_Controller = owner;
		
		m_Location = GameZones.UNKNOWN;
		
		m_Template = ServerCardTemplateManager.get().GetCardTemplate( template_id );
		
		host.AddToDirectory( this );
	}
	
	public synchronized void SetLocation( GameZones location ) { 
		if( m_Location != location ) { 
			++m_TimesMoved; 
			m_Location = location;
		}
	}
	public void Reset() { m_Controller = m_Owner; }
	public GamePlayer getController() { return m_Controller; }
	public GamePlayer getOwner() { return m_Owner; };
	
	/**
	 * Sends the id of this CardInstance's CardTemplate.
	 * Fails if this CardInstance in hidden to the requester.
	 * 
	 * @param requester
	 * 		GamePlayer to send the information to.
	 */
	public void SendCardInformation( GamePlayer requester ) {
		if( requester == m_Controller || !m_Location.isHiddenZone() ) {
			requester.SendMessageFromGame( 
					ClientMessages.CARD_INFO, 
					String.valueOf( m_UID ), 
					String.valueOf( m_Template.getCardTemplateID() ), 
					String.valueOf( m_Owner.getClientAccount().getUserID() ), 
					String.valueOf( m_Controller.getClientAccount().getUserID() ) );
		} else {
			requester.SendMessageFromGame( 
					ClientMessages.CARD_INFO, 
					String.valueOf( m_UID ), 
					String.valueOf( CARD_TEMPLATE_INVISIBLE ) );
		}
	}
	
	public int GetCardUID() { return getStackObjectID(); }
	public ServerCardTemplate GetCardTemplate() { return m_Template; }

	@Override
	public void Resolve(ResolutionEvent e) {
		e.resolvingCard = this;
		e.targets = m_Targets;
		if( m_Template.getCardType() == CardTypes.UNIT || m_Template.getCardType() == CardTypes.GEAR ) {
			e.locationAfterResolution = GameZones.FIELD;
		}
		m_Template.Resolve( e );
		MoveCardTo( e.locationAfterResolution );
		m_Targets.clear();
	}
	public void MakeAttack( ServerCardInstance t  ) {
		
	}
	public void CheckStatus() { }
	
	private void MoveCardTo(GameZones locationAfterResolution) {
		switch( locationAfterResolution ) {
		case GRAVEYARD:
			m_Owner.putCardInZone( this, GameZones.GRAVEYARD );
		case FIELD:
			m_Host.PutCardOntoField( this );
		default:
		}
	}

	public GameZones getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public int TimesMoved() {
		return m_TimesMoved ;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	public void clearTargets() {
		m_Targets.clear();
	}
	
	public void addTarget( Target t ) {
		m_Targets.add( t );
	}
	
	public boolean ValidateTargets() {
		//TODO Validate targets
		return true;
	}
}
