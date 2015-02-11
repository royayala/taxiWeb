package com.sinapsitech.taxiWeb.acciones;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "logoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  protected void doGet(HttpServletRequest request,
    HttpServletResponse response) 
      throws ServletException, IOException {
    
    // Invalidate current HTTP session.
    // Will call JAAS LoginModule logout() method
	  response.setHeader("Cache-Control", "no-cache, no-store");  
      response.setHeader("Pragma", "no-cache");  
    request.getSession().invalidate();
    request.logout();//JAAS log out! do NOT work? (servlet specification)  
    //response.sendRedirect(request.getContextPath() + "/login.jsp");  
    
    // Redirect the user to the secure web page.
    // Since the user is now logged out the
    // authentication form will be shown
    response.sendRedirect(request.getContextPath() 
      + "/index.html");
    
  }

}
