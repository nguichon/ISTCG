package Shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ThreadedConnectionDevice {
	private Thread connection = null;
	private Connection c = null;

	public ThreadedConnectionDevice( Socket socket ) throws IOException {
		c = new Connection(socket);
		connection = new Thread(c);
		connection.start();
	}
	public ThreadedConnectionDevice( String host, int port ) throws IOException {
		c = new Connection(host,port);
		connection = new Thread(c);
		connection.start();
	}
	
	public String getData() {
		return c.getData();
	}
	
	public void sendData( String Data ) {
		c.sendData(Data);
	}
	public void close() throws IOException {
		c.stop();
		connection = null;
	}
	public boolean isValid() {
		return c.isValid();
	}
	public boolean hasData(){
		return c.hasData();
	}
}
