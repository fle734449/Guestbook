package guestbook;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
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
	/*@Id long id;
	
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
	*/
	//Frank Driving Now
	private static final Logger _logger = Logger.getLogger(Subscribe.class.getName());
	@SuppressWarnings("serial")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_logger.info("Cron Job executed!");

	        String guestbookName = req.getParameter("guestbookName");
	        if(guestbookName == null) {
	        	guestbookName = "default";
	        }
	       
	        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	        Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
		    
	        Query query = new Query("Greeting", guestbookKey).addSort("date", Query.SortDirection.DESCENDING);
			List<Entity> posts = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
			
			Collections.reverse(posts);
			
			String blogPosts = "";
			DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
			dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
			
			for(Entity post: posts) {
				if(inLastDay((Date) post.getProperty("date"))) {
					String d = dateFormat.format(post.getProperty("date"));
					blogPosts +=  post.getProperty("title") + "\nBy: " + post.getProperty("user") + "\nPosted on: " + d + "\n" + post.getProperty("content")  + "\n" + "\n";
				}
			}
			String subs = "subscribers";
			Key subKey = KeyFactory.createKey("SubList", subs);
			Query subQuery = new Query("List", subKey).addSort("email", Query.SortDirection.DESCENDING);
			List<Entity> subscriberList = datastore.prepare(subQuery).asList(FetchOptions.Builder.withDefaults());
			
			try {
				if(!(blogPosts.equals(""))) {
					for (Entity sub: subscriberList) {
						String email = (String) sub.getProperty("email");
						_logger.info("Email has been sent to" + email);
						Properties p = new Properties();
					    Session session = Session.getDefaultInstance(p, null);
					    MimeMessage msg = new MimeMessage(session);
					    msg.setFrom(new InternetAddress("thoughtbubbles@thoughtbubbles-blog.appspotmail.com"));
					    msg.addRecipient(Message.RecipientType.TO,new InternetAddress(email));
					    msg.setSubject("You have subscribed to ThoughtBubbles");
					    msg.setText("Dear Subscriber, \nHere are the blog posts made in the last 24 hours:\n\n" + blogPosts);
					    Transport.send(msg);
					}
				}
				
			} catch (Exception e) {
				
	        
			}
			
			
	
		}
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        boolean subscribed = false;
        String subStatus = req.getParameter("subStatus");
        String subs = "subscribers";
		String email = user.getEmail();
		Key subKey = KeyFactory.createKey("SubList", subs);
		Entity subscriber = new Entity("List", subKey);
		subscriber.setProperty("email", email);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Query subQuery = new Query("List", subKey).addSort("email", Query.SortDirection.DESCENDING);
		List<Entity> subscriberList = datastore.prepare(subQuery).asList(FetchOptions.Builder.withDefaults());
		if(subStatus.equals("sub")) {
			for (Entity sub: subscriberList) {
				if(sub.getProperty("email").equals(email)) {
					subscribed = true;
				}
			}
			if(!subscribed) {
				datastore.put(subscriber);
			}
		} else {
			for (Entity sub: subscriberList) {
				if(sub.getProperty("email").equals(email)) {
					datastore.delete(sub.getKey());
				}
			}
		}
        /*
	        if(email != null) {
	        	if(!(subscribers.contains(email))) {
	        		subscribers.add(email);
	        	}
	        }
	    */
	    resp.sendRedirect("/guestbook.jsp");
	}
	
	public final static long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
	public boolean inLastDay(Date date) {
		return date.getTime() > System.currentTimeMillis() - MILLIS_PER_DAY;
	}
}
//End of Frank Driving