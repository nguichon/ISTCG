package server.admin;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import server.Database;

public class AdminCommandSelect extends AdminCommand {
	private final int MINIMUM_NUMBER_OF_PARAMETERS = 2;
	
	@Override
	public String Activate(String[] params) {
		//Checking for valid # of parameters
		if( params.length < MINIMUM_NUMBER_OF_PARAMETERS ) {
			return "Invalid number of parameters.";
		}
		
		//Run the query
		String query = "";
		for( int i = 0; i < params.length; i++) {
			query += params[i] + " ";
		}
		ResultSet rs = Database.get().quickQuery(query);
		
		//Prepare to return result
		String toReturn = "Result of query:";
		
		//Attempt to parse/display result
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				if (i != 1) {
					toReturn += " | ";
				} else {
					toReturn += "\n";
				}
				toReturn += rsmd.getColumnName(i);
			}
			
			while (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (i != 1) {
						toReturn += " | ";
					} else {
						toReturn += "\n";
					}
					toReturn += rs.getString(rsmd.getColumnName(i));
				}
			}
			
			toReturn += "\nEND";
			return toReturn;
		} catch (SQLException e) {
			e.printStackTrace();
			return "Query failed.";
		}
	}
}
	