package server.games.stack;

import server.games.GameInstance;
import server.games.events.ResolutionEvent;

public abstract class StackObject {
	protected GameInstance m_Host;
	protected  int m_UID;

	public StackObject( GameInstance gi ) {
		m_Host = gi;
		m_UID = m_Host.CreateNewUniqueID();
	}
	public abstract void Resolve( ResolutionEvent e );
	public int getStackObjectID( ) { return m_UID; }
}
