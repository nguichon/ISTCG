package server.admin;

public enum AdminCommands {
	SELECT			(new AdminCommandSelect()), 
	CREATE_ACCOUNT	(new AdminCommandCreateAccount()), 
	GET_USERS		(new AdminCommandNothing()), 
	SHRINK			(new AdminCommandNothing()), 
	QUIT			(new AdminCommandQuit()), 
	NOTHING			(new AdminCommandNothing());
	
	private AdminCommand m_Command;
	
	private AdminCommands( AdminCommand ac ){
		m_Command = ac;
	}
	
	public String Activate( String...params ){
		return m_Command.Activate( params );
	}
}
