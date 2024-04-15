package tukano.servers.rest;

import java.util.List;

import tukano.api.Short;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;
import tukano.servers.java.JavaShorts;
import jakarta.inject.Singleton;

@Singleton
public class RestShortsResource extends BigBoyRest implements RestShorts {

	final Shorts impl;
	
	public RestShortsResource() {
		this.impl = new JavaShorts();
	}

    @Override
    public Short createShort(String userId, String password) {
        return super.fromJavaResult(impl.createShort(userId, password));
    }

    @Override
    public void deleteShort(String shortId, String password) {
        super.fromJavaResult(impl.deleteShort(shortId, password));
    }

    @Override
    public Short getShort(String shortId) {
        return super.fromJavaResult(impl.getShort(shortId));
    }

    @Override
    public List<String> getShorts(String userId) {
        return super.fromJavaResult(impl.getShorts(userId));
    }

    @Override
    public void follow(String userId1, String userId2, boolean isFollowing, String password) {
        super.fromJavaResult(impl.follow(userId1, userId2, isFollowing, password));
    }

    @Override
    public List<String> followers(String userId, String password) {
        return super.fromJavaResult(impl.followers(userId, password));
    }

    @Override
    public void like(String shortId, String userId, boolean isLiked, String password) {
        super.fromJavaResult(impl.like(shortId, userId, isLiked, password));
    }

    @Override
    public List<String> likes(String shortId, String password) {
        return super.fromJavaResult(impl.likes(shortId, password));
    }

    @Override
    public List<String> getFeed(String userId, String password) {
        return super.fromJavaResult(impl.getFeed(userId, password));
    }

    @Override
    public boolean verifyBlobURI(String blobId) {
        return super.fromJavaResult(impl.verifyBlobURI(blobId));
    }
}