package ServerTester;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import Shared.ClientMessages;
import Shared.ClientResponses;


public class GameTest {
	private static int m_Port = 4567;
	private static String m_Host = "127.0.0.1";
	
	@Test
	public void TestGameFlow() {
		TestClient clientA = new TestClient( "Grogian", "password" );
		TestClient clientB = new TestClient( "TestUser", "password" );
		String[] response = clientA.GetResponse();
		Assert.assertEquals( "USER_LOGGED_IN", response[0] );
		
		clientA.Challenge( clientB );
		
		
		response = clientA.GetResponse();
		while( response[0].equals( ClientMessages.MOVE.name() ) || response[0].equals( ClientMessages.UPDATE_ZONE.name() )) {
			response = clientA.GetResponse();

		}
		
		response = clientB.GetResponse();
		while( response[0].equals( ClientMessages.MOVE.name() ) || response[0].equals( ClientMessages.UPDATE_ZONE.name() )) {
			response = clientB.GetResponse();

		}
	}
}
