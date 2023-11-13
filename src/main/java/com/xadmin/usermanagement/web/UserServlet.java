package com.xadmin.usermanagement.web;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xadmin.usermanagement.baen.User;
import com.xadmin.usermanagement.dao.UserDao;

@WebServlet("/")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDao userDAO;
	
	public void init() {
		userDAO = new UserDao();
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse rep) 
		throws ServletException, IOException {	
		doGet(req,rep);
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse rep) 
		throws ServletException, IOException {	
		String action = req.getServletPath();
	try {	
		switch(action) {
		case "/new":
			showNewForm(req,rep);
			break;
		case "/insert":
			insertUser(req,rep);
			break;
		case "/delete":
			deleteUser(req,rep);
			break;
		case "/edit":
			showEditForm(req,rep);
			break;
		case "/update":
			updateUser(req,rep);
			break;
		default:
			userList(req,rep);
			break;
		}
	}
		catch (SQLException ex) {
			throw new ServletException(ex);
		}
	}	
	
	private void userList(HttpServletRequest req, HttpServletResponse rep) 
			throws SQLException, ServletException, IOException  {
			List<User> userList = userDAO.selectAllUsers();
			req.setAttribute("userList", userList);
			RequestDispatcher dispatcher = req.getRequestDispatcher("user-list.jsp");
			dispatcher.forward(req, rep);
	}
	
	private void showNewForm(HttpServletRequest req, HttpServletResponse rep)
		throws ServletException, IOException{
		RequestDispatcher dispatcher = req.getRequestDispatcher("user-form.jsp");
		dispatcher.forward(req, rep);	
	}
	
	private void showEditForm(HttpServletRequest req, HttpServletResponse rep)
	throws SQLException, ServletException, IOException{
		int id = Integer.parseInt(req.getParameter("id"));
		User existingUser = userDAO.selectUser(id);
		RequestDispatcher dispatcher = req.getRequestDispatcher("user-form.jsp");
		req.setAttribute("user",existingUser);
		dispatcher.forward(req, rep);
	}
	
	private void insertUser(HttpServletRequest req, HttpServletResponse rep)
	throws SQLException, ServletException, IOException{
		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String country = req.getParameter("country");
		
		User user = new User(name,email,country);
		userDAO.insertUser(user);
		rep.sendRedirect("List");	
	}
	private void updateUser(HttpServletRequest req, HttpServletResponse rep)
			throws SQLException, ServletException, IOException {
		
		int id = Integer.parseInt(req.getParameter("id"));
		String name = req.getParameter("name");
		String email = req.getParameter("email");
		String country = req.getParameter("country");
		
		User user = new User(name,email,country);
		userDAO.updateUser(user);
		rep.sendRedirect("List");
	}
	
	private void deleteUser(HttpServletRequest req, HttpServletResponse rep)
	throws SQLException, ServletException, IOException {
		
		int id = Integer.parseInt(req.getParameter("id"));
		
		User user = new User(id);		
		userDAO.deleteUser(user);
		rep.sendRedirect("List");
	}
	
}
