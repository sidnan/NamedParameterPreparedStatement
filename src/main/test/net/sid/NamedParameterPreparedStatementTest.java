/**
 * 
 */
package net.sid;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

/**
 * @author sidnan
 *
 */
public class NamedParameterPreparedStatementTest {

	@Test
	public void test() {
		String query = "SELECT * FROM CUSTOMER WHERE customer_name LIKE :custName";
		try {
			NamedParameterPreparedStatement npPrepStmt = new NamedParameterPreparedStatement(getDBConnection(), query);
			npPrepStmt.setString("custName", "Asia%");
			ResultSet rs = npPrepStmt.executeQuery();
			if(rs.next()) {
				System.out.println("found");
			} else {
				System.out.println("No result found");
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getDBConnection() throws SQLException, ClassNotFoundException {
		Class.forName("oracle.jdbc.OracleDriver");
		Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=timstsnap00.ivanet.net)(PORT=1521))(CONNECT_DATA=(SERVER=DEDICATED)(SID=timsrdg2)))", "RMSSMSSYS", "RMSSMSSYS123");
		return connection;
	}

}
