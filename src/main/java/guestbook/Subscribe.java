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
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();    
		String email = user.getEmail();
	        if(email != null) {
	        	if(!(subscribers.contains(email))) {
	        		subscribers.add(email);
	        	}
	        }
	        
	        resp.sendRedirect("/guestbook.jsp");
	        
	        

	        
	}
}