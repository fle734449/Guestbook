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
		<h1 id = "logo-text">ThoughtBubbles - Come share your thoughts here!</h1>
		
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
		    List<Entity> posts = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
		
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
	                	<b>${fn:escapeXml(post_title)}</b>
	                	<br>
		                <p> By: <b>${fn:escapeXml(post_user.nickname)}</b> </p>
		                <p> Posted on: <b>${fn:escapeXml(post_date)}</b> </p>
			            <blockquote>${fn:escapeXml(post_content)}</blockquote>
			            <br>
		            </div>
		            <%
		            
		        }
		    }
		%> 
		
			
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
			
			<div class = "submit-posts">
		    	<form action="/sign" method="post" id="textbox">
				      <input type="hidden" name="guestbookName" value="${fn:escapeXml(guestbookName)}"/>
			    </form>
			    
				<form action="newpost.jsp">
	    			<input type="submit" value="Create New Post" />
				</form>
		    </div>
		<% 
			} 
		%>		
  </body>
</html>