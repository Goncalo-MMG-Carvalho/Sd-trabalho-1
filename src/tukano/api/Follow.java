package tukano.api;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Follow {
    @Id
    String followerId;
    String followedId;

    public Follow() {}

    public Follow(String followerId, String followedId) {
        this.followerId = followerId;
        this.followedId = followedId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowedId() {
        return followedId;
    }

    public void setFollowedId(String followedId) {
        this.followedId = followedId;
    }
}
