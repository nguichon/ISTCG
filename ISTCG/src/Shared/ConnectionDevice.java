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

<<<<<<< HEAD
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
=======
import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;
>>>>>>> parent of a59123a... Added two Server console commands "select" for DB queries, and

public class ConnectionDevice {
	private Socket connectionSocket;
	private PrintWriter output;
	private Scanner input;
<<<<<<< HEAD
	private Cipher cif = null;
	private String keystring = "1941u404jo2nrfo 240rhn2 rhfowfofdnonjhr08r3 F@#RT#GV EADFw ijq34rt";
	
	
=======
	private Crypter crypter=null;
>>>>>>> parent of a59123a... Added two Server console commands "select" for DB queries, and

	public ConnectionDevice( Socket socket ) throws IOException {
		connectionSocket = socket;
		
		input = new Scanner(connectionSocket.getInputStream());
		output = new PrintWriter(connectionSocket.getOutputStream());
<<<<<<< HEAD
		try {
			Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
=======
		
		try {
			crypter = new Crypter(".");
		} catch (KeyczarException e) {
>>>>>>> parent of a59123a... Added two Server console commands "select" for DB queries, and
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public ConnectionDevice( String host, int port ) throws IOException {
		connectionSocket = new Socket( host, port );
		
		input = new Scanner(connectionSocket.getInputStream());
		output = new PrintWriter(connectionSocket.getOutputStream());
		
		try {
<<<<<<< HEAD
			Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
=======
			crypter = new Crypter(".");
		} catch (KeyczarException e) {
>>>>>>> parent of a59123a... Added two Server console commands "select" for DB queries, and
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getData() {
		if( input.hasNext() ) {
<<<<<<< HEAD
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
=======
			String s = "";
			try {
				s = crypter.decrypt(input.next());
			} catch (KeyczarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return s;
>>>>>>> parent of a59123a... Added two Server console commands "select" for DB queries, and
		}
		return "";
	}
	
	public void sendData( String Data ) {
<<<<<<< HEAD
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
=======
		String s = "";
		try {
			s = crypter.encrypt(Data);
		} catch (KeyczarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		output.println( s );
>>>>>>> parent of a59123a... Added two Server console commands "select" for DB queries, and
		output.flush();
	}
	
	public void close() throws IOException {
		input.close();
		output.close();
		connectionSocket.close();
	}
}
