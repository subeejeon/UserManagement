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
	
	private String jdbcURL = "jdbc:mysql://localhost:3306/userdb";
	private String jdbcUsername = "root";
	private String jdbcPassword = "12345678";
	private String jdbcDriver = "com.mysql.cj.jdbc.Driver";

	private static final String INSERT_USERS_SQL = 
			"INSERT INTO users" + " (name, email, country) VALUES "
	+ "(?,?,?);";
	private static final String SELECT_USERS_BY_ID =
			"SELECT id,name,email, country from users where id=?";	
	private static final String DELETE_USERS_SQL = 
			"DELETE from users where id =?;";
	private static final String UPDATE_USERS_SQL = 
			"UPDATE users set name = ?, email= ?, country =? where id =?;";
	
	
	public UserDao() {
	}
	
	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		} catch (SQLException e ){
			System.out.println(e.getMessage());
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
			preparedStatement.setString(3, user.getCountry());
			preparedStatement.executeUpdate();	
			
			System.out.println(preparedStatement);
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
//		System.out.println(SELECT_USERS_BY_ID);
		try(Connection connection = getConnection();
			PreparedStatement preparedstatement = connection.prepareStatement(SELECT_USERS_BY_ID)) {
			preparedstatement.setInt(1, id);	
		ResultSet rs = preparedstatement.executeQuery();
		System.out.println(preparedstatement);
		
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
		
		// Using try-with-resources to avoid closing resources 
		List<User> userList = new ArrayList<>();
		
		try(Connection connection = getConnection();
				PreparedStatement preparedStatement = 
				connection.prepareStatement(SELECT_USERS_BY_ID);){
				ResultSet rs = preparedStatement.executeQuery();
				
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
			pst.setInt(1, user.getId());
			pst.setString(2, user.getName());
			pst.setString(3, user.getEmail());
			pst.setString(4, user.getCountry());
			
			rowUpdated = pst.executeUpdate() > 0 ;
			
			System.out.println("updated User:" + pst);
			System.out.println(String.format("Row updated %d", rowUpdated));
		} return rowUpdated;
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