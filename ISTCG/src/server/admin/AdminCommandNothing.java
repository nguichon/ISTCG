package server.admin;

public class AdminCommandNothing extends AdminCommand {
	@Override
	public String Activate(String[] params) {
		return "You get nothing! Good day, sir!";
	}
}
	