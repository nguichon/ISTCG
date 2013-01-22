package Shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;

public class ConnectionDevice {
	private Socket connectionSocket;
	private PrintWriter output;
	private Scanner input;
	private Crypter crypter=null;

	public ConnectionDevice( Socket socket ) throws IOException {
		connectionSocket = socket;
		
		input = new Scanner(connectionSocket.getInputStream());
		output = new PrintWriter(connectionSocket.getOutputStream());
		
		try {
			crypter = new Crypter(".");
		} catch (KeyczarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public ConnectionDevice( String host, int port ) throws IOException {
		connectionSocket = new Socket( host, port );
		
		input = new Scanner(connectionSocket.getInputStream());
		output = new PrintWriter(connectionSocket.getOutputStream());
		
		try {
			crypter = new Crypter(".");
		} catch (KeyczarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getData() {
		if( input.hasNext() ) {
			String s = "";
			try {
				s = crypter.decrypt(input.next());
			} catch (KeyczarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return s;
		}
		return "";
	}
	
	public void sendData( String Data ) {
		String s = "";
		try {
			s = crypter.encrypt(Data);
		} catch (KeyczarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		output.println( s );
		output.flush();
	}
	
	public void close() throws IOException {
		input.close();
		output.close();
		connectionSocket.close();
	}
}
