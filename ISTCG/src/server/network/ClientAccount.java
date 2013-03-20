package server.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.postgresql.util.Base64;

import Shared.ClientMessages;
import Shared.ClientResponses;

import server.Database;
import server.LobbyManager;
import server.ServerMain;
import server.games.GameManager;

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
		m_AdminAccount = false;
		m_ToClient = client;

		try {
			m_Input = new Scanner(m_ToClient.getInputStream());
			m_Output = new PrintWriter(m_ToClient.getOutputStream());

			m_Connected = true;
		} catch (IOException e) {
			ServerMain
					.ConsoleMessage('!',
							"A client connected, but creating Scanner and Print Writer failed.");
			e.printStackTrace();
		}
	}

	// ===================================
	// -NETWORK FUNCTIONS-
	// ===================================
	private Socket m_ToClient;
	private PrintWriter m_Output;
	private Scanner m_Input;
	private boolean m_Connected;

	public void SendMessage(ClientMessages messageType, String ... parameters ) {
		String parameterString = "";
		for( int i = 0; i < parameters.length; i++ ) {
			parameterString += parameters[i] + ";";
		}
		
		synchronized(m_Output){
			m_Output.println(messageType.name() + ";" + parameterString);
			m_Output.flush();
		}
	}

	public void run() {
		while (m_Connected) {
			String line = m_Input.nextLine();
			try {
				if (line != null && !line.isEmpty()) {
					String command[] = line.split(";");
					switch (ClientResponses.valueOf(command[0].toUpperCase())) {
					case LOGIN:
						LoginAttempt(command[1], command[2]);
						break;
					case SAY:
						if (m_UserID != -1) {
							LobbyManager.say(m_UserName, command[1]);
						}
						break;
					case TELL:
						if (m_UserID != -1) {
							LobbyManager.whisper( this, command[1], command[2]);
						}
						break;
					case LOGOUT:
						DisconnectMe();
						break;
					case DISCONNECT:
						DisconnectMe();
						break;
					case CHALLENGE:
						if (m_UserID != -1) {
							ClientAccount opponent = ConnectionsHandler.get()
									.GetClientByName(command[1]);
							GameManager.get().CreateGame(this, opponent);
						}
						break;
					case ADMIN:
						if (m_AdminAccount) {
							SendMessage(ClientMessages.SERVER, ServerMain.RunCommand(command[1]));
						}
					case GETCOLLECTION:
						SendPlayerCollection();
						break;
					case DECKLIST:
						// Drop through
					case PLAY:
						// Drop through
					case GETCARDINFO:
						// Drop through
					case END:
						GameManager.get().SendMessageToGame(
								Integer.valueOf(command[1]), this.m_UserID,
								command);
						break;
					default:
						break;
					}
				}
			} catch (Exception e) {
				System.out.println( "MESSAGE WAS \"" + line + "\"");
				e.printStackTrace();
				DisconnectMe();
			}
		}
	}

	private void SendPlayerCollection() {
		ResultSet rs = Database.get().quickQuery("SELECT COLLECTIONS.count, COLLECTIONS.card_id FROM COLLECTIONS WHERE owner_id = " + this.getUserID() + ";");
		String toSend = "";
		try {
			while( rs.next() ) {
				toSend += rs.getInt( "card_id" ) + "," + rs.getInt( "count" ) + "|";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SendMessage( ClientMessages.COLLECTION, toSend );
	}

	public void DisconnectMe() {
		if (m_UserName != null) {
			ServerMain.ConsoleMessage('-', m_UserName + " logged out.");
		} else {
			ServerMain.ConsoleMessage('-', "Unknown user disconnected");
		}
		ConnectionsHandler.get().RemoveConnectedClientAccount(this);

		m_UserID = -1;
		m_UserName = null;
		m_AdminAccount = false;

		try {
			m_Output.close();
			m_Input.close();
			m_ToClient.close();
		} catch (IOException e) {
			ServerMain
					.ConsoleMessage(
							'!',
							"Failed to close Socket, Scanner, and/or PrintWriter for a disconnecting/disconnected client.");
			e.printStackTrace();
		}

		m_Connected = false;
	}

	// ===================================
	// -ACCOUNT FUNCTIONS-
	// ===================================
	private int m_UserID;
	private String m_UserName;
	private boolean m_AdminAccount = false;

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
				Database.get().quickInsert(
						"UPDATE Users SET last_login = NOW() WHERE Users.id = "
								+ m_UserID + ";");
				// m_AdminAccount = rs.getBoolean("is_admin");
				// TODO fix the check for admin, so not everyone is an admin :P
				m_AdminAccount = true;
			} else {
				m_UserID = -1;
				return false;
			}
		} catch (SQLException e) {
			ServerMain.ConsoleMessage('!',
					"Problem accessing database while authenticating user "
							+ user_name);
			e.printStackTrace();
		} catch (Exception e) {
			ServerMain.ConsoleMessage('!', "Error while authenticating user "
					+ user_name);
			e.printStackTrace();
		}
		return true;
	}

	public String getUserName() {
		return this.m_UserName;
	}

	public int getUserID() {
		return this.m_UserID;
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
		boolean success = false;
		if (m_UserID == -1) {
			if (Authenticate(name, password)) {
				if (ConnectionsHandler.get().GetClientByName(this.m_UserName) != null) {
					SendMessage( ClientMessages.LOGIN_FAILED, "User is logged in at another location.");
					m_UserName = null;
					m_UserID = -1;
				} else {
					ServerMain.ConsoleMessage('-', m_UserName + " logged in.");
					SendMessage( ClientMessages.LOGIN_SUCCESS, "" + m_UserID);
					LobbyManager.loginMessage(m_UserName);
					ConnectionsHandler.get().Authenticated(this, m_UserName);
					success = true;
				}
			} else {
				SendMessage( ClientMessages.LOGIN_FAILED, "Bad login information.");
			}
		} else {
			SendMessage( ClientMessages.LOGIN_FAILED, "Already logged in.");
		}

		if (!success) {
			DisconnectMe();
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
