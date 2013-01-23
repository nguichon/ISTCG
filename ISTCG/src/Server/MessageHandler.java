package Server;

public class MessageHandler extends Thread {
	private enum ClientMessages {
		SAY, TELL;
	}
	
	private MessageHandler() { }
	private static MessageHandler m_Instance;
	public static MessageHandler get() {
		if( m_Instance == null ) { m_Instance = new MessageHandler(); }
		return m_Instance;
	}
	
	
	@Override
	public void run() {
		while( !m_Quit ) { //This thread runs until the server quits
			if( m_First != null ) { //If there are ANY clients, m_First will be set.
				while( m_First != null && m_First.RemoveMe() ) { //If m_First is set to be removed, then remove it. Set m_Next to be the next one.
					m_First = m_First.GetNext(); 
					if( m_First != null ) {
						m_First.SetPrevious( null );
					}
				}
				ClientAccount next = m_First;
				while( next != null ) {
					//Read/send messages from/to this client
					next.Update();
					next = next.GetNext();
				}
			}
		}
	}
	
	public void Quit() {
		m_Quit = true;
	}
	
	boolean m_Quit;
	ClientAccount m_First;
}
