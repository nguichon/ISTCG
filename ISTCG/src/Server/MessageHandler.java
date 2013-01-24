package Server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageHandler extends Thread {
	private MessageHandler() { m_ToAdd = new ConcurrentLinkedQueue<ClientAccount>(); m_MessageToAll = new ConcurrentLinkedQueue<String>();
		m_Whispers = new ConcurrentLinkedQueue<String>();
	}
	private static MessageHandler m_Instance;
	public static MessageHandler get() {
		if( m_Instance == null ) { m_Instance = new MessageHandler(); }
		return m_Instance;
	}
	
	
	
	@Override
	public void run() {
		while( !m_Quit ) { //This thread runs until the server quits
			while( m_First != null && m_First.RemoveMe() ) { //If m_First is set to be removed, then remove it. Set m_Next to be the next one.
				m_First = m_First.GetNext(); 
				if( m_First != null ) {
					m_First.SetPrevious( null );
				}
			}
			
			while( m_ToAdd.peek() != null ) {
				if( m_First != null ) {
					m_First.InsertAfter( m_ToAdd.remove() );
				} else {
					m_First = m_ToAdd.remove();
				}
			}
			ClientAccount next = m_First;
			String toSendToAll = m_MessageToAll.poll();
			String toWhisper = m_Whispers.poll();
			while( next != null ) {
				//Read/send messages from/to this client
				if(toSendToAll != null) {
					next.AddMessage(toSendToAll);
				}
				next.Update();
				next = next.GetNext();
				if(toWhisper!=null){
					String[] s = toWhisper.split(";");
					ServerMain.getClientByName(s[2]).AddMessage("SAY;"+s[1]+";"+s[3]);
					toWhisper=null;
				}
			}
		}
	}
	
	public void Quit() {
		m_Quit = true;
	}
	
	public void AddClient( ClientAccount ca ) {
		m_ToAdd.add(ca);
	}
	
	public void SayToAll(String message) {
		m_MessageToAll.add(message);
	}
	
	public void SayTo(String message) {
		m_Whispers.add(message);
	}
	
	boolean m_Quit;
	ClientAccount m_First;
	private Queue<ClientAccount> m_ToAdd;
	private Queue<String> m_MessageToAll;
	
	private Queue<String> m_Whispers;
}
