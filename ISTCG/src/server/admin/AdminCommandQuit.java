package server.admin;

import server.ServerMain;

public class AdminCommandQuit extends AdminCommand {
	private final int NUMBER_OF_PARAMETERS = 0;
	
	@Override
	public String Activate(String[] params) {
		//Checking for valid # of parameters
		if( params.length == NUMBER_OF_PARAMETERS ) {
			return "Invalid number of parameters.";
		}
		
		ServerMain.Quit();
		return "Server shut down command sent.";
	}
}
	