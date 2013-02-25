package ServerTester;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ServerTestCases {
	private static int m_Port = 4567;
	private static String m_Host = "127.0.0.1";
	private static String m_User = "Grogian";
	private static String m_Pass = "password";
	private static String m_User2 = "TestUser";
	private static String m_Pass2 = "password";
		
	private Socket makeConnection() {
		Socket toServer = null;
		try {
			toServer = new Socket( m_Host, m_Port );
		} catch (Exception e) {
			Assert.fail("Socket failed to be created");
		}
		return toServer;
	}
	
	@Test
	public void testCanConnectToServer() {
		Socket toServer = makeConnection();
		Assert.assertTrue(toServer != null);
		Assert.assertTrue(toServer.isConnected());
	}
	
	@Test
	public void testTwoPlayersCanLogin() {
		Socket clientA = makeConnection();
		PrintWriter clientAOut = null;
		Scanner clientAIn = null;
		
		Socket clientB = makeConnection();
		PrintWriter clientBOut = null;
		Scanner clientBIn = null;
		
		try {
			clientAOut = new PrintWriter(clientA.getOutputStream());
			clientAIn = new Scanner(clientA.getInputStream());

			clientBOut = new PrintWriter(clientB.getOutputStream());
			clientBIn = new Scanner(clientB.getInputStream());
		} catch (IOException e) {
			Assert.fail("Failed to create Scanner or Writer");
		}
		
		clientAOut.println("LOGIN;" + m_User + ";" + m_Pass);
		clientAOut.flush();
		Assert.assertEquals("LOGIN_SUCCESS;3", clientAIn.nextLine());

		clientBOut.println("LOGIN;" + m_User2 + ";" + m_Pass2);
		clientBOut.flush();
		Assert.assertEquals("LOGIN_SUCCESS;5", clientBIn.nextLine());
		Assert.assertEquals("LOGGED_IN_MESSAGE;TestUser", clientAIn.nextLine());
		

		clientAOut.println("DISCONNECT");
		clientAOut.flush();
		clientBOut.println("DISCONNECT");
		clientBOut.flush();
	}

	@Test
	public void testPlayerCanChallengeAnotherPlayer() {
		Socket clientA = makeConnection();
		PrintWriter clientAOut = null;
		Scanner clientAIn = null;
		
		Socket clientB = makeConnection();
		PrintWriter clientBOut = null;
		Scanner clientBIn = null;
		
		try {
			clientAOut = new PrintWriter(clientA.getOutputStream());
			clientAIn = new Scanner(clientA.getInputStream());

			clientBOut = new PrintWriter(clientB.getOutputStream());
			clientBIn = new Scanner(clientB.getInputStream());
		} catch (IOException e) {
			Assert.fail("Failed to create Scanner or Writer");
		}
		
		clientAOut.println("LOGIN;" + m_User + ";" + m_Pass);
		clientAOut.flush();
		Assert.assertEquals("LOGIN_SUCCESS;3", clientAIn.nextLine());

		clientBOut.println("LOGIN;" + m_User2 + ";" + m_Pass2);
		clientBOut.flush();
		Assert.assertEquals("LOGIN_SUCCESS;5", clientBIn.nextLine());
		Assert.assertEquals("LOGGED_IN_MESSAGE;TestUser", clientAIn.nextLine());
		
		clientAOut.println("CHALLENGE;" + m_User2);
		clientAOut.flush();
		
		String[] testString = clientAIn.nextLine().split(";");
		Assert.assertEquals("JOIN", testString[0]);
		int game_id = Integer.parseInt( testString[1] );
		testString = clientBIn.nextLine().split(";");
		Assert.assertEquals("JOIN", testString[0]);
		Assert.assertEquals( game_id, Integer.parseInt(testString[1]));
		

		clientAOut.println("DISCONNECT");
		clientAOut.flush();
		clientBOut.println("DISCONNECT");
		clientBOut.flush();
	}
	
	@Test
	public void testLoginGoodCredentialsSuccessful() {
		Socket toServer = makeConnection();
		PrintWriter pw = null;
		Scanner s = null;
		try {
			pw = new PrintWriter(toServer.getOutputStream());
			s = new Scanner(toServer.getInputStream());
		} catch (IOException e) {
			Assert.fail("Failed to create Scanner or Writer");
		}
		
		pw.println("LOGIN;" + m_User + ";" + m_Pass);
		pw.flush();
		
		Assert.assertEquals("LOGIN_SUCCESS", s.nextLine().split(";")[0]);
		
		pw.println("DISCONNECT");
		pw.flush();
	}
	
	@Test
	public void testLoginGoodCredentialsMultipleLoginFails() {
		Socket client1 = makeConnection();
		Socket client2 = makeConnection();
		
		PrintWriter pw1 = null;
		Scanner s1 = null;

		PrintWriter pw2 = null;
		Scanner s2 = null;
		
		try {
			pw1 = new PrintWriter(client1.getOutputStream());
			s1 = new Scanner(client1.getInputStream());
			pw2 = new PrintWriter(client2.getOutputStream());
			s2 = new Scanner(client2.getInputStream());
		} catch (IOException e) {
			Assert.fail("Failed to create Scanner or Writer");
		}
		
		pw1.println("LOGIN;" + m_User + ";" + m_Pass);
		pw1.flush();
		
		Assert.assertEquals("LOGIN_SUCCESS", s1.nextLine().split(";")[0]);
		
		pw2.println("LOGIN;" + m_User + ";" + m_Pass);
		pw2.flush();
		
		Assert.assertEquals("LOGIN_FAILED;User is logged in at another location.", s2.nextLine());
		
		pw1.println("DISCONNECT");
		pw1.flush();
		
		pw2.println("DISCONNECT");
		pw2.flush();
	}
}
