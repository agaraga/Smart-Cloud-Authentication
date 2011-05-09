<%@ page language="java" contentType="text/html"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ page import="cloudapp.UserEntity" %>
<%@ page import="com.googlecode.objectify.*" %>

<html>
  <head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
  </head>
  <body>
  	<p>Administrators:
<%
Objectify ofy = ObjectifyService.begin();
Query<UserEntity> q = ofy.query(UserEntity.class).filter("type", UserEntity.Type.ADMIN);

for (UserEntity userEntity: q) {
%>
	<%= userEntity.getName() %>	
<%
}
%>
	</p>
	<form action="/userlist" method="post">
Create admin: <input type="text" name="user" />
<input type="submit" value="Create" />
	</form>

  </body>
</html>