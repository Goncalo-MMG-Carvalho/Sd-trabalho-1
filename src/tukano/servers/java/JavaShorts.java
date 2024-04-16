package tukano.servers.java;


import java.util.List;
import java.util.logging.Logger;

import tukano.api.User;
import tukano.persistence.Hibernate;
import tukano.api.Follow;
import tukano.api.Like;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.clients.UserClientFactory;
import tukano.api.java.Result.ErrorCode;

public class JavaShorts implements Shorts {
	
	private static Logger Log = Logger.getLogger(JavaShorts.class.getName());

	@Override
	public Result<Short> createShort(String userId, String pwd) {
		Log.info("createShort ... user: " + userId + " password: " + pwd);
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
		//Log.info("after creating factory to call get user"); //DEBUG
		if(!res.isOK()) {
			Log.info("Error user does not exist or password wrong.");
			return Result.error(res.error());
		}
		//Log.info("after checking result of get user"); //DEBUG
		// String shortId, String ownerId, String blobUrl, long timestamp, int totalLikes
		//TODO NAO SEI O Q FAZER AQUI COM O BLOB_URL
		String id = generateShortId(userId); 
		String blobId = generateBlobID(id);
		
		Short sh = new Short(id, userId, generateBlobUrl(blobId));
		
		Hibernate.getInstance().persist(sh);
		//Log.info("after short presist"); //DEBUG
		
		Log.info("Success in creating short: " + id);
		return Result.ok(sh);
	}

	@Override
	public Result<Void> deleteShort(String shortId, String pwd) {
		
		Log.info("deleteShort: " + shortId);
		
//		if(shortId == null || pwd == null ) {
//			Log.info("Input invalid.");
//			return Result.error( ErrorCode.BAD_REQUEST);
//		}
		
		var shortList = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.shortId = '" + shortId + "'", Short.class);
		
		if(shortList.isEmpty()) {
			Log.info("Short does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		Short sh = shortList.get(0);
		
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
			Log.info("Error with password");
			return Result.error(res.error());
		}
		
		// get likes to this short
		var likesList = Hibernate.getInstance().sql("SELECT * FROM Like l WHERE l.shortId = '" + shortId + "'" , Like.class);
		
		
		// TODO delete the blob when i delete the short?
					// call BlobsClientFactory to delete the blobs
		
		
		// delete likes
		Hibernate.getInstance().delete(likesList);
		// delete short
		Hibernate.getInstance().delete(sh);
		
		
		Log.info("Success in deleting short.");
		return Result.ok();
	}

	@Override
	public Result<Short> getShort(String shortId) {
		Log.info("getShort: " + shortId);
		
		if(shortId == null ) {
			Log.info("Input invalid.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		var shortList = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.shortId = '" + shortId + "'", Short.class);
		
		if(shortList.isEmpty()) {
			Log.info("Short does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		Short sh = shortList.get(0);
		
		Log.info("Success getShort: " +  shortId);
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
			if(error != ErrorCode.FORBIDDEN) {
				Log.info("User does not exist");
				return Result.error(error);
			}
		}
		
		var shortList = Hibernate.getInstance().sql("SELECT shortId FROM Short s WHERE s.ownerId = '" + userId + "'", String.class);
		
		
		Log.info("Success getShorts, userId: " + userId);
		return Result.ok(shortList);
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean wantToFollow, String pwd) {
		Log.info("Follow/Unfollow");
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId1, pwd);
		
		if(!res.isOK()) {
			Log.info("User1 does not exist or password is incorrect");
			return Result.error(res.error());
		}
		
		res = uclient.getUser(userId2, "randomPass"); 
		
		if(!res.isOK()) { //verify if the user exists but ignore wrong password
			ErrorCode error = res.error();
			if(error != ErrorCode.FORBIDDEN) {
				Log.info("User2 does not exist.");
				return Result.error(error);
			}
		}
		
		String newId = generateFollowId(userId1, userId2);
		
		var followList = Hibernate.getInstance().sql("SELECT * FROM Follow f WHERE f.id = '" + newId + "'", Follow.class);
		boolean alreadyFollow = !followList.isEmpty();
		
		
		Follow f = new Follow(newId, userId1, userId2);
		
		if(wantToFollow) {
			if(!alreadyFollow) {
				Hibernate.getInstance().persist(f);
			}
			else {
				// TODO reconsider this
				Log.info("ESTE ERRO NAO ESTA NA INTERFACE");
				return Result.error(ErrorCode.CONFLICT); //comentar isto os testes do prof nao coincidem com o resilt
			}
		}
		else {
			if(alreadyFollow) {
				Hibernate.getInstance().delete(f);
			}
			else {
				// TODO reconsider this
				Log.info("ESTE ERRO NAO ESTA NA INTERFACE");
				return Result.error(ErrorCode.CONFLICT);
				
			}
		}
		
		Log.info("Success follow/unfollow");
		return Result.ok();
	}	

	@Override
	public Result<List<String>> followers(String userId, String pwd) {
		Log.info("Start of followers. user: " + userId);
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, pwd);
		
		if(!res.isOK()) {
			Log.info("Error user does not exist or password wrong.");
			return Result.error(res.error());
		}
		
		var followersList = Hibernate.getInstance().sql("SELECT follower FROM Follow f WHERE f.followed = '" + userId + "'", String.class);
		
		Log.info("Success followers. user: " + userId);
		return Result.ok(followersList);
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String pwd) {
		// TODO Verificar error code when user does not exist
		
		if(shortId == null || userId == null || pwd == null) {
			Log.info("info: bad input");
			return Result.error(ErrorCode.BAD_REQUEST);
		}
		
		
		Log.info("|Like| user: " + userId + " wants to like short: " + shortId + " value: " + isLiked);
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, pwd);
		
		if(!res.isOK()) {
			Log.info("Error with password or user does not exist.");
			return Result.error(res.error());
		}
		
		
		Result<Short> res2 = this.getShort(shortId);
		
		if(!res2.isOK()) {
			Log.info("Short not found");
			return Result.error(res2.error());
		}
		
		Short oldShort = res2.value();
		
		String newId = generateLikeId(userId, shortId);
		
		var likelist = Hibernate.getInstance().sql("SELECT * FROM Like l WHERE l.id = '" + newId + "'", Like.class);
		var isEmpty = likelist.isEmpty();
		
		if(!isEmpty && isLiked) {
			Log.info("Like already exists");
			return Result.error(ErrorCode.CONFLICT);
		}
		
		if(isEmpty && !isLiked) {
			Log.info("Already not liked");
			return Result.error(ErrorCode.NOT_FOUND);
		}
		
		Like l = new Like(newId, userId, shortId);
		//Short newShort;
		
		if(isLiked) {
			Hibernate.getInstance().persist(l);
			// String shortId, String ownerId, String blobUrl, long timestamp, int totalLikes
//			newShort = new Short(oldShort.getShortId(), oldShort.getOwnerId(), oldShort.getBlobUrl(),
//					oldShort.getTimestamp(), oldShort.getTotalLikes() + 1);
//			Hibernate.getInstance().update(newShort);
			
			
			oldShort.incLikes();
			Hibernate.getInstance().update(oldShort);
			
		}
		else {
			Hibernate.getInstance().delete(l);

//			newShort = new Short(oldShort.getShortId(), oldShort.getOwnerId(), oldShort.getBlobUrl(),
//			oldShort.getTimestamp(), oldShort.getTotalLikes() - 1);
//			Hibernate.getInstance().update(newShort);
			
			oldShort.decLikes();
			Hibernate.getInstance().update(oldShort);
		}
		
		Log.info("Success Like/Dislike.");
		return Result.ok();
	}

	@Override
	public Result<List<String>> likes(String shortId, String pwd) {		
		Log.info("Likes ...");
		
		Result<Short> res = this.getShort(shortId);
		
		if(!res.isOK()) {
			Log.info("Short does not exist.");
			return Result.error(res.error());
		}
		
		Short sh = res.value();
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res2 = uclient.getUser(sh.getOwnerId(), pwd);
		
		if(!res2.isOK()) {
			Log.info("Wrong password");
			return Result.error(res2.error());
		}
		
		var likeList = Hibernate.getInstance().sql("SELECT l.user FROM Like l WHERE l.shortId = '" + shortId + "'", String.class);
		
		Log.info("Success Likes.");
		return Result.ok(likeList);
	}

	@Override
	public Result<List<String>> getFeed(String userId, String pwd) {
		Log.info("Started getFeed.");
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, pwd);
		
		if(!res.isOK()) {
			Log.info("User does not exist or wrong password");
			return Result.error(res.error());
		}
		
		/*
		//DEBUG
		//PRINT ALL THE FOLLOWERS AND FOLLOWED
		var allFollow = Hibernate.getInstance().sql("SELECT * FROM Follow f", Follow.class);
		allFollow.forEach(f -> Log.info("follow id: " + f.getId() + 
		" follow: " + f.getFollowed() + " follower: " + f.getFollower()));

		var followList = Hibernate.getInstance().sql("SELECT f.follower FROM Follow f WHERE f.follower = '"+ userId + "'", String.class);
		followList.forEach(f -> Log.info("follower: " + f));
		if (followList.isEmpty())
			Log.info("no followers");
		var followedList = Hibernate.getInstance().sql("SELECT f.followed FROM Follow f WHERE f.followed = '"+ userId + "'", String.class);
		followedList.forEach(f -> Log.info("followed: " + f));
		if (followedList.isEmpty())
			Log.info("no one followed");

		//this from the method followers
		var followersList = Hibernate.getInstance().sql("SELECT f.follower FROM Follow f WHERE f.followed = '" + userId + "'", String.class);
		followersList.forEach(f -> Log.info("followers: " + f));
		if (followersList.isEmpty())
			Log.info("another empty list");
		//DEBUG
		*/
		//Ja percebi o teste 4b do prof é mesmo rato quando não há users a seguir poem os shorts do mesmo
		
		var followList = Hibernate.getInstance().sql( "SELECT s.shortId"
					+ "FROM (SELECT * FROM Short INNER JOIN Follow ON Short.ownerId = Follow.followed) s "
					+ "WHERE s.follower = '" + userId + "' "
					+ "ORDER BY s.timestamp ASC", String.class);
		var followList2 = Hibernate.getInstance().sql("SELECT s.shortId FROM Short s WHERE s.ownerId = '" + userId + "'", String.class);
		followList.addAll(followList2);
		followList.forEach(f -> Log.info("short: " + f));

		Log.info("Success getfeed.");
		return Result.ok(followList);
	}
	
	public Result<Boolean> verifyBlobURI(String blobId) {
		String url = generateBlobUrl(blobId);
		
		var blobUriList = Hibernate.getInstance().sql("SELECT s.shortId FROM Shorts s WHERE s.blobUrl = '" + url + "'", String.class);
		
		if(blobUriList.isEmpty()) {
			return Result.ok(false);
		}
		
		return Result.ok(true);
	}
	
	
	
	
	private static String generateShortId(String userId) {
		return userId + "." + System.currentTimeMillis();
	}
	
	private static String generateFollowId(String userId1, String userId2) {
		return userId1 + "." + userId2;
	}
	
	private static String generateLikeId(String userId, String shortId) {
		return userId + "." + shortId;
	}
	
	public static String generateBlobID(String shortId) {
		return "blob." + shortId;
	}
	
	public static String generateBlobUrl(String blobId) {
		return "blobs/"+ blobId;
	}
}