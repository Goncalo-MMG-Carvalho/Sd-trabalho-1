package tukano.clients.rest;

import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.GenericType;

public class RestUsersClient extends RestClient implements Users {
		
	final WebTarget target;
	
    protected RestUsersClient(URI serverURI) {
    	super(serverURI);
		target = client.target(serverURI).path(RestUsers.PATH);
	}
    
    

	private Result<String> clt_createUser(User user) {
    	return super.toJavaResult( 
    		target.request().accept(MediaType.APPLICATION_JSON)
    						.post(Entity.entity(user, MediaType.APPLICATION_JSON)), String.class );
    }

    private Result<User> clt_getUser(String userId, String pwd) {
    	return super.toJavaResult(
    			target	.path( userId )
    					.queryParam(RestUsers.PWD, pwd).request()
    					.accept(MediaType.APPLICATION_JSON)
    					.get(), User.class);
    }

    /* ----------- DADO PELO PROF ACIMA ----------- */
    
    private Result<User> clt_updateUser(String userId, String pwd, User user) {
		return super.toJavaResult( target.path(userId)
										.queryParam(RestUsers.PWD, pwd)
										.request()
										.accept(MediaType.APPLICATION_JSON)
										.put(Entity.entity(user, MediaType.APPLICATION_JSON)), User.class);
		
		
    }
    
    private Result<User> clt_deleteUser(String userId, String pwd) {    	
    	return super.toJavaResult(target.path(userId)
    			.queryParam(RestUsers.PWD, pwd)
    			.request()
    			.accept(MediaType.APPLICATION_JSON)
    			.delete(), User.class);
    }
    
    private Result<List<User>> clt_searchUsers(String pattern) {
    	return super.toJavaResult( target.path("")
					.queryParam(RestUsers.QUERY, pattern)
					.request()
					.accept(MediaType.APPLICATION_JSON)
					.get(), new GenericType<List<User>> () {} ); //TODO recomendado fazer assim, verificar
    }
    
    
    
    /* ----------- DADO PELO PROF ABAIXO ----------- */
    
    @Override
    public Result<String> createUser(User user) {
    	return super.reTry( () -> clt_createUser(user));
    }

    @Override
    public Result<User> getUser(String userId, String pwd) {
    	return super.reTry( () -> clt_getUser(userId, pwd));
    }
    
    /* ----------- DADO PELO PROF ACIMA ----------- */
    

	@Override
	public Result<User> updateUser(String userId, String pwd, User user) {
		return super.reTry(() -> clt_updateUser(userId, pwd, user));
	}

	@Override
	public Result<User> deleteUser(String userId, String pwd) {
		return super.reTry(() -> clt_deleteUser(userId, pwd));
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		return super.reTry(() -> clt_searchUsers(pattern));
	}
}