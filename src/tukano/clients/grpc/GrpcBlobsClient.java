package tukano.clients.grpc;


import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.google.protobuf.ByteString;

import io.grpc.ManagedChannelBuilder;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.impl.grpc.generated_java.BlobsGrpc;
import tukano.impl.grpc.generated_java.BlobsProtoBuf.*;

public class GrpcBlobsClient extends GrpcClient implements Blobs{

	
	final BlobsGrpc.BlobsBlockingStub stub;
	
	public GrpcBlobsClient(URI serverURI) {
		super(serverURI);
		var channel = ManagedChannelBuilder.forAddress(serverURI.getHost(), serverURI.getPort()).usePlaintext().build();
		stub = BlobsGrpc.newBlockingStub( channel ).withDeadlineAfter(GrpcClient.GRPC_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	private Result<Void> priv_upload(String blobId, byte[] bytes) {
		return super.toJavaResult( () -> {
			stub.upload(UploadArgs.newBuilder().setBlobId(blobId).setData(ByteString.copyFrom(bytes)).build());
			
			return null;
		});		
	}
	
	private Result<byte[]> priv_download(String blobId) {
		return super.toJavaResult( () -> {
			var res = stub.download(DownloadArgs.newBuilder().setBlobId(blobId).build());
			
			ByteString val = null;
			while(res.hasNext()) {
				ByteString temp = res.next().getChunk();
				if(val == null)
					val = temp;
				else
					val.concat(temp);
			}
			
			return val.toByteArray();
		});		
	}
	
	
	private Result<Void> priv_deleteShortBlobs(String shortId) {
		return super.toJavaResult( () -> {
			stub.deleteShortBlobs(deleteShortBlobsArgs.newBuilder().setShortId(shortId).build());
			
			return null;
		});		
	}
	
	
	@Override
	public Result<Void> upload(String blobId, byte[] bytes) {
		return super.reTry(() -> priv_upload(blobId, bytes));
	}

	@Override
	public Result<byte[]> download(String blobId) {
		return super.reTry(() -> priv_download(blobId));
	}

	@Override
	public Result<Void> deleteShortBlobs(String shortId) {
		return super.reTry(() -> priv_deleteShortBlobs(shortId));
	}
	
}
