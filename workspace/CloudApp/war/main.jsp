<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="cloudapp.UserEntity" %>
<%@ page import="cloudapp.AppConfig" %>
<%@ page import="com.googlecode.objectify.*" %>

<html>
  <head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
  </head>
  <body>
	<h3>Welcome to the Smart Cloud Authentication project server</h3>
<%
	boolean knownUser = false;
	boolean admin = false;
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
        Objectify ofy = ObjectifyService.begin();
    	UserEntity userEntity = ofy.find(UserEntity.class, user.getNickname());
    	if (userEntity != null) {
    		knownUser = true;   		   	
	    	if (userEntity.getType() == UserEntity.Type.ADMIN) {
	    		admin = true;
	    	}
    	}
    }
    if (knownUser) {
%>
<p>Hello, <%= user.getNickname() %>! (You can
<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%    	
    } else {
%>
<p>
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a>
</p>
<% 
	}
%>
    <table>
      <tr>
        <td colspan="2" style="font-weight:bold;">Available actions:</td>        
      </tr>
      <tr>
        <td><a href="usercert.jsp">Upload user certificate</a></td>
      </tr> 
      <tr>
        <td><a href="getappcert">Get application certificate</a></td>
      </tr>      
<%
	if (admin) {
%>
   	
     <tr>
        <td><a href="admin/userlist.jsp">View/Create users</a></td>
      </tr>
      <tr>
        <td><a href="admin/rootcert.jsp">Upload root certificates</a></td>

      </tr>
      <tr>
        <td><a href="admin/appcert.jsp">Upload application certificate</a></td>
      </tr> 
<%
    }
%>
    </table>

  </body>
</html>