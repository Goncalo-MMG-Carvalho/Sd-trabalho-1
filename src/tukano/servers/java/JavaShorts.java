package tukano.servers.java;


import java.util.List;
import java.util.logging.Logger;

import tukano.api.User;
import tukano.persistence.Hibernate;
import tukano.api.Follow;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.clients.UserClientFactory;
import tukano.clients.rest.RestUsersClient;
import tukano.api.java.Result.ErrorCode;

public class JavaShorts implements Shorts {
	
	
	private static Logger Log = Logger.getLogger(JavaShorts.class.getName());

	@Override
	public Result<Short> createShort(String userId, String pwd) {
//		Log.info("createShort ...");
//		
//		if(userId == null || pwd == null ) {
//			Log.info("Input invalid.");
//			return Result.error( ErrorCode.BAD_REQUEST);
//		}
//		
//		var userList = Hibernate.getInstance().sql("SELECT * FROM User u WHERE u.userId = " + userId, User.class);
//		User user = userList.get(0);
//		
//		if(user == null) {
//			Log.info("User does not exist.");
//			return Result.error( ErrorCode.NOT_FOUND);
//		}
//		
//		if(!user.pwd().equals(pwd)) {
//			Log.info("Password is incorrect.");
//			return Result.error( ErrorCode.FORBIDDEN);
//		}
		
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, pwd);
		
		if(!res.isOK()) {
			return Result.error(res.error());
		}
		
		// String shortId, String ownerId, String blobUrl, long timestamp, int totalLikes
		//TODO NAO SEI O Q FAZER AQUI COM O BLOB_URL
		String id = generateShortId(userId); 
		//String blobId = "blob" + id; //usar discovery
		
		Short sh = new Short(id, userId, "blobs/" + blobId);
		
		Hibernate.getInstance().persist(sh);
		
		return Result.ok(sh);
	}

	@Override
	public Result<Void> deleteShort(String shortId, String pwd) {
		
		Log.info("deleteShort: " + shortId);
		
//		if(shortId == null || pwd == null ) {
//			Log.info("Input invalid.");
//			return Result.error( ErrorCode.BAD_REQUEST);
//		}
		
		var shortList = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.shortId = " + shortId, Short.class);
		Short sh = shortList.get(0);
		
		if(sh == null) {
			Log.info("Short does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
//		var userList  = Hibernate.getInstance().sql("SELECT * FROM User u WHERE u.userId = " + sh.getOwnerId(), User.class);
//		User user = userList.get(0);
//		
//		if(!user.pwd().equals(pwd)) {
//			Log.info("Password is incorrect.");
//			return Result.error( ErrorCode.FORBIDDEN);
//		}
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(sh.getOwnerId(), pwd);
		
		if(!res.isOK()) {
			return Result.error(res.error());
		}
		
		
		// do i need to delete the blob when i delete the short?
		/* TODO delete short */
		Hibernate.getInstance().delete(sh);
		
		return Result.ok();
	}

	@Override
	public Result<Short> getShort(String shortId) {
		Log.info("getShort: " + shortId);
		
		if(shortId == null ) {
			Log.info("Input invalid.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		var shortList = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.shortId = " + shortId, Short.class);
		Short sh = shortList.get(0);
		
		if(sh == null) {
			Log.info("Short does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		return Result.ok(sh);
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
		Log.info("getShorts of user: " + userId);
		
		if(userId == null ) {
			Log.info("Input invalid.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		
//		var userList  = Hibernate.getInstance().sql("SELECT * FROM User u WHERE u.userId = " + userId, User.class);
//		User user = userList.get(0);
//		
//		if(user == null) {
//			Log.info("User does not exist.");
//			return Result.error( ErrorCode.NOT_FOUND);
//		}
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, "randomPass"); // TODO reavaluate
		
		if(!res.isOK()) {
			ErrorCode error = res.error();
			if(error != ErrorCode.FORBIDDEN)
				return Result.error(error);
		}
		
		var shortList = Hibernate.getInstance().sql("SELECT shortId FROM Short s WHERE s.ownerId = " + userId, String.class);
		
		return Result.ok(shortList);
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String pwd) {
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId1, pwd);
		
		if(!res.isOK()) {
			return Result.error(res.error());
		}
		
		res = uclient.getUser(userId2, "randomPass");
		
		if(!res.isOK()) {
			ErrorCode error = res.error();
			if(error != ErrorCode.FORBIDDEN)
				return Result.error(error);
		}
		
		String newId = generateFollowId(userId1, userId2);
		Follow f = new Follow(newId, userId1, userId2);
		
		if(isFollowing) {
			Hibernate.getInstance().persist(f);
		}
		else {
			Hibernate.getInstance().delete(f);
		}
		
		return Result.ok();
	}	

	@Override
	public Result<List<String>> followers(String userId, String pwd) {
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, pwd);
		
		if(!res.isOK()) {
			return Result.error(res.error());
		}
		
		
		var followersList = Hibernate.getInstance().sql("SELECT follower FROM Follow f WHERE f.followed = " + userId, String.class);
		
		return Result.ok(followersList);
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	private String generateShortId(String userId) {
		return userId + "." + System.currentTimeMillis();
	}
	
	private String generateFollowId(String userId1, String userId2) {
		return userId1 + "." + userId2;
	}
	
}