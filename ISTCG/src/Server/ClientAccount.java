package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.postgresql.util.Base64;

import Shared.ThreadedConnectionDevice;

/**
 * @author Nicholas Guichon
 */
public class ClientAccount extends Thread {
	/**
	 * Constructor for a this class. Adds the ClientAccount to MessageHandler's
	 * queue.
	 * 
	 * @param client
	 *            A valid ThreadedConnectionDevice, unique to this ClientAccount
	 */
	public ClientAccount(Socket client) {
		m_UserID = -1;
		m_ToClient = client;
		
		try {
			m_Input = new Scanner( m_ToClient.getInputStream() );
			m_Output = new PrintWriter( m_ToClient.getOutputStream() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// ===================================
	// -NETWORK FUNCTIONS-
	// ===================================
	private Socket m_ToClient;
	private PrintWriter m_Output;
	private Scanner m_Input;
	/**
	 * 
	 * Various commands received from a client.
	 * 
	 * @author Nicholas Guichon
	 * 
	 */
	private enum ClientMessages {
		LOGIN, SAY, TELL;
	}
	
	public void SendMessage( String message ) {
		m_Output.println( message );
		m_Output.flush();
	}
	public void run() {
		boolean connected = true;
		
		while( connected ) {
			String line = m_Input.nextLine();
			if( line != null ) {
				String command[] = line.split(";");
	            switch( ClientMessages.valueOf( command[0].toUpperCase() ) ) {
	            case LOGIN:
	                    LoginAttempt(command[1], command[2]);
	                    break;
	            case SAY:
	                    LobbyManager.say(m_UserName, command[1]);
	                    break;
	            case TELL:
	                    LobbyManager.whisper(m_UserName, command[1], command[2]);
	                    SendMessage("SAY;" + "to [" + command[1] + "];" + command[2]);
	                    break;
	            default:
	                    break;
	            }
			}
		}
	}
	// ===================================
	// -ACCOUNT FUNCTIONS-
	// ===================================
	private int m_UserID;
	private String m_UserName;

	/**
	 * Creates a new row in the table Users
	 * 
	 * @param name
	 *            Desired username for the new account, must be unique
	 * @param password
	 *            Desired un-hashed password for the user, will be hashed
	 * @param email
	 *            Email address for the user.
	 * @throws Exception
	 *             User in the database already has the desired user name.
	 */
	public static void NewAccount(String name, String password, String email)
			throws Exception {
		String db_password = getSaltedHash(password);

		ResultSet rs = Database.get().quickQuery(
				"SELECT * FROM users WHERE user_name = '" + name + "';");
		if (!rs.next()) {
			Database.get()
					.quickInsert(
							"INSERT INTO users(id, created_at, modified_at, user_name, password, email, points)"
									+ " VALUES (DEFAULT, NULL, NULL, '"
									+ name
									+ "', '"
									+ db_password
									+ "', '"
									+ email
									+ "', DEFAULT);");
		} else {
			throw new Exception("User name already exists");
		}
	}

	/**
	 * Attempts to authenticate a user.
	 * 
	 * @param user_name
	 * @param password_text
	 * @return true if the user login was valid and login was successful or
	 *         false if the user login was not valid
	 */
	public boolean Authenticate(String user_name, String password_text) {
		ResultSet rs = Database.get().quickQuery(
				"SELECT * FROM users WHERE user_name = '" + user_name + "';");
		try {
			if (!rs.next()) {
				m_UserID = -1;
				return false;
			}
			if (check(password_text, rs.getString("password"))) {
				m_UserID = rs.getInt("id");
				m_UserName = rs.getString("user_name");
				
			} else {
				m_UserID = -1;
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ConnectionsHandler.get().Authenticated( this, m_UserName );
		return true;
	}

	public String getUserName(){
		return this.m_UserName;
	}

	/**
	 * Attempts to log the user in. Is necessary for any other command to be
	 * processed.
	 * 
	 * @param name
	 *            User's login name
	 * @param password
	 *            User's password
	 */
	private void LoginAttempt(String name, String password) {
		if (m_UserID == -1) {
			if (Authenticate(name, password)) {
				SendMessage("LOGIN_SUCCESS");
				LobbyManager.loginMessage(m_UserName);
			} else {
				SendMessage("LOGIN_FAILED");
			}
		} else {
			SendMessage("ALREADY_LOGGED_IN");
		}
	}

	// ===================================
	// -LINKED LIST FUNCTIONS-
	// ===================================
	private ClientAccount m_Next;
	private ClientAccount m_Previous;
	private boolean m_ToRemove;

	public void SetNext(ClientAccount cm) {
		m_Next = cm;
	}

	public ClientAccount GetNext() {
		return m_Next;
	}

	public void SetPrevious(ClientAccount cm) {
		m_Previous = cm;
	}

	public void SetRemove() {
		m_ToRemove = true;
	}

	public ClientAccount RetrieveNext() {
		if (m_ToRemove == true) {
			m_Previous.SetNext(m_Next);
			m_Next.SetPrevious(m_Previous);
		}
		return m_Next;
	}

	public void InsertAfter(ClientAccount ca) {
		ca.SetNext(m_Next);
		ca.SetPrevious(this);
		m_Next = ca;
	}

	public boolean RemoveMe() {
		return m_ToRemove;
	}

	// ===================================
	// -PASSWORD FUNCTIONS-
	// ===================================
	private static final int iterations = 10 * 1024;
	private static final int saltLen = 32;
	private static final int desiredKeyLen = 256;

	/**
	 * Computes a salted PBKDF2 hash of given plaintext password suitable for
	 * storing in a database.
	 */
	public static String getSaltedHash(String password) throws Exception {
		byte[] salt = SecureRandom.getInstance("SHA1PRNG")
				.generateSeed(saltLen);
		// store the salt with the password
		return Base64.encodeBytes(salt) + "$" + hash(password, salt);
	}

	// using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
	// cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
	private static String hash(String password, byte[] salt) throws Exception {
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(),
				salt, iterations, desiredKeyLen));
		return Base64.encodeBytes(key.getEncoded());
	}

	/**
	 * Checks whether given plaintext password corresponds to a stored salted
	 * hash of the password.
	 */
	public static boolean check(String password, String stored)
			throws Exception {
		String[] saltAndPass = stored.split("\\$");
		if (saltAndPass.length != 2) {
			return false;
		}
		String hashOfInput = hash(password, Base64.decode(saltAndPass[0]));
		return hashOfInput.equals(saltAndPass[1]);
	}
}
