package server.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private static Database instance;
	
	public static Database get() { return instance; }
	public static void initialize() throws SQLException {
		instance = new Database();
		
		instance.m_DBConnection = DriverManager.getConnection(
			"jdbc:postgresql://localhost/tcg_server", "postgres",
			"password");
	}
	
	private Database() { }
	
	//==========================================
	//Start instance variables
	//==========================================

	private Connection m_DBConnection;
	
	//==========================================
	//Start public methods
	//==========================================
	
	public ResultSet quickQuery( String query ) {
		try {
			return m_DBConnection.prepareCall( query ).executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void quickInsert( String query ) {
		try {
			//CallableStatement cs = m_DBConnection.prepareCall( query );
			Statement s = m_DBConnection.createStatement();
			s.executeUpdate( query );
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
