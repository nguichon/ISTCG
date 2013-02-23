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
		
		Assert.assertEquals("LOGIN_SUCCESS", s.nextLine());
	}
}
