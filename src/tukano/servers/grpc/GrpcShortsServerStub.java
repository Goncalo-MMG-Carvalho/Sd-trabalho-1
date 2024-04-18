package tukano.servers.grpc;

//import static tukano.impl.grpc.common.DataModelAdaptor.GrpcShort_to_Short;
import static tukano.impl.grpc.common.DataModelAdaptor.Short_to_GrpcShort;
import static tukano.servers.grpc.GrpcUsersServerStub.errorCodeToStatus;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;

import tukano.servers.java.JavaShorts;
import tukano.api.java.Shorts;

import tukano.impl.grpc.generated_java.ShortsGrpc;
import tukano.impl.grpc.generated_java.ShortsProtoBuf.*;

public class GrpcShortsServerStub implements ShortsGrpc.AsyncService, BindableService {

	Shorts impl = new JavaShorts();
	
	 @Override 
	 public final ServerServiceDefinition bindService() {
	      return ShortsGrpc.bindService(this);
	 }
	 
	 
	public void createShort(CreateShortArgs request, StreamObserver<CreateShortResult> responseObserver) {
		var res = impl.createShort(request.getUserId(), request.getPassword());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext( CreateShortResult.newBuilder().setValue(Short_to_GrpcShort(res.value())).build());
			responseObserver.onCompleted();
		}
	}
	 
	public void deleteShort(DeleteShortArgs request, StreamObserver<DeleteShortResult> responseObserver) {
		var res = impl.deleteShort(request.getShortId(), request.getPassword());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext(DeleteShortResult.newBuilder().build());
			responseObserver.onCompleted();
		}
	}
	
	public void getShort(GetShortArgs request, StreamObserver<GetShortResult> responseObserver) {
		var res = impl.getShort(request.getShortId());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext(GetShortResult.newBuilder().setValue(Short_to_GrpcShort(res.value())).build());
			responseObserver.onCompleted();
		}
	}
	
	public void getShorts(GetShortsArgs request, StreamObserver<GetShortsResult> responseObserver) {
		var res = impl.getShorts(request.getUserId());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			
			// .add for "repeated" fields .set for non repeated fields, so .add here
			
			responseObserver.onNext( GetShortsResult.newBuilder().addAllShortId( res.value() ).build() );
			responseObserver.onCompleted();
		}
	}
	
	public void follow(FollowArgs request, StreamObserver<FollowResult> responseObserver) {
		var res = impl.follow(request.getUserId1(), request.getUserId2(), request.getIsFollowing(), request.getPassword());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext( FollowResult.newBuilder().build() );
			responseObserver.onCompleted();
		}
	}
	
	public void followers(FollowersArgs request, StreamObserver<FollowersResult> responseObserver) {
		var res = impl.followers(request.getUserId(), request.getPassword());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			// .add for "repeated" fields
			responseObserver.onNext( FollowersResult.newBuilder().addAllUserId(res.value()).build() );
			responseObserver.onCompleted();
		}
	}
	
	public void like(LikeArgs request, StreamObserver<LikeResult> responseObserver) {
		var res = impl.like(request.getShortId(), request.getUserId(), request.getIsLiked(), request.getPassword());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext( LikeResult.newBuilder().build() );
			responseObserver.onCompleted();
		}
	}
	
	public void likes(LikesArgs request, StreamObserver<LikesResult> responseObserver) {
		var res = impl.likes(request.getShortId(), request.getPassword());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			// .add for "repeated" fields
			responseObserver.onNext( LikesResult.newBuilder().addAllUserId(res.value()) .build() );
			responseObserver.onCompleted();
		}
	}
	
	public void getFeed(GetFeedArgs request, StreamObserver<GetFeedResult> responseObserver) {
		var res = impl.getFeed(request.getUserId(), request.getPassword());
		 
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			// .add for "repeated" fields
			responseObserver.onNext( GetFeedResult.newBuilder().addAllShortId(res.value()).build() );
			responseObserver.onCompleted();
		}
	}
	
	public void verifyBlobURI(verifyBlobURIArgs request, StreamObserver<verifyBlobURIResult> responseObserver) {
		var res = impl.verifyBlobURI(request.getBlobId());
		
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext( verifyBlobURIResult.newBuilder().setBlobUri(res.value()).build() );
			responseObserver.onCompleted();
		}
	}
	
	public void deleteUserLikes(deleteUserLikesArgs request, StreamObserver<deleteUserLikesResult> responseObserver) {
		var res = impl.deleteUserLikes(request.getUserId());
		
		if(!res.isOK()) 
			responseObserver.onError(errorCodeToStatus(res.error()));
		else {
			responseObserver.onNext( deleteUserLikesResult.newBuilder().build() );
			responseObserver.onCompleted();
		}
	}
}









