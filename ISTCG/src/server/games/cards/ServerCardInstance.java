package server.games.cards;

import java.util.ArrayList;
import java.util.HashMap;

import server.games.GameInstance;
import server.games.GamePlayer;
import server.games.events.GameEvent;


import Shared.ClientMessages;
import Shared.GameZones;
import Shared.StatBlock;

public class ServerCardInstance {
	private static final int CARD_TEMPLATE_INVISIBLE = -1;
	
	//Instance information
	private ServerCardTemplate m_Template;
	private int m_UID;
	
	//Game information
	private GameInstance m_Host;
	private GameZones m_Location;
	
	//Player information
	private GamePlayer m_Owner, m_Controller;
	
	public ServerCardInstance( GameInstance host, GamePlayer owner, int template_id ) {
		m_Host = host; m_Owner = owner; m_Controller = owner;
		
		m_UID = m_Host.CreateNewCardID();
		m_Location = GameZones.UNKNOWN;
		
		m_Template = ServerCardTemplateManager.get().GetCardTemplate( template_id );
		
		host.AddToDirectory( this );
	}
	
	public void SetLocation( GameZones location ) { m_Location = location; }
	public void Reset() { m_Controller = m_Owner; }
	
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
					String.valueOf( m_Template.getCardTemplateID()) );
		} else {
			requester.SendMessageFromGame( 
					ClientMessages.CARD_INFO, 
					String.valueOf( m_UID ), 
					String.valueOf( CARD_TEMPLATE_INVISIBLE ) );
		}
	}
	
	public int GetCardUID() { return m_UID; }
	public ServerCardTemplate GetCardTemplate() { return m_Template; }

	public void resolve(GameEvent e) {
		e.m_SourcePlayer = m_Controller;
		e.m_SourceCard = this;
		m_Template.onPlay( e );
	}
}
