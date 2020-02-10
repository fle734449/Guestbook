package guestbook;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;

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

public class SendEmail extends HttpServlet {
	private static final Logger _logger = Logger.getLogger(SendEmail.class.getName());
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_logger.info("Cron Job has been executed");
		ObjectifyService.register(Subscribe.class);
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String guestbookName = req.getParameter("guestbookName");
        Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Subscribe> s = ObjectifyService.ofy().load().type(Subscribe.class).list();
	    List<String> subscribers = s.get(0).getSubscribers();
	    
        Query query = new Query("Greeting", guestbookKey).addSort("date", Query.SortDirection.DESCENDING);
		List<Entity> posts = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
		
		Collections.reverse(posts);
		
		String blogPosts = "";
		
		for(Entity post: posts) {
			if(inLastDay((Date) post.getProperty("date"))) {
				blogPosts +=  post.getProperty("title") + "\n" + post.getProperty("user") + "\n" + post.getProperty("date") + "\n" + post.getProperty("content")  + "\n" + "\n";
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
		
		
		
        /*
        Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);

        Entity subscriber = new Entity("Greeting", guestbookKey);
        subscriber.setProperty("email", email);

 		

        DatastoreService subList = DatastoreServiceFactory.getDatastoreService();

        subList.put(subscriber);
		*/
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