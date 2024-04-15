package tukano.clients.rest;

import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.rest.RestShorts;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.GenericType;

public class RestShortsClient extends RestClient implements Shorts {

	final WebTarget target;
	
    public RestShortsClient(URI serverURI) {
        super(serverURI);
        target = client.target(serverURI).path(RestShorts.PATH);
    }


    private Result<Short> priv_createShort(String userId, String pwd) { // TODO verificar se null funciona
    	return super.toJavaResult( 
    		target	.path(userId)
    				.queryParam(RestShorts.PWD, pwd)
    				.request().accept(MediaType.APPLICATION_JSON)
    				.post(null) , Short.class ); //TODO
    }
    
    private Result<Void> priv_deleteShort(String shortId, String pwd) { 
    	return super.toJavaResult( 
    		target	.path(shortId)
    				.queryParam(RestShorts.PWD, pwd)
    				.request()
    				.delete() , Void.class );
    }
    
    private Result<Short> priv_getShort(String shortId) {
    	return super.toJavaResult( 
    		target	.path(shortId)
    				.request().accept(MediaType.APPLICATION_JSON)
    				.get() , Short.class );
    }
    
    private Result<List<String>> priv_getShorts (String userId) {
    	return super.toJavaResult( 
    		target	.path(userId + RestShorts.SHORTS)
    				.request().accept(MediaType.APPLICATION_JSON)
    				.get() , new GenericType<List<String>>() {} );
    }
    
    private Result<Void> priv_follow (String userId1, String userId2, 
    								   boolean isFollowing, String pwd) {
    	
    	return super.toJavaResult( 
    		target	.path(userId1 + "/" +  userId2 + RestShorts.FOLLOWERS)
    				.queryParam(RestShorts.PWD, pwd)
    				.request()
    				.post(Entity.entity(isFollowing, MediaType.APPLICATION_JSON)), Void.class );
    }
    
    private Result<List<String>> priv_followers (String userId, String pwd) {

		return super.toJavaResult( 
			target	.path(userId + RestShorts.FOLLOWERS)
					.queryParam(RestShorts.PWD, pwd)
					.request().accept(MediaType.APPLICATION_JSON)
					.get(), new GenericType<List<String>>() {} );
	}
    
    private Result< Void > priv_like (String shortId, String userId, boolean isLiked, String pwd) {
    	return super.toJavaResult( 
    		target	.path(shortId + "/" + userId + RestShorts.LIKES)
    				.queryParam(RestShorts.PWD, pwd)
    				.request()
    				.post(Entity.entity(isLiked, MediaType.APPLICATION_JSON)) , Void.class );
    }
    
    private Result<List<String>> priv_likes (String shortId, String pwd) {
    	return super.toJavaResult( 
    		target	.path(shortId + RestShorts.LIKES)
    				.queryParam(RestShorts.PWD, pwd)
    				.request().accept(MediaType.APPLICATION_JSON)
    				.get() , new GenericType<List<String>>() {} );
    }
    
    private Result<List<String>> priv_getFeed (String userId, String pwd) {
    	return super.toJavaResult( 
    		target	.path(userId + RestShorts.FEED)
    				.queryParam(RestShorts.PWD, pwd)
    				.request().accept(MediaType.APPLICATION_JSON)
    				.get() , new GenericType<List<String>>() {} );
    }
    
    //fazer igual ao Rest Users Client
    @Override
    public Result<Short> createShort(String userId, String password) {
    	return super.reTry( () -> priv_createShort(userId, password));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'createShort'");
    }


    @Override
    public Result<Void> deleteShort(String shortId, String password) {
    	return super.reTry( () -> priv_deleteShort(shortId, password));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'deleteShort'");
    }


    @Override
    public Result<Short> getShort(String shortId) {
    	return super.reTry( () -> priv_getShort(shortId));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'getShort'");
    }


    @Override
    public Result<List<String>> getShorts(String userId) {
    	return super.reTry( () -> priv_getShorts(userId));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'getShorts'");
    }


    @Override
    public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
    	return super.reTry( () -> priv_follow(userId1, userId2, isFollowing, password));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'follow'");
    }


    @Override
    public Result<List<String>> followers(String userId, String password) {
    	return super.reTry( () -> priv_followers(userId, password));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'followers'");
    }


    @Override
    public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
    	return super.reTry( () -> priv_like(shortId, userId, isLiked, password));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'like'");
    }


    @Override
    public Result<List<String>> likes(String shortId, String password) {
    	return super.reTry( () -> priv_likes(shortId, password));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'likes'");
    }


    @Override
    public Result<List<String>> getFeed(String userId, String password) {
    	return super.reTry( () -> priv_getFeed(userId, password));
    	
        //throw new UnsupportedOperationException("Unimplemented method 'getFeed'");
    }

    
    
    /* ------------- Apartir de aqui é como Angola, é nosso --------------------*/
    public Result<Boolean> priv_verifyBlobURI(String blobId) {
    	return super.toJavaResult( 
        		target	.path(RestShorts.VERIFY)
        				.request().accept(MediaType.APPLICATION_JSON)
        				.post(Entity.entity(blobId, MediaType.APPLICATION_JSON)) , boolean.class );
    }

	@Override
	public Result<Boolean> verifyBlobURI(String blobId) {
		return super.reTry( () -> priv_verifyBlobURI(blobId));
	}
}