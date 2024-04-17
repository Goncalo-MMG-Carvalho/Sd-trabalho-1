package tukano.servers.java;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import tukano.api.User;
import tukano.persistence.Hibernate;
import tukano.api.Follow;
import tukano.api.Likes;
import tukano.api.Short;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.api.java.Users;
import tukano.clients.BlobClientFactory;
import tukano.clients.UserClientFactory;
import tukano.discovery.Discovery;
import tukano.api.java.Result.ErrorCode;

public class JavaShorts implements Shorts {
	
	private static Logger Log = Logger.getLogger(JavaShorts.class.getName());

	@Override
	public Result<Short> createShort(String userId, String pwd) {
		//Log.info("createShort ... user: " + userId + " password: " + pwd);
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
//		Log.info("after creating factory to call get user"); //DEBUG
		if(!res.isOK()) {
//			Log.info("Error user does not exist or password wrong.");
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
		
//		Log.info("Success in creating short: " + id);
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
		
		
		
		
		// TODO delete the blob when i delete the short?
					// call BlobsClientFactory to delete the blobs
		
//		Log.info("Before deleting blobs.");
		
		
		Blobs bclient = BlobClientFactory.getBlobsClient();
		bclient.deleteShortBlobs(shortId);
		
		
		
		// get likes to this short
		var likesList = Hibernate.getInstance().sql("SELECT * FROM Likes l WHERE l.shortId = '" + shortId + "'" , Likes.class);
		
//		Log.info("Before deleting Likes, After deleting Blobs. likeListsize: " + likesList.size());
		// delete likes
		if(!likesList.isEmpty())
			for (Likes l : likesList) {
				Hibernate.getInstance().delete(l);
			}
			//Hibernate.getInstance().delete(likesList);
		// delete short
		
//		Log.info("Before deleting short, After deleting likes.");
		Hibernate.getInstance().delete(sh);
		
		
		Log.info("Success in deleting short: " + shortId);
		return Result.ok();
	}

	@Override
	public Result<Short> getShort(String shortId) {
//		Log.info("getShort: " + shortId);
		
		if(shortId == null ) {
//			Log.info("Input invalid.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		var shortList = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.shortId = '" + shortId + "'", Short.class);
		
		if(shortList.isEmpty()) {
//			Log.info("Short does not exist.");
			return Result.error( ErrorCode.NOT_FOUND);
		}
		
		Short sh = shortList.get(0);
		
//		Log.info("Success getShort: " +  shortId);
		return Result.ok(sh);
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
//		Log.info("getShorts of user: " + userId);
		
		if(userId == null ) {
//			Log.info("Input invalid.");
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
		Result<User> res = uclient.getUser(userId, "randomPass"); // do this to see if the user exists
		
		if(!res.isOK()) {
			ErrorCode error = res.error();
			if(error != ErrorCode.FORBIDDEN) { // if forbidden then the user exists, but wrong password. So i confirmed the user exists
//				Log.info("User does not exist");
				return Result.error(error);
			}
		}
		
		var shortList = Hibernate.getInstance().sql("SELECT shortId FROM Short s WHERE s.ownerId = '" + userId + "'", String.class);
		
		
//		Log.info("Success getShorts, userId: " + userId);
		return Result.ok(shortList);
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean wantToFollow, String pwd) {
//		Log.info("Follow/Unfollow");
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId1, pwd);
		
		if(!res.isOK()) {
//			Log.info("User1 does not exist or password is incorrect");
			return Result.error(res.error());
		}
		
		res = uclient.getUser(userId2, "randomPass"); 
		
		if(!res.isOK()) { //verify if the user exists but ignore wrong password
			ErrorCode error = res.error();
			if(error != ErrorCode.FORBIDDEN) {
//				Log.info("User2 does not exist.");
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
//				Log.info("ESTE ERRO NAO ESTA NA INTERFACE");
				return Result.error(ErrorCode.CONFLICT); //comentar isto os testes do prof nao coincidem com o resilt
			}
		}
		else {
			if(alreadyFollow) {
				Hibernate.getInstance().delete(f);
			}
			/*else {
				// sem este ja funciona
//				Log.info("ESTE ERRO NAO ESTA NA INTERFACE");
				return Result.error(ErrorCode.CONFLICT);
				
			}*/
		}
		
//		Log.info("Success follow/unfollow");
		return Result.ok();
	}	

	@Override
	public Result<List<String>> followers(String userId, String pwd) {
//		Log.info("Start of followers. user: " + userId);
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, pwd);
		
		if(!res.isOK()) {
//			Log.info("Error user does not exist or password wrong.");
			return Result.error(res.error());
		}
		
		var followersList = Hibernate.getInstance().sql("SELECT follower FROM Follow f WHERE f.followed = '" + userId + "'", String.class);
		
//		Log.info("Success followers. user: " + userId);
		return Result.ok(followersList);
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean wantToLike, String pwd) {
		// TODO Verificar error code when user does not exist
		
		if(shortId == null || userId == null || pwd == null) {
//			Log.info("info: bad input");
			return Result.error(ErrorCode.BAD_REQUEST);
		}
		
		
		Log.info("|Like| user: " + userId + ", short: " + shortId + ", wantToLike: " + wantToLike);
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, pwd);
		
		if(!res.isOK()) {
//			Log.info("Error with password or user does not exist.");
			return Result.error(res.error());
		}
		
		
		Result<Short> res2 = this.getShort(shortId);
		
		if(!res2.isOK()) {
//			Log.info("Short not found");
			return Result.error(res2.error());
		}
		
		Short oldShort = res2.value();
		
		String newId = generateLikeId(userId, shortId);
		
		var likelist = Hibernate.getInstance().sql("SELECT * FROM Likes l WHERE l.id = '" + newId + "'", Likes.class);
		var alreadyLikes = !likelist.isEmpty();
		
		if(alreadyLikes && wantToLike) {
//			Log.info("Like already exists");
			return Result.error(ErrorCode.CONFLICT);
		}
		
		if(!alreadyLikes && !wantToLike) {
//			Log.info("Already not liked");
			return Result.error(ErrorCode.NOT_FOUND);
		}
		
		Likes l = new Likes(newId, userId, shortId);
		//Short newShort;
		
		if(wantToLike) {
			Hibernate.getInstance().persist(l);
			// String shortId, String ownerId, String blobUrl, long timestamp, int totalLikes
//			newShort = new Short(oldShort.getShortId(), oldShort.getOwnerId(), oldShort.getBlobUrl(),
//					oldShort.getTimestamp(), oldShort.getTotalLikes() + 1);
//			Hibernate.getInstance().update(newShort);
			
			Log.info("Before increase -> Likes: " + oldShort.getTotalLikes());
			
			oldShort.incLikes();
			
			Log.info("After increase -> Likes: " + oldShort.getTotalLikes());
			
			Hibernate.getInstance().update(oldShort);
			
			var list = Hibernate.getInstance().sql("SELECT s.totalLikes FROM Short s WHERE s.shortId = '" + shortId + "'", Integer.class);
			Log.info("Recorded in the DB -> likes: " + list.get(0));
			
		}
		else {
			Log.info("Before decrease -> Likes: " + oldShort.getTotalLikes());
			
			Hibernate.getInstance().delete(l);

//			newShort = new Short(oldShort.getShortId(), oldShort.getOwnerId(), oldShort.getBlobUrl(),
//			oldShort.getTimestamp(), oldShort.getTotalLikes() - 1);
//			Hibernate.getInstance().update(newShort);
			
			
			
			oldShort.decLikes();
			
			Log.info("After  decrease -> Likes: " + oldShort.getTotalLikes());
			
			Hibernate.getInstance().update(oldShort);
			
			// TODO Delete Debug
			var list = Hibernate.getInstance().sql("SELECT s.totalLikes FROM Short s WHERE s.shortId = '" + shortId + "'", Integer.class);
			Log.info("Recorded in the DB -> likes: " + list.get(0));
		}
		
//		Log.info("Success Like/Dislike.");
		return Result.ok();
	}

	@Override
	public Result<List<String>> likes(String shortId, String pwd) {		
//		Log.info("Likes ...");
		
		Result<Short> res = this.getShort(shortId);
		
		if(!res.isOK()) {
//			Log.info("Short does not exist.");
			return Result.error(res.error());
		}
		
		Short sh = res.value();
		
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res2 = uclient.getUser(sh.getOwnerId(), pwd);
		
		if(!res2.isOK()) {
//			Log.info("Wrong password");
			return Result.error(res2.error());
		}
		
		var likeList = Hibernate.getInstance().sql("SELECT l.user FROM Likes l WHERE l.shortId = '" + shortId + "'", String.class);
		
//		Log.info("Success Likes.");
		return Result.ok(likeList);
	}

	@Override
	public Result<List<String>> getFeed(String userId, String pwd) {
//		Log.info("Started getFeed.");
		Users uclient = UserClientFactory.getUsersClient();
		Result<User> res = uclient.getUser(userId, pwd);
		
		if(!res.isOK()) {
//			Log.info("User does not exist or wrong password");
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
		
		// VERSÃO SEBAS
		
		var allFollow = Hibernate.getInstance().sql("SELECT * FROM Follow f WHERE f.follower = '" + userId + "'", Follow.class);
		
		List<Short> followedShortsList = Hibernate.getInstance().sql(" SELECT * FROM Short s WHERE s.ownerId = '" + userId + "'" , Short.class);
		
		for (Follow followed : allFollow) {
			var temp = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.ownerId = '" + followed.getFollowed() + "'" , Short.class);
			if(!temp.isEmpty())
				followedShortsList.addAll(temp);
		}
		
		// SORT
		followedShortsList.sort((o1, o2) -> {
			return (int) (o2.getTimestamp() - o1.getTimestamp());
		});
		
		List<String> resultList = new ArrayList<>();
		for (Short s : followedShortsList) {
			resultList.add(s.getShortId());
		}
		
		/*
		followList.addAll(followList2);
		followList.forEach(f -> Log.info("short: " + f));*/
		
		// VERSÃO GONCAS
		/*
		var followList = Hibernate.getInstance()
				.sql( "SELECT s.shortId "
					+ "FROM (SELECT Short.shortId, Short.timestamp "
						+ "FROM Short INNER JOIN Follow ON Short.ownerId = Follow.followed "
						+ "WHERE Follow.follower = '" + userId + "' OR Short.ownerId = '" + userId + "' "
						+ "ORDER BY Short.timestamp ASC) s", String.class);
		
		*/
		
		
//		Log.info("Success getfeed.");
		return Result.ok(resultList);
	}
	
	public Result<String> verifyBlobURI(String blobId) {
		var blobUriList = Hibernate.getInstance().sql("SELECT s.blobUrl FROM Short s WHERE s.blobUrl LIKE '%" + blobId + "%'", String.class);
		
		if(blobUriList.isEmpty()) {
			return Result.ok(null);
		}
		
		return Result.ok(blobUriList.get(0));
	}
	
	@Override
	public Result<Void> deleteUserLikes(String userId) {
		
		var userLikesList = Hibernate.getInstance().sql("SELECT * FROM Likes l Where l.user = '" + userId + "'", Likes.class);
		
		if(!userLikesList.isEmpty()) {
			for (Likes l : userLikesList) {
				var s = Hibernate.getInstance().sql("SELECT * FROM Short s WHERE s.shortId = '"  + l.getShortId() + "'", Short.class).get(0);
				s.decLikes();
				Hibernate.getInstance().update(s);
				Hibernate.getInstance().delete(l);
			}
		}
		
		return Result.ok();
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
		URI[] blobsServices = Discovery.getInstance().knownUrisOf("blobs", 1); //talvez mudar as minEntries depois
		
		return blobsServices[0].toString() + "/" + blobId;
	}

	
}