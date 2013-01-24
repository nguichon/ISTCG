package Shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Connection implements Runnable {

	private boolean exit = false;
	
	private Socket connectionSocket;
	private PrintWriter output;
	private Scanner input;
	private PriorityQueue<String> q = null;
	public Connection (Socket socket) throws IOException {
		q = new PriorityQueue<String>();
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
		q = new PriorityQueue<String>();
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
			getInput();
		}
	}
	
	private void getInput() {
		if(input.hasNext()){
			String s = input.nextLine();
			//System.out.println("Got data "+s);
			q.add(s);
		}
	}
	public boolean hasData(){
		return !q.isEmpty();
	}
	public String getData(){
		return q.poll();
	}
	public void sendData( String Data ) {
		//Test if live
		try {
			String s = Data;
			//System.out.println("Sending data: "+s);
		output.println( Data );
		output.flush();
		} catch (Exception e) {
			System.err.println("Disconnected. Failed to send data");
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
