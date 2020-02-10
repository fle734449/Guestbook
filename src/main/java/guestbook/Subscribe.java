package guestbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Subscribe extends HttpServlet {
	private static final Logger _logger = Logger.getLogger(Subscribe.class.getName());
	List<String> subscribers = new ArrayList<String>();
	String subEmail;
	
	private Subscribe(){}

	public Subscribe(List<String> subscribers, String subEmail) {
		this.subscribers = subscribers;
		this.subEmail = subEmail;
	}
	
	public List<String> getSubscribers() {
		return subscribers;
	}
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			UserService userService = UserServiceFactory.getUserService();
	        User user = userService.getCurrentUser();

	        String guestbookName = req.getParameter("guestbookName");
	        String email = user.getEmail();
	        
	        if(!(subscribers.contains(email))) {
	        	subscribers.add(email);
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
}