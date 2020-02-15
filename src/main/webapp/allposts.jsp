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
		<h1 id = "logo-text">ThoughtBubbles - Look at all these thoughts!</h1>
		
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
			List<Entity> posts = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		    if (posts.isEmpty()) {
		        %>
		        <p>Guestbook '${fn:escapeXml(guestbookName)}' has no messages.</p>
		        <%
		
		    } else {
		    	
		        %>
		        <p>Messages in Guestbook '${fn:escapeXml(guestbookName)}'.</p>
		        <%
		
		        for (Entity post : posts) {
		        	pageContext.setAttribute("post_title", post.getProperty("title")); 
		            pageContext.setAttribute("post_content", post.getProperty("content"));      
	                pageContext.setAttribute("post_user", post.getProperty("user"));
	                pageContext.setAttribute("post_date", post.getProperty("date"));
	                
	                %>
	                <div class = "recent-posts">
	                	<div class = "post-title"><b>${fn:escapeXml(post_title)}</b></div>
		                <p> By: <b>${fn:escapeXml(post_user.nickname)}</b> </p>
		                <p> Posted on: <b>${fn:escapeXml(post_date)}</b> </p>
			            <textarea id = "post-text">${fn:escapeXml(post_content)}</textarea>
			            <br>
		            </div>
		            <%
		            
		        }
		    }
		%> 
		<div class = "return-home">
				<a href="guestbook.jsp"><button class="button"> Return to Homepage</button></a>	
		</div>
			
  </body>
</html>