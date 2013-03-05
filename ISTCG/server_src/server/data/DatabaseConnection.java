package server.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import server.ServerMain;

public class DatabaseConnection {
	private final static String DATABASE_URL = "localhost/tcg_server";
	private final static String DATABASE_LOGIN = "postgres";
	private final static String DATABASE_PASSWORD = "password";
	private final static DatabaseConnection INSTANCE = new DatabaseConnection();

	private Connection m_DBConnection;

	/**
	 * Gets the only instance of this class.
	 * 
	 * @return A reference the the singleton instance
	 */
	public static DatabaseConnection getInstance() {
		if (INSTANCE == null) {
			throw new RuntimeException("No singleton instance available");
		}

		return INSTANCE;
	}

	/**
	 * Private constructor. Creates a connection to the database.
	 */
	private DatabaseConnection() {
		try {
			m_DBConnection = DriverManager.getConnection("jdbc:postgresql://"
					+ DATABASE_URL, DATABASE_LOGIN, DATABASE_PASSWORD);
		} catch (SQLException e) {
			ServerMain.DisplayMessage("!!!",
					"Failed to create database connection.");
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
