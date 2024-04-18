package tukano.clients.grpc;

import static tukano.impl.grpc.common.DataModelAdaptor.GrpcShort_to_Short;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannelBuilder;
import tukano.api.Short;
import tukano.api.java.Result;
import tukano.api.java.Shorts;
import tukano.impl.grpc.generated_java.ShortsGrpc;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.*;

public class GrpcShortsClient extends GrpcClient implements Shorts{
	
	final ShortsGrpc.ShortsBlockingStub stub;
	
	public GrpcShortsClient(URI serverURI) {
		super(serverURI);
		var channel = ManagedChannelBuilder.forAddress(serverURI.getHost(), serverURI.getPort()).usePlaintext().build();
		stub = ShortsGrpc.newBlockingStub( channel ).withDeadlineAfter(GrpcClient.GRPC_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	
	private Result<Short> priv_createShort(String userId, String password) {
		return super.toJavaResult( () -> {
			var res = stub.createShort(CreateShortArgs.newBuilder().setUserId(userId).setPassword(password).build());
			
			return GrpcShort_to_Short(res.getValue());
		});
				
	}
	
	private Result<Void> priv_deleteShort(String shortId, String password) {
		return super.toJavaResult( () -> {
			stub.deleteShort(DeleteShortArgs.newBuilder().setShortId(shortId).setPassword(password).build());
			
			return null;
		});		
	}
	
	private Result<Short> priv_getShort(String shortId) {
		return super.toJavaResult( () -> {
			var res = stub.getShort(GetShortArgs.newBuilder().setShortId(shortId).build());
			
			return GrpcShort_to_Short(res.getValue());
		});		
	}
	
	private Result<List<String>> priv_getShorts(String userId) {
		return super.toJavaResult( () -> {
			var res = stub.getShorts(GetShortsArgs.newBuilder().setUserId(userId).build());
			
			return res.getShortIdList();
		});		
	}
	
	private Result<Void> priv_follow(String userId1, String userId2, boolean isFollowing, String password) {
		return super.toJavaResult( () -> {
			stub.follow(FollowArgs.newBuilder().setUserId1(userId1).setUserId2(userId2).setIsFollowing(isFollowing).setPassword(password).build());
			
			return null;
		});		
	}
	
	private Result<List<String>> priv_followers(String userId, String password) {
		return super.toJavaResult( () -> {
			var res = stub.followers(FollowersArgs.newBuilder().setUserId(userId).setPassword(password).build());
			
			return res.getUserIdList();
		});		
	}
	
	private Result<Void> priv_like(String shortId, String userId, boolean isLiked, String password) {
		return super.toJavaResult( () -> {
			stub.like(LikeArgs.newBuilder().setShortId(shortId).setUserId(userId).setIsLiked(isLiked).setPassword(password).build());
			
			return null;
		});		
	}
	
	private Result<List<String>> priv_likes(String shortId, String password) {
		return super.toJavaResult( () -> {
			var res = stub.likes(LikesArgs.newBuilder().setShortId(shortId).setPassword(password).build());
			
			return res.getUserIdList();
		});		
	}
	
	private Result<List<String>> priv_getFeed(String userId, String password) {
		return super.toJavaResult( () -> {
			var res = stub.getFeed(GetFeedArgs.newBuilder().setUserId(userId).setPassword(password).build());
			
			return res.getShortIdList();
		});		
	}
	
	private Result<String> priv_verifyBlobURI(String blobId) {
		return super.toJavaResult( () -> {
			var res = stub.verifyBlobURI(verifyBlobURIArgs.newBuilder().setBlobId(blobId).build());
			
			return res.getBlobUri();
		});		
	}
	
	private Result<Void> priv_deleteUserLikes(String userId) {
		return super.toJavaResult( () -> {
			stub.deleteUserLikes(deleteUserLikesArgs.newBuilder().setUserId(userId).build());
			
			return null;
		});		
	}
	
	
	@Override
	public Result<Short> createShort(String userId, String password) {
		return super.reTry(() -> priv_createShort(userId, password));
	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		return super.reTry(() -> priv_deleteShort(shortId, password));
	}

	@Override
	public Result<Short> getShort(String shortId) {
		return super.reTry(() -> priv_getShort(shortId));
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
		return super.reTry(() -> priv_getShorts(userId));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		return super.reTry(() -> priv_follow(userId1, userId2, isFollowing, password));
	}

	@Override
	public Result<List<String>> followers(String userId, String password) {
		return super.reTry(() -> priv_followers(userId, password));
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		return super.reTry(() -> priv_like(shortId, userId, isLiked, password));
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {
		return super.reTry(() -> priv_likes(shortId, password));
	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		return super.reTry(() -> priv_getFeed(userId, password));
	}

	@Override
	public Result<String> verifyBlobURI(String blobId) {
		return super.reTry(() -> priv_verifyBlobURI(blobId));
	}

	@Override
	public Result<Void> deleteUserLikes(String userId) {
		return super.reTry(() -> priv_deleteUserLikes(userId));
	}
}
