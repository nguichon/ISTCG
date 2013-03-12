package server.admin;

import server.network.ClientAccount;

public class AdminCommandCreateAccount extends AdminCommand {
	private final int NUMBER_OF_PARAMETERS = 4;
	@Override
	public String Activate(String[] params) {
		//Checking for valid # of parameters
		if( params.length != NUMBER_OF_PARAMETERS ) {
			return "Invalid number of parameters.";
		}
		
		try {
			ClientAccount.NewAccount(params[1], params[2], params[3]);
			return "User account " + params[1] + " created.";
		} catch (Exception e1) {
			e1.printStackTrace();
			return "Failed to create account";
		}
	}
}
	