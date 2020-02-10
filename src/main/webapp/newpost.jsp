<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="java.util.List" %>

<%@ page import="com.google.appengine.api.users.User" %>

<%@ page import="com.google.appengine.api.users.UserService" %>

<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>

<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>

<%@ page import="com.google.appengine.api.datastore.Query" %>

<%@ page import="com.google.appengine.api.datastore.Entity" %>

<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>

<%@ page import="com.google.appengine.api.datastore.Key" %>

<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

  

<html>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />

  <head>
 
  </head>
  
  
  <body>
 	<div class = "blog-bar">
 		<img src="ThoughtBubble.png" id = "logo-image" >
		<h1 id = "logo-text">ThoughtBubbles - Creating new thoughts...</h1>
		
		<div class = "user-function">
			<%
			    String guestbookName = request.getParameter("guestbookName");
			
			    if (guestbookName == null) {
			        guestbookName = "default";
			    }
			
			    pageContext.setAttribute("guestbookName", guestbookName);
			    UserService userService = UserServiceFactory.getUserService();
			    User user = userService.getCurrentUser();
			
			    if (user != null) {
			      pageContext.setAttribute("user", user);
			
			%>
			
			<p>Hello, ${fn:escapeXml(user.nickname)}! (You can
			<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
			
			<%
			    } else {
			%>
			
			
			<p>
				<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
			</p>
			
			<%
			    }
			%>
		</div>
		
	</div>
			<div class = "submit-posts">
		    	<form action="/sign" method="post" id="textbox">
		    		<div><textarea id = "title-textarea"name="title" rows="1" cols="50" >Title of Thought...</textarea></div>
				    <div><textarea id = "content-textarea" name="content" rows="10" cols="100" >Think away!</textarea></div>
				      <div><input type="submit" value="Submit Post" /></div>
				      <input type="hidden" name="guestbookName" value="${fn:escapeXml(guestbookName)}"/>
			    </form>
		    </div>
		<%
		    if (user == null) {
		
		%>
			<script type="text/javascript"> 
		 		document.getElementById("textbox").style.display = "none";
			</script>
		<% } else { %> 
			<script type="text/javascript"> 
		 		document.getElementById("textbox").style.display = "block";
			</script>
		<% 
			} 
		%>		

	
  </body>
</html>