package guestbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;


public class Subscribe extends HttpServlet {
	@Id long id;
	private static final Logger _logger = Logger.getLogger(Subscribe.class.getName());
	@Index List<String> subscribers = new ArrayList<String>();
	@Index String subEmail;
	
	public Subscribe(){}

	public Subscribe(List<String> subscribers, String subEmail) {
		this.subscribers = subscribers;
		this.subEmail = subEmail;
	}
	
	public List<String> getSubscribers() {
		return subscribers;
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();    
		String email = user.getEmail();
	        if(email != null) {
	        	if(!(subscribers.contains(email))) {
	        		subscribers.add(email);
	        	}
	        }
	        String guestbookName = req.getParameter("guestbookName");
	        Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    
	        Query query = new Query("Greeting", guestbookKey).addSort("date", Query.SortDirection.DESCENDING);
			List<Entity> posts = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
			
			Collections.reverse(posts);
			
			String blogPosts = "";
			
			for(Entity post: posts) {
				if(inLastDay((Date) post.getProperty("date"))) {
					blogPosts +=  post.getProperty("title") + "\nBy:" + post.getProperty("user") + "\nPosted on:" + post.getProperty("date") + "\n" + post.getProperty("content")  + "\n" + "\n";
				}
			}
			try {
				if(!(blogPosts.equals(""))) {
					for (String sub: subscribers) {
						_logger.info("Email has been sent to" + sub);
						Properties p = new Properties();
					    Session session = Session.getDefaultInstance(p, null);
					    MimeMessage msg = new MimeMessage(session);
					    msg.setFrom(new InternetAddress("fle734449@gmail.com"));
					    msg.addRecipient(Message.RecipientType.TO,new InternetAddress(sub));
					    msg.setSubject("You have subscribed to ThoughtBubbles");
					    msg.setText("Dear Subscriber, \nHere are the blog posts made in the last 24 hours:\n\n" + blogPosts);
					    Transport.send(msg);
					}
				}
			} catch (Exception e) {
				
	        
			}
			resp.sendRedirect("/guestbook.jsp");
			
	
		}
	public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
	public boolean inLastDay(Date date) {
		return date.getTime() > System.currentTimeMillis() - MILLIS_PER_DAY;
	}
}