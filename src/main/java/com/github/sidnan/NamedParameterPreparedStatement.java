/**
 * 
 */
package com.github.sidnan;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sidnan
 *
 */
public class NamedParameterPreparedStatement {

	// PreparedStatement wrapped by this object
	private final PreparedStatement statement;

	// Map parameter names to parameter index 
	private final Map<String, List<Integer>> indexMap;

	/**
	 * Creates a NamedParameterPreparedStatement
	 * @param connection DB connection
	 * @param query      parameterized query
	 * @throws SQLException if the statement could not be created
	 */
	public NamedParameterPreparedStatement(Connection connection, String query) throws SQLException {
		indexMap = new HashMap<String, List<Integer>>();
		String parsedQuery = parse(query, indexMap);
		statement=connection.prepareStatement(parsedQuery);
	}

	/**
	 * Parses a query with named parameters. 
	 * The parameter-index mappings are put into the map,
	 * and the parsed query is returned. 
	 * @param query    query to parse
	 * @param paramMap map to hold parameter-index mappings
	 * @return the parsed query
	 */
	/**
	 * Parses query with named parameters
	 * and map the parameter name to the parameter index
	 * @param query
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	final String parse(String query, Map paramMap) {
		int length = query.length();
		StringBuffer parsedQuery = new StringBuffer(length);
		boolean withInSingleQuote = false;
		boolean withInDoubleQuote = false;
		int index=1;

		for(int i=0;i<length;i++) {
			char c=query.charAt(i);

			/*
			 * Parameter name is identified by the format ":<param_name>"
			 * There could be a case where the SQL has filter with search content ":"
			 * Example: select * from EMPLOYEE where emp_address like ':sometext'
			 * In the above example ":sometext" is not a NamedParameter, it is just a search filter content
			 * To overcome this, the code below makes sure the param name is not with in Single/Double Quote 
			 */

			if(withInSingleQuote) {
				// Skip processing characters within single quote
				if(c=='\'') {
					withInSingleQuote=false;
				}
			} else if(withInDoubleQuote) {
				// Skip processing characters within double quote
				if(c=='"') {
					withInDoubleQuote=false;
				}
			} else {
				if(c=='\'') {
					// Skip processing characters within single quote
					withInSingleQuote=true;
				} else if(c=='"') {
					// Skip processing characters within double quote
					withInDoubleQuote=true;
				} else if(c==':' && i+1<length && Character.isJavaIdentifierStart(query.charAt(i+1))) {
					// Identify the NamedParameter
					int j=i+2;
					while(j<length && Character.isJavaIdentifierPart(query.charAt(j))) {
						j++;
					}
					// Get the NamedParameter
					String name=query.substring(i+1,j);
					c='?'; // replace the parameter name with a question mark
					i+=name.length(); // skip past the end of the parameter

					// Param index are put in List because there could be SQL with same param which expect same value
					List indexList=(List)paramMap.get(name);
					if(indexList==null) {
						indexList=new ArrayList();
						paramMap.put(name, indexList);
					}
					indexList.add(new Integer(index));

					index++;
				}
			}
			parsedQuery.append(c);
		}

		// replace the lists of Integer objects with arrays of ints
		/*for(Iterator itr=paramMap.entrySet().iterator(); itr.hasNext();) {
			Map.Entry entry=(Map.Entry)itr.next();
			List list=(List)entry.getValue();
			int[] indexes=new int[list.size()];
			int i=0;
			for(Iterator itr2=list.iterator(); itr2.hasNext();) {
				Integer x=(Integer)itr2.next();
				indexes[i++]=x.intValue();
			}
			entry.setValue(indexes);
		}*/

		return parsedQuery.toString();
	}

	/**
	 * Returns the indexes for a parameter.
	 * @param name name of the parameter
	 * @return parameter indexes of the given parameter name
	 * @throws IllegalArgumentException if the parameter does not exist
	 */
	private List<Integer> getIndexes(String name) {
		List<Integer> indexes=(List<Integer>) indexMap.get(name);
		if(indexes==null) {
			throw new IllegalArgumentException("Parameter not found: "+name);
		}
		return indexes;
	}

	/**
	 * Sets value of type Object for the given parameter name.
	 * @param name  parameter name
	 * @param value parameter value
	 * @throws SQLException if an error occurred
	 * @throws IllegalArgumentException if the parameter does not exist
	 * @see PreparedStatement#setObject(int, java.lang.Object)
	 */
	public void setObject(String name, Object value) throws SQLException {
		List<Integer> indexes=getIndexes(name);
		for(Integer index: indexes) {
			statement.setObject(index.intValue(), value);
		}
	}

	/**
	 * Sets value of type String for the given parameter name.
	 * @param name  parameter name
	 * @param value parameter value
	 * @throws SQLException if an error occurred
	 * @throws IllegalArgumentException if the parameter does not exist
	 * @see PreparedStatement#setString(int, java.lang.String)
	 */
	public void setString(String name, String value) throws SQLException {
		List<Integer> indexes=getIndexes(name);
		for(Integer index: indexes) {
			statement.setString(index.intValue(), value);
		}
	}

	/**
	 * Sets value of type Int for the given parameter name.
	 * @param name  parameter name
	 * @param value parameter value
	 * @throws SQLException if an error occurred
	 * @throws IllegalArgumentException if the parameter does not exist
	 * @see PreparedStatement#setInt(int, int)
	 */
	public void setInt(String name, int value) throws SQLException {
		List<Integer> indexes=getIndexes(name);
		for(Integer index: indexes) {
			statement.setInt(index.intValue(), value);
		}
	}

	/**
	 * Sets value of type long for the given parameter name.
	 * @param name  parameter name
	 * @param value parameter value
	 * @throws SQLException if an error occurred
	 * @throws IllegalArgumentException if the parameter does not exist
	 * @see PreparedStatement#setInt(int, long)
	 */
	public void setLong(String name, long value) throws SQLException {
		List<Integer> indexes=getIndexes(name);
		for(Integer index: indexes) {
			statement.setLong(index.intValue(), value);
		}
	}

	/**
	 * Sets value of type Timestamp for the given parameter name.
	 * @param name  parameter name
	 * @param value parameter value
	 * @throws SQLException if an error occurred
	 * @throws IllegalArgumentException if the parameter does not exist
	 * @see PreparedStatement#setTimestamp(int, java.sql.Timestamp)
	 */
	public void setLong(String name, Timestamp value) throws SQLException {
		List<Integer> indexes=getIndexes(name);
		for(Integer index: indexes) {
			statement.setTimestamp(index.intValue(), value);
		}
	}

	/**
	 * Sets value of type Date for the given parameter name.
	 * @param name  parameter name
	 * @param value parameter value
	 * @throws SQLException if an error occurred
	 * @throws IllegalArgumentException if the parameter does not exist
	 * @see PreparedStatement#setDate(int, java.sql.Date)
	 */
	public void setDate(String name, Date value) throws SQLException {
		List<Integer> indexes=getIndexes(name);
		for(Integer index: indexes) {
			statement.setDate(index.intValue(), value);
		}
	}

	/**
	 * Returns the wrapped prepared statement.
	 * @return prepared statement
	 */
	public PreparedStatement getStatement() {
		return statement;
	}

	/**
	 * Executes prepared statement.
	 * @return true if the first result is a {@link ResultSet}
	 * @throws SQLException if an error occurred
	 * @see PreparedStatement#execute()
	 */
	public boolean execute() throws SQLException {
		return statement.execute();
	}

	/**
	 * Executes prepared statement, which must be a query.
	 * @return the query results
	 * @throws SQLException if an error occurred
	 * @see PreparedStatement#executeQuery()
	 */
	public ResultSet executeQuery() throws SQLException {
		return statement.executeQuery();
	}

	/**
	 * Executes prepared statement, which must be an SQL INSERT, UPDATE or DELETE statement;
	 * or an SQL statement that returns nothing, such as a DDL statement.
	 * @return number of rows affected
	 * @throws SQLException if an error occurred
	 * @see PreparedStatement#executeUpdate()
	 */
	public int executeUpdate() throws SQLException {
		return statement.executeUpdate();
	}

	/**
	 * Closes prepared statement.
	 * @throws SQLException if an error occurred
	 * @see Statement#close()
	 */
	public void close() throws SQLException {
		statement.close();
	}
}
