package Client;

import Shared.NetworkObject;

public class Card implements NetworkObject {
	private int m_NetworkObjectID;
	
	private String m_CardName;
	private String m_CardText;
	//private Image m_CardImage;
	
	@Override
	public int compareTo(NetworkObject arg0) {
		if ( m_NetworkObjectID < arg0.GetNetworkID() ) {
			return -1;
		}
		if ( m_NetworkObjectID > arg0.GetNetworkID() ) {
			return 1;
		}
		return 0;
	}
	@Override
	public void SetNetworkID(int newID) {
		m_NetworkObjectID = newID;
	}
	@Override
	public int GetNetworkID() {
		return m_NetworkObjectID;
	}
	@Override
	public boolean ParseMessage(String s) {
		return false;
	}

}
