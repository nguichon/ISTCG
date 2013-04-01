package FalseClient;

import org.eclipse.swt.widgets.Display;

import OldClient.ClientMain;

public class AutoClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Display display = new Display();
		ClientMain client = new ClientMain(display);
		
		//assert that client is up
		
		assert(client instanceof ClientMain);
		System.out.println("AutoClient is running...");
		client.Login("user2", "password");
		for(int i=0;i<500;i++){
			if(client.isLoggedIn()){
				break;
			}
		}
		assert(client.isLoggedIn());
		System.out.println("AutoClient is logged in");
		client.SendTextMessage("Auto Client is logged in.");
		for(int i=0;i<500;i++){
			if(!client.getTextContents().isEmpty())
				break;
		}
		assert(client.getTextContents().contains("Auto Client is logged in."));
		System.out.println("AutoClient is sending messages");
		display.dispose();
	}

}
