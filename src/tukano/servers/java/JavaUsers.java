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
	//private final Map<String,User> users = new HashMap<>();

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
		// Insert user, checking if name already exists
		/*if( users.putIfAbsent(user.userId(), user) != null ) {
			Log.info("User already exists.");
			return Result.error( ErrorCode.CONFLICT);
		}*/

		//db.sql("SELECT FROM ");
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

		/*
		//User user = users.get(userId);			
		// Check if user exists 
		if( user == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		*/

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
	public Result<User> updateUser(String userId, String pwd, User newUser) { // TODO DONE
		
		Log.info("getUser : user = " + userId + "; pwd = " + pwd);
		Log.info(String.format("New User: userId = %s, pass = %s, email = %s, displayName = %s",
				newUser.getUserId(), newUser.getPwd(), newUser.getEmail(), newUser.getDisplayName()));
		
		// Check if user is valid
		if(userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		/* 
		User currUser = users.get(userId);			
		// Check if user exists 
		if( currUser == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		*/

		var resultUsers = Hibernate.getInstance().sql("SELECT * FROM User user WHERE user.userId = '" + userId + "'", User.class);

		if(resultUsers.isEmpty()) {
			//Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}

		/*
		//Check if the password is correct
		if( !currUser.pwd().equals(pwd) ) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}
		*/
		User user = resultUsers.get(0);
		
		//var passList = Hibernate.getInstance().sql("SELECT user.pwd FROM User user WHERE user.userId = '" + userId + "'", String.class);
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
		
		//users.replace(userId, newUser);
		Hibernate.getInstance().update(user);

		//return Result.error( ErrorCode.NOT_IMPLEMENTED);
		return Result.ok(user);
	}

	@Override
	public Result<User> deleteUser(String userId, String pwd) { //TODO DONE

		//Log.info("delete user : user = " + userId + "; pwd = " + pwd);
		
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
		
		//var passList = Hibernate.getInstance().sql("SELECT user.pwd FROM User user WHERE user.userId = '" + userId + "'", String.class);
		User user = resultUsers.get(0);
		
		if (!user.getPwd().equals(pwd)) {
			//Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}
		
		//TODO SUPOSTAMENTE TEREMOS QUE FAZER UM DELETE NOS FOLLOWS
		
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
	public Result<List<User>> searchUsers(String pattern) { //TODO DONE
		//Log.info("search users: pattern = " + null);
		
		if(pattern == null) {
			//Log.info("pattern is null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		//List<User> matchedUsers = new ArrayList<>();
		//String PATTERN = pattern.toLowerCase();

		var listUsers = Hibernate.getInstance().sql("SELECT * FROM user WHERE UPPER(userId) LIKE '%" + pattern.toUpperCase() + "%'", User.class);
		/*users.forEach((userId, user) -> {
			if (userId.toLowerCase().contains(PATTERN)) {
              matchedUsers.add(user);
			}
		;*/
		
        return Result.ok(listUsers);
		//return Result.error( ErrorCode.NOT_IMPLEMENTED);
	}
}
