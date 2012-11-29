package Client;

import java.util.Vector;

import Shared.NetworkObject;

public class FieldObject implements NetworkObject{
	private int m_NetworkObjectID;
	private Vector<Ability> m_Abilities;
	
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
		// TODO Auto-generated method stub
		return false;
	}
}
