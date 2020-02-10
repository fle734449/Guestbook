package guestbook;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class BlogPost implements Comparable<BlogPost>{
	@Id long id;
	@Index String title;
	@Index String content;
	@Index User user;
	@Index Date date;
	
	private BlogPost() {}

	public BlogPost(String title, String content, User user) {
		this.title = title;
		this.content = content;
		this.user = user;
		this.date = new Date();
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
		return dateFormat.format(date);
	}
	
	@Override
	public int compareTo(BlogPost post) {
		if (date.after(post.date)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public String toString() {
		return title + "\n" + user + "\n" + this.getDate() + "\n" + content + "\n";
	}
	
	
}
			