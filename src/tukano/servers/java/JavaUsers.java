package tukano.servers.java;

import java.util.List;
import java.util.logging.Logger;

import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.clients.ShortClientFactory;
import tukano.api.java.Shorts;
import tukano.api.User;
import tukano.api.java.Users;
import tukano.persistence.Hibernate;

//ADD PERSISTENCE AND CHECK IF ITS RIGHT
public class JavaUsers implements Users {

	private static Logger Log = Logger.getLogger(JavaUsers.class.getName());

	@Override
	public Result<String> createUser(User user) {
		
		//Log.info("createUser : " + user);
		String userId = user.userId();
		
		// Check if user data is valid
		if(userId == null || user.pwd() == null || user.displayName() == null || user.email() == null) {
			//Log.info("User object invalid.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		var resultUsers = Hibernate.getInstance().sql("SELECT * FROM User user WHERE user.userId = '" + userId + "'", User.class);

		if(!resultUsers.isEmpty()) {
			//Log.info("User already exists.");
			return Result.error( ErrorCode.CONFLICT);
		}
		
		Hibernate.getInstance().persist(user);

		return Result.ok(userId);
	}

	@Override
	public Result<User> getUser(String userId, String pwd) {
		//Log.info("getUser : user = " + userId + "; pwd = " + pwd);
		
		// Check if user is valid
		if(userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		var resultUsers = Hibernate.getInstance().sql("SELECT * FROM User user WHERE user.userId = '" + userId + "'", User.class);

		if(resultUsers.isEmpty()) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		User user = resultUsers.get(0);
		
		if(!user.pwd().equals(pwd)) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String userId, String pwd, User newUser) { 
		
		Log.info("getUser : user = " + userId + "; pwd = " + pwd);
		Log.info(String.format("New User: userId = %s, pass = %s, email = %s, displayName = %s",
				newUser.getUserId(), newUser.getPwd(), newUser.getEmail(), newUser.getDisplayName()));
		
		// Check if user is valid
		if(userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		var resultUsers = Hibernate.getInstance().sql("SELECT * FROM User user WHERE user.userId = '" + userId + "'", User.class);

		if(resultUsers.isEmpty()) {
			//Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}

		User user = resultUsers.get(0);
		
		if (!user.pwd().equals(pwd)) {
			//Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}

		if (newUser.pwd() != null)
			user.setPwd(newUser.pwd());
		
		if (newUser.email() != null)
			user.setEmail(newUser.email());
		
		if (newUser.displayName() != null)
			user.setDisplayName(newUser.displayName());
		
		if (newUser.userId() != null && !newUser.getUserId().equals(userId)) {
			//Log.info("User id can't be changed");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		Hibernate.getInstance().update(user);

		return Result.ok(user);
	}

	@Override
	public Result<User> deleteUser(String userId, String pwd) {

		Log.info("delete user : user = " + userId + "; pwd = " + pwd);
		
		// Check if user is valid
		if(userId == null || pwd == null) {
			//Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		var resultUsers = Hibernate.getInstance().sql("SELECT * FROM User user WHERE user.userId = '" + userId + "'", User.class);

		if(resultUsers.isEmpty()) {
			//Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		User user = resultUsers.get(0);
		
		if (!user.getPwd().equals(pwd)) {
			//Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}
		
		// SUPOSTAMENTE TERIAMOS QUE FAZER UM DELETE NOS FOLLOWS, MAS NÃO É TESTADO
		
		Shorts sclient = ShortClientFactory.getShortsClient();
		Result<List<String>> res = sclient.getShorts(userId);
		
		if(res.isOK()) { //delete user shorts, if he has any
			var shortsList = res.value();
			
			for (String shortId : shortsList) {
				
				Log.info("Deleting short: " + shortId);
				sclient.deleteShort(shortId, pwd);
			}
		}
		
		//Deletes all the likes this user has made
		sclient.deleteUserLikes(userId);
		
		Hibernate.getInstance().delete(user); 
		
		return Result.ok(user);
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		if(pattern == null) {
			//Log.info("pattern is null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		var listUsers = Hibernate.getInstance().sql("SELECT * FROM user WHERE UPPER(userId) LIKE '%" + pattern.toUpperCase() + "%'", User.class);
		
        return Result.ok(listUsers);
	}
}
