package server.games.cards;

import server.games.GameInstance;
import server.games.GamePlayer;
import server.games.events.GameEvent;
import server.games.events.ResolutionEvent;
import server.games.stack.StackObject;


import Shared.ClientMessages;
import Shared.GameZones;

public class ServerCardInstance extends StackObject {
	private static final int CARD_TEMPLATE_INVISIBLE = -1;
	
	//Instance information
	private ServerCardTemplate m_Template;
	
	//Game information
	private GameZones m_Location;
	
	//Player information
	private GamePlayer m_Owner, m_Controller;
	
	public ServerCardInstance( GameInstance host, GamePlayer owner, int template_id ) {
		super( host );
		m_Owner = owner; m_Controller = owner;
		
		m_Location = GameZones.UNKNOWN;
		
		m_Template = ServerCardTemplateManager.get().GetCardTemplate( template_id );
		
		host.AddToDirectory( this );
	}
	
	public void SetLocation( GameZones location ) { m_Location = location; }
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
	
	public int GetCardUID() { return m_UID; }
	public ServerCardTemplate GetCardTemplate() { return m_Template; }

	@Override
	public void Resolve(ResolutionEvent e) { }
}
