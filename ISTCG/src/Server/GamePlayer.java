package Server;

public class GamePlayer {
	private ClientAccount m_Account;
	
	public GamePlayer( ClientAccount acc ) {
		m_Account = acc;
	}
	public void SwitchAccountInstance( ClientAccount acc ) {
		m_Account = acc;
	}
}
