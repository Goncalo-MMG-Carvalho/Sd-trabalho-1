package tukano.servers.grpc;

import static tukano.servers.grpc.GrpcUsersServerStub.errorCodeToStatus;

import com.google.protobuf.ByteString;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;
import io.grpc.stub.StreamObserver;
import tukano.api.java.Blobs;
import tukano.servers.java.JavaBlobs;

import tukano.impl.grpc.generated_java.BlobsGrpc;
import tukano.impl.grpc.generated_java.BlobsProtoBuf.*;

public class GrpcBlobsServerStub implements BlobsGrpc.AsyncService, BindableService {
	
	Blobs impl = new JavaBlobs();
	
	 @Override 
	 public final ServerServiceDefinition bindService() {
	      return BlobsGrpc.bindService(this);
	 }
	 
	 
	 
	public void upload(UploadArgs request, StreamObserver<UploadResult> responseObserver) {
		var res = impl.upload( request.getBlobId(), request.getData().toByteArray() );	
			
		if(!res.isOK()) 
				responseObserver.onError(errorCodeToStatus(res.error()));
		else {
				responseObserver.onNext( UploadResult.newBuilder().build());
				responseObserver.onCompleted();
		}
	}
	
	public void download(DownloadArgs request, StreamObserver<DownloadResult> responseObserver) {
		var res = impl.download( request.getBlobId() );	
			
		if(!res.isOK()) 
				responseObserver.onError(errorCodeToStatus(res.error()));
		else {
				responseObserver.onNext( DownloadResult.newBuilder().setChunk( ByteString.copyFrom( res.value() ) ).build());
				responseObserver.onCompleted();
		}
	}
	
	public void deleteShortBlobs(deleteShortBlobsArgs request, StreamObserver<deleteShortBlobsResult> responseObserver) {
		var res = impl.deleteShortBlobs( request.getShortId() );	
			
		if(!res.isOK()) 
				responseObserver.onError(errorCodeToStatus(res.error()));
		else {
				responseObserver.onNext( deleteShortBlobsResult.newBuilder().build());
				responseObserver.onCompleted();
		}
	}
	
	
	
	
}