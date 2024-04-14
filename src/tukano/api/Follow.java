package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Follow {
    @Id
    String id;
    String follower;
    String followed;

    public Follow() {}

    public Follow(String id, String follower, String followed) {
        this.id = id;
    	this.follower = follower;
        this.followed = followed;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String followerId) {
        this.follower = followerId;
    }

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String followedId) {
        this.followed = followedId;
    }
    
    public String getId() {
    	return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
}
