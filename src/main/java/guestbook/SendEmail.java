package guestbook;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.servlet.annotation.WebFilter;
import com.googlecode.objectify.ObjectifyFilter;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.googlecode.objectify.Objectify;
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
	
	
	

/*
	@WebFilter(urlPatterns = {"/*"})
	public class ObjectifyWebFilter extends ObjectifyFilter {}
	*/
	
}