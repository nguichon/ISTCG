package ServerTester;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.junit.Assert;

import Shared.ClientMessages;
import Shared.ClientResponses;

public class TestClient {
	private static final int m_Port = 4567;
	private static final String m_Host = "127.0.0.1";
	
	private PrintWriter output;
	private Scanner input;
	
	private int my_id;
	private String my_name;
	private int my_game;
	
	public TestClient( String name, String password ) {
		my_name = name;
		Socket toServer = null;
		try {
			toServer = new Socket( m_Host, m_Port );
		} catch (Exception e) {
			Assert.fail("Socket failed to be created");
		}
		
		try {
			output = new PrintWriter( toServer.getOutputStream());
			input = new Scanner( toServer.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		output.println( "LOGIN;" + name + ";" + password );
		output.flush();
		String[] result = input.nextLine().split(";");
		Assert.assertEquals("LOGIN_SUCCESS", result[0]);
		my_id = Integer.valueOf( result[1] );
		
	}

	public void SendMessage( ClientResponses type, String...parameters ) {
		String parameterString = "";
		for( int i = 0; i < parameters.length; i++ ) {
			parameterString += parameters[i] + ";";
		}
		
		synchronized(output){
			output.println( type.name() + ";" + parameterString);
			output.flush();
		}
	}
	
	public String[] GetResponse() {
		return input.nextLine().split(";");
	}
	
	public void Challenge(TestClient clientB) {
		SendMessage( ClientResponses.CHALLENGE, clientB.GetUserName() );

		String[] response;
		
		response = this.GetResponse();
		Assert.assertEquals( "JOIN", response[0] );
		my_game = Integer.valueOf( response[1] );
		
		response = clientB.GetResponse();
		Assert.assertEquals( "JOIN",  response[0] );
		Assert.assertEquals( my_game + "", response[1] );
		
		this.SendMessage( ClientResponses.DECKLIST, my_game + "", "2,60" );
		clientB.SendMessage( ClientResponses.DECKLIST, my_game + "", "2,60" );
	}

	public String GetUserID() {
		return my_id + "";
	}
	
	public String GetUserName() { return my_name; }

}
