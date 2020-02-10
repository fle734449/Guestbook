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


import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Subscribe extends HttpServlet {
	private static final Logger _logger = Logger.getLogger(Subscribe.class.getName());
	List<String> subscribers = new ArrayList<String>();
	String subEmail;
	
	public Subscribe(){}

	public Subscribe(List<String> subscribers, String subEmail) {
		this.subscribers = subscribers;
		this.subEmail = subEmail;
	}
	
	public List<String> getSubscribers() {
		return subscribers;
	}
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			_logger.info("Cron Job has been executed");
			UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser();

	        String guestbookName = req.getParameter("guestbookName");
	        String email = user.getEmail();
	        if(email != null) {
	        	if(!(subscribers.contains(email))) {
	        		subscribers.add(email);
	        	}
	        }
	        
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		    Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
		    
	        Query query = new Query("Greeting", guestbookKey).addSort("date", Query.SortDirection.DESCENDING);
			List<Entity> posts = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
			
			Collections.reverse(posts);
			
			String blogPosts = "";
			for(Entity post: posts) {
				if(inLastDay((Date) post.getProperty("date"))) {
					blogPosts +=  post.getProperty("title") + "\n" + post.getProperty("user") + "\n" + post.getProperty("date") + "\n" + post.getProperty("content")  + "\n" + "\n";
				}
			}
			
			if(!(blogPosts.equals(""))) {
				for (String sub: subscribers) {
					_logger.info("Email has been sent to" + sub);
					Properties p = new Properties();
				    Session session = Session.getDefaultInstance(p, null);
				    MimeMessage msg = new MimeMessage(session);
				    msg.setFrom(new InternetAddress("thoughtbubbles@thought-bubbles.appspotmail.com"));
				    msg.addRecipient(Message.RecipientType.TO,new InternetAddress(sub));
				    msg.setSubject("You have subscribed to ThoughtBubbles");
				    msg.setText("Dear Subscriber, \nHere are the blog posts made in the last 24 hours:\n\n" + blogPosts);
				    Transport.send(msg);
				}
			}
			
	        /*
	        Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);

	        Entity subscriber = new Entity("Greeting", guestbookKey);
	        subscriber.setProperty("email", email);

	 		

	        DatastoreService subList = DatastoreServiceFactory.getDatastoreService();

	        subList.put(subscriber);
			*/

	        resp.sendRedirect("/guestbook.jsp?guestbookName=" + guestbookName);
		} catch (Exception ex) {
			//Log any exceptions in your Cron Job
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
	public boolean inLastDay(Date date) {
		return date.getTime() > System.currentTimeMillis() - MILLIS_PER_DAY;
	}
	
}