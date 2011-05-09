<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="cloudapp.UserEntity" %>
<%@ page import="com.googlecode.objectify.*" %>

<html>
  <head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
  </head>
  <body>

<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
        Objectify ofy = ObjectifyService.begin();
    	UserEntity userEntity = ofy.find(UserEntity.class, user.getNickname());
    	if (userEntity == null) {
%>
<p>User not found in the database. You must
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a>
as an administrator.</p>
<%
    		return;
    	}
    	if (userEntity.getType() != UserEntity.Type.ADMIN) {
%>
<p>This is a normal account. You must
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a>
as an administrator.</p>
<%
    		return;
    	}
%>
<p>Hello, <%= user.getNickname() %>! (You can
<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>

	<form action="/rootcert" method="post">
	  <div><textarea name="content" rows="3" cols="60"></textarea></div>
	  <div><input type="submit" value="Upload certificate" /></div>
	</form>
	
<%
    } else {
%>
<p>You must
<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a>
as an administrator.</p>
<%
    }
%>

  </body>
</html>