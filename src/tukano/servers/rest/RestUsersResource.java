package tukano.servers.rest;

import java.util.List;

import tukano.api.User;
import tukano.api.java.Users;
import tukano.api.rest.RestUsers;
import tukano.servers.java.JavaUsers;
import jakarta.inject.Singleton;

@Singleton
public class RestUsersResource extends BigBoyRest implements RestUsers {

	final Users impl;
	
	public RestUsersResource() {
		this.impl = new JavaUsers();
	}
	
	@Override
	public String createUser(User user) {
		return super.fromJavaResult(impl.createUser( user));
	}

	@Override
	public User getUser(String userId, String pwd) {
		return super.fromJavaResult(impl.getUser(userId, pwd));
	}
	

	@Override
	public User updateUser(String userId, String pwd, User user) {
		return super.fromJavaResult(impl.updateUser(userId, pwd, user));
	}

	@Override
	public User deleteUser(String userId, String pwd) {
		return super.fromJavaResult(impl.deleteUser(userId, pwd));
	}

	@Override
	public List<User> searchUsers(String pattern) {
		return super.fromJavaResult(impl.searchUsers(pattern));
	}
	
}