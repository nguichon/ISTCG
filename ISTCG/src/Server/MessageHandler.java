package Server;

public class MessageHandler extends Thread {
	private MessageHandler() { }
	private static MessageHandler m_Instance;
	public static MessageHandler get() {
		if( m_Instance == null ) { m_Instance = new MessageHandler(); }
		return m_Instance;
	}
	
	
	@Override
	public void run() {
		while( !m_Quit ) {
			if( m_First != null ) {
				while( m_First.RemoveMe() ) {
					m_First = m_First.GetNext();
					m_First.SetPrevious( null );
				}
				ClientAccount next = m_First;
				while( next != null ) {
					next = next.GetNext();
				}
			}
		}
	}
	
	boolean m_Quit;
	ClientAccount m_First;
}
