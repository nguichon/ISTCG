package Shared;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ConnectionDevice {
	private Socket connectionSocket;
	private PrintWriter output;
	private Scanner input;
	private Cipher cif = null;
	private String keystring = "1941u404jo2nrfo 240rhn2 rhfowfofdnonjhr08r3 F@#RT#GV EADFw ijq34rt";
	
	

	public ConnectionDevice( Socket socket ) throws IOException {
		connectionSocket = socket;
		try {
			Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if( connectionSocket != null ) {
			input = new Scanner(connectionSocket.getInputStream());
			output = new PrintWriter(connectionSocket.getOutputStream());
		}
	}
	public ConnectionDevice( String host, int port ) throws IOException {
		connectionSocket = new Socket( host, port );
		try {
			Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( connectionSocket != null ) {
			input = new Scanner(connectionSocket.getInputStream());
			output = new PrintWriter(connectionSocket.getOutputStream());
		}
	}
	
	public String getData() {
		if( input.hasNext() ) {
			String tmp = input.next();
			String[] sA = tmp.split("&&&&&&");
			IvParameterSpec ips = new IvParameterSpec(sA[0].getBytes());
			MessageDigest digest = null;
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			digest.update(keystring.getBytes());
			byte[] key = new byte[16];
			System.arraycopy(digest.digest(), 0, key, 0, key.length);
			SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
			try {
				cif.init(Cipher.DECRYPT_MODE, keySpec, ips);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] decrypt = null;
			try {
				decrypt = cif.doFinal(sA[1].getBytes());
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new String(decrypt);
		}
		return "";
	}
	
	public void sendData( String Data ) {
		String input = Data;
		byte[] initVector = new byte[cif.getBlockSize()];
		new SecureRandom().nextBytes(initVector);
		IvParameterSpec ips = new IvParameterSpec(initVector);
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		digest.update(keystring.getBytes());
		byte[] key = new byte[16];
		System.arraycopy(digest.digest(), 0, key, 0, key.length);
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		try {
			cif.init(Cipher.ENCRYPT_MODE, keySpec, ips);
			byte[] encrypted = cif.doFinal(input.getBytes("UTF-8"));
			input = new String(encrypted);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		output.println( new String(ips.getIV())+"&&&&&&"+input );
		output.flush();
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
