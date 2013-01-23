package Shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionDevice {
	private Socket connectionSocket;
	private PrintWriter output;
	private Scanner input;
	

	public ConnectionDevice( Socket socket ) throws IOException {
		connectionSocket = socket;
		if( connectionSocket != null ) {
			input = new Scanner(connectionSocket.getInputStream());
			output = new PrintWriter(connectionSocket.getOutputStream());
		}
	}
	public ConnectionDevice( String host, int port ) throws IOException {
		connectionSocket = new Socket( host, port );
		
		if( connectionSocket != null ) {
			input = new Scanner(connectionSocket.getInputStream());
			output = new PrintWriter(connectionSocket.getOutputStream());
		}
	}
	
	public String getData() {
		if( input.hasNext() ) {
			return input.next();
		}
		return "";
	}
	
	public void sendData( String Data ) {
		//Test if live
		try {
		output.println( Data );
		output.flush();
		} catch (Exception e) {
			System.err.println("Disconnected.");
		}
	}
	
	public void close() throws IOException {
		input.close();
		output.close();
		connectionSocket.close();
	}
	public boolean isValid() {
		if( connectionSocket != null ) {
			return true;
		}
		return false;
	}
	
}
