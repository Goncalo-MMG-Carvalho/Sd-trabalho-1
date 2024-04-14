package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Like {
	
	@Id
    String id;
    String user;
    String shortId;
    
    public Like( String id, String user, String shortId) {
    	this.id = id;
    	this.user = user;
    	this.shortId = shortId;
    }
    
    public String getId() {
    	return id;
    }
    
    public String getUser() {
    	return user;
    }
    
    public String getShortId() {
    	return shortId;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public void setShortId(String shortId) {
        this.shortId = shortId;
    }
    
}
