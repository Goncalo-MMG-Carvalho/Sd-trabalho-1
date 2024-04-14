package tukano.servers.java;

import java.util.List;
import java.util.logging.Logger;

import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;
import tukano.api.User;
import tukano.api.java.Users;
import tukano.persistence.Hibernate;

//ADD PERSISTENCE AND CHECK IF ITS RIGHT
public class JavaUsers implements Users {
	//private final Map<String,User> users = new HashMap<>();

	private static Logger Log = Logger.getLogger(JavaUsers.class.getName());

	@Override
	public Result<String> createUser(User user) {
		
		Log.info("createUser : " + user);
		// Check if user data is valid
		if(user.userId() == null || user.pwd() == null || user.displayName() == null || user.email() == null) {
			Log.info("User object invalid.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}

		var resultUsers = Hibernate.getInstance().sql("SELECT * FROM User user WHERE user.userId = '" + user.userId() + "'", User.class);

		if(!resultUsers.isEmpty())
		{
			Log.info("User already exists.");
			return Result.error( ErrorCode.CONFLICT);
		}
		// Insert user, checking if name already exists
		/*if( users.putIfAbsent(user.userId(), user) != null ) {
			Log.info("User already exists.");
			return Result.error( ErrorCode.CONFLICT);
		}*/

		//db.sql("SELECT FROM ");
		Hibernate.getInstance().persist(user);

		return Result.ok( user.userId() );
	}

	@Override
	public Result<User> getUser(String userId, String pwd) {
		Log.info("getUser : user = " + userId + "; pwd = " + pwd);
		
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

		if(resultUsers.isEmpty())
		{
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		var userList = Hibernate.getInstance().sql("SELECT * FROM User u WHERE u.userId = " + userId, User.class);
		User user = userList.get(0);

		return Result.ok(user);
	}

	@Override
	public Result<User> updateUser(String userId, String pwd, User newUser) { // TODO DONE
		
		//Log.info("getUser : user = " + userId + "; pwd = " + pwd);
		
		// Check if user is valid
		if(userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		if(!userId.equals(newUser.userId())) {
			Log.info("User id can't be changed");
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

		if(resultUsers.isEmpty())
		{
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}

		/*
		//Check if the password is correct
		if( !currUser.pwd().equals(pwd) ) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}
		*/

		var passList = Hibernate.getInstance().sql("SELECT user.pwd FROM User user WHERE user.userId = '" + userId + "'", String.class);
		if (!passList.get(0).equals(pwd)) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}


		//users.replace(userId, newUser);
		Hibernate.getInstance().update(newUser);

		
		//return Result.error( ErrorCode.NOT_IMPLEMENTED);
		return Result.ok(newUser);
	}

	@Override
	public Result<User> deleteUser(String userId, String pwd) { //TODO DONE

		//Log.info("delete user : user = " + userId + "; pwd = " + pwd);
		
		// Check if user is valid
		if(userId == null || pwd == null) {
			Log.info("Name or Password null.");
			return Result.error( ErrorCode.BAD_REQUEST);
		}
		
		/*//User user = users.get(userId);			
		// Check if user exists 
		if( user == null ) {
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}*/

		var resultUsers = Hibernate.getInstance().sql("SELECT * FROM User user WHERE user.userId = '" + userId + "'", User.class);

		if(resultUsers.isEmpty())
		{
			Log.info("User does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		
		var passList = Hibernate.getInstance().sql("SELECT user.pwd FROM User user WHERE user.userId = '" + userId + "'", String.class);
		if (!passList.get(0).equals(pwd)) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}

		/*//Check if the password is correct
		if( !user.pwd().equals( pwd)) {
			Log.info("Password is incorrect.");
			return Result.error( ErrorCode.FORBIDDEN);
		}*/
		
		//TODO SUPOSTAMENTE TEREMOS QUE FAZER UM DELETE NOS SHORTS/ CHAMAR O CLIENTE e NOS LIKES
		Hibernate.getInstance().delete(resultUsers.get(0)); 
		//users.remove(userId);
		
		
		//return Result.error( ErrorCode.NOT_IMPLEMENTED);
		return Result.ok(resultUsers.get(0));
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) { //TODO DONE
		//Log.info("search users: pattern = " + null);
		
		if(pattern == null) {
			Log.info("pattern is null.");
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
