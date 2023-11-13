package com.xadmin.usermanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.xadmin.usermanagement.baen.User;

public class UserDao {
	
	private String jdbcURL = "jdbc:mysql://localhost:3306/mydb";
	private String jdbcUsername = "root";
	private String jdbcPassword = "12345678";
	private String jdbcDriver = "com.mysql.cj.jdbc.Driver";

	private static final String INSERT_USERS_SQL = 
			"INSERT INTO users" + " (name, email, country) VALUES "
	+ "(?,?,?);";
	private static final String SELECT_USERS_BY_ID =
			"select id,name,email.country from users where id=?";	
	private static final String DELETE_USERS_SQL = 
			"delete from users where id =?;";
	private static final String UPDATE_USERS_SQL = 
			"update users set name = ?, email= ?, country =? where id =?;";
	
	
	public UserDao() {
	}
	
	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		} catch (SQLException e ){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return connection;
	} 
	
	//[Insert user]
	public void insertUser(User user) throws SQLException {
		System.out.println(INSERT_USERS_SQL);
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = 
						connection.prepareStatement(INSERT_USERS_SQL)) {
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getEmail());
			preparedStatement.setString(1, user.getCountry());
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
		} catch(SQLException e){
			printSQLException(e);
		}						
	}
	private void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if ( e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException)e).getSQLState());
				System.err.println("Error Code: " + ((SQLException)e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while(t!= null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}
	
	//[Select user by id]
	public User selectUser(int id) {
		User user = null;
		//Step 1: Establishing a connection
		System.out.println(SELECT_USERS_BY_ID);
		//Step 2: Create a statement using connection object
		try(Connection connection = getConnection();
				PreparedStatement preparedstatement =
						connection.prepareStatement(SELECT_USERS_BY_ID)) {
			preparedstatement.setInt(1, id);
			System.out.println(preparedstatement);
		//Step 3: Execute the query or update query
		ResultSet rs = preparedstatement.executeQuery();
		//Step 4: Process the ResultSet Object
		//Condition check using hasNext() method which holds true till 
		//there is single element remaining in List
		while (rs.next()){
			String name = rs.getString("name");
			String email = rs.getString("email");
			String country = rs.getString("country");
			user = new User(id, name, email, country);
			}			
		//Catching database exceptions if any
		} catch(SQLException e) {
			//Print the exception
			printSQLException(e);
		}
		return user;
	}
	
	//[Select all users]
	public List<User> selectAllUsers() {
		
		User users = null;	
		// Using try-with-resources to avoid closing resources 
		List<User> userList = new ArrayList<>();
		
		//Step 1. Establishing a Connection
		try(Connection connection = getConnection();
			
				//Step 2. Create a statement using connection object
				PreparedStatement preparedStatement = 
				connection.prepareStatement(SELECT_USERS_BY_ID);){
			System.out.println(preparedStatement);
			//Step 3. Execute the query or update query
			ResultSet rs = preparedStatement.executeQuery();
			
			//Step 4. Process the ResultSet object
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				userList.add(new User (id,name,email,country));
				}	 
			} catch (SQLException e) {
				printSQLException(e);
		}
		return userList;
	}
	
	//[Update user]
	public boolean updateUser(User user) throws SQLException {
		boolean rowUpdated;
		try(Connection con = getConnection();
				PreparedStatement pst = con.prepareStatement(UPDATE_USERS_SQL)){
			System.out.println("updated User:" + pst);
			pst.setInt(1, user.getId());
			pst.setString(2, user.getName());
			pst.setString(3, user.getEmail());
			pst.setString(4, user.getCountry());
			
			rowUpdated = pst.executeUpdate() > 0 ;
			System.out.println(String.format("Row updated %d", rowUpdated));
		} return rowUpdated;
	}
	private void printSQLExeption(SQLException ex) {
		for (Throwable e:ex) {
			if(e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException)e).getSQLState());
				System.err.println("Error Code: " + ((SQLException)e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while(t!=null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}
	
	//[Delete Users]
	public boolean deleteUser(User user) throws SQLException {
		boolean rowDeleted;
		
		
		try(Connection con = getConnection();
				PreparedStatement pst = con.prepareStatement(DELETE_USERS_SQL);){
			pst.setInt(1, user.getId());
			rowDeleted = pst.executeUpdate() > 0;		
		}
		return rowDeleted;
	}
}