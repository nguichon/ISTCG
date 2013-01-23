package Shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connection implements Runnable {

	private boolean exit = false;
	
	private Socket connectionSocket;
	private PrintWriter output;
	private Scanner input;
	
	public Connection (Socket socket) throws IOException {
		
		connectionSocket = socket;
		if( connectionSocket != null ) {
			input = new Scanner(connectionSocket.getInputStream());
			output = new PrintWriter(connectionSocket.getOutputStream());
		} else {
			throw new IOException();
		}
		
	}
	public Connection (String host, int port)  throws IOException {
		connectionSocket = new Socket( host, port );
		
		if( connectionSocket != null ) {
			input = new Scanner(connectionSocket.getInputStream());
			output = new PrintWriter(connectionSocket.getOutputStream());
		} else {
			throw new IOException();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!exit) {
			//Do something
			
		}
	}
	
	public String getData() {
		try {
			if( input.hasNext() ) {
				System.out.println("Getting data");
				return input.next();
			}
			return "";
		} catch (Exception e){
			System.err.println("Network problem.");
			return "";
		}
	}
	public void sendData( String Data ) {
		//Test if live
		try {
			System.out.println("Sending data");
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
	public void stop() {
		try {
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		exit=true;
	}
}
