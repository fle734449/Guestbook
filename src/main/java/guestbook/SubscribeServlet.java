package guestbook;


import static com.googlecode.objectify.ObjectifyService.ofy;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SubscribeServlet extends HttpServlet {
	private static final Logger _logger = Logger.getLogger(SubscribeServlet.class.getName());
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
    	throws IOException {
    	UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        ObjectifyService.register(Subscriber.class);
        _logger.info(user.getEmail() + " subscribed!");
        Subscriber sub = new Subscriber(user.getEmail());
        
        List<Subscriber> subscribers = ObjectifyService.ofy().load().type(Subscriber.class).list();
        boolean alreadySubscribed = false;
        for(Subscriber s: subscribers) {
        	if(s.getEmail().equals(sub.getEmail())) {
        		alreadySubscribed = true;
        	}
        }
        if(!alreadySubscribed) {
        	ofy().save().entity(sub).now();
        }
        
        resp.sendRedirect("/guestbook.jsp");
    }
}