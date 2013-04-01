package server.admin;

import server.network.ConnectionsHandler;
import Shared.ClientMessages;

public class AdminCommandShout extends AdminCommand {
	@Override
	public String Activate(String[] params) {
		String s = "";
		for( int i = 1; i < params.length; i++ ) {
			s += params[i];
		}
		ConnectionsHandler.get().SendMessageToAllAuthenticated( ClientMessages.MESSAGE, "Server", s );
		return "Message sent.";
	}
}
	