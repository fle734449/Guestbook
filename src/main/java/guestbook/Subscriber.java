package guestbook;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Subscriber {
	@Id long id;
	@Index String subEmail;
	
	public Subscriber(){}

	public Subscriber(String subEmail) {
		this.subEmail = subEmail;
	}
	
	public String getEmail() {
		return subEmail;
	}


	
}