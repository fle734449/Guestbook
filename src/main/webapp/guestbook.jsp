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
		<h1 id = "logo-text">ThoughtBubbles</h1>
		<p id = "slogan">Share your thoughts here!</p>
		
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

	

		<%
		    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
		
		    // Run an ancestor query to ensure we see the most up-to-date
		
		    // view of the Greetings belonging to the selected Guestbook.
		
			Query query = new Query("Greeting", guestbookKey).addSort("date", Query.SortDirection.DESCENDING);
		    List<Entity> greetings = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
		
		    if (greetings.isEmpty()) {
		        %>
		        <p>Guestbook '${fn:escapeXml(guestbookName)}' has no messages.</p>
		        <%
		
		    } else {
		    	
		        %>
		        <p>Messages in Guestbook '${fn:escapeXml(guestbookName)}'.</p>
		        <%
		
		        for (Entity greeting : greetings) {
		            pageContext.setAttribute("greeting_content", greeting.getProperty("content"));      
	                pageContext.setAttribute("greeting_user", greeting.getProperty("user"));
	                
	                %>
	                <div class = "recent-posts">
		                <p><b>${fn:escapeXml(greeting_user.nickname)}</b> wrote:</p>
			            <blockquote>${fn:escapeXml(greeting_content)}</blockquote>
		            </div>
		            <%
		            
		        }
		    }
		%> 
			<div class = "submit-posts">
		    	<form action="/sign" method="post" id="textbox">
				    <div><textarea name="content" id = "textbox" rows="3" cols="60" ></textarea></div>
				      <div><input type="submit" value="Post Greeting" /></div>
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