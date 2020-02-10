package guestbook;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 

public class SignGuestbookServlet extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        // We have one entity group per Guestbook with all Greetings residing
        // in the same entity group as the Guestbook to which they belong.
        // This lets us run a transactional ancestor query to retrieve all
        // Greetings for a given Guestbook.  However, the write rate to each
        // Guestbook should be limited to ~1/second.
        String guestbookName = req.getParameter("guestbookName");
        String title = req.getParameter("title");
        
        if (title == null || title == "") {
        	title = "Untitled Post";
        }
        
        String content = req.getParameter("content");
        
        Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
        Date date = new Date();
        Entity post = new Entity("Greeting", guestbookKey);
        post.setProperty("title", title);
        post.setProperty("user", user);
        post.setProperty("date", date);
        post.setProperty("content", content);
 

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        datastore.put(post);

 

        resp.sendRedirect("/guestbook.jsp?guestbookName=" + guestbookName);

    }

}
			/*
			if (content == null) {
				content = "(No greeting)";
			}
			if (user != null) {
				log.info("Greeting posted by user " + user.getNickname() + ": " + content);
			} else {
				log.info("Greeting posted anonymously: " + content);
			}
			resp.sendRedirect("/guestbook.jsp");
			*/