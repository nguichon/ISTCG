package FalseClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import OldClient.LoginUI;
import OldClient.ClientMain.GameState;
import Shared.ConnectionDevice;
import Shared.ThreadedConnectionDevice;

public class FalseClient {
	private final static String HOST_IP = "127.0.0.1";
	private final static int HOST_PORT = 4567;
	
	private static Scanner input;
	private static PrintWriter output;
	private static Scanner consoleInput;
	
	private static boolean m_Quit = false;
	
	
	private class Reader extends Thread {
		@Override
		public void run() {
			while( !m_Quit ) {
				if( input.hasNext() ) {
					System.out.println(input.nextLine());
				}
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		consoleInput = new Scanner( System.in );
		
		try {
			Socket socket = new Socket( HOST_IP, HOST_PORT );
			input = new Scanner( socket.getInputStream() );
			output = new PrintWriter( socket.getOutputStream() );
		} catch (Exception e) {
			System.out.println( "Socket faild to connect, or something" );
			System.exit( - 1 );
		}
		
		new FalseClient().RunThings();
		
		while( !m_Quit ) {
			String input = consoleInput.nextLine();
			
			if( input.equals( "quit" ) ) {
				m_Quit = true;
				input = "disconnect";
			}
			
			if( !input.equals("") ) {
				output.println( input );
				output.flush();
			}
		}
	}
	
	public void RunThings() {
		(this.new Reader()).start();
	}
	

}
