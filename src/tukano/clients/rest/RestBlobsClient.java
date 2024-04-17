package tukano.clients.rest;

import tukano.api.java.Result;
import tukano.api.java.Blobs;
import tukano.api.rest.RestBlobs;

import java.net.URI;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class RestBlobsClient extends RestClient implements Blobs {

	final WebTarget target;
	
    public RestBlobsClient(URI serverURI) {
        super(serverURI);
        target = client.target(serverURI).path(RestBlobs.PATH);
    }

    
    private Result<Void> priv_upload(String blobId, byte[] bytes) {
    	return super.toJavaResult(
    			target.path(blobId).request()
    				.post(Entity.entity(bytes, MediaType.APPLICATION_OCTET_STREAM)) , Void.class );
    }
    
    private Result<byte[]> priv_download(String blobId) {
    	return super.toJavaResult(
    			target.path(blobId)
    				.request()
    				.accept(MediaType.APPLICATION_OCTET_STREAM) 
    				.get() , byte[].class );
    }
    
    
    
    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
    	return super.reTry( () -> priv_upload(blobId, bytes));
    	
    	//throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }

    @Override
    public Result<byte[]> download(String blobId) {
    	return super.reTry( () -> priv_download(blobId));
    	
    	//throw new UnsupportedOperationException("Unimplemented method 'download'");
    }


	@Override
	public Result<Void> deleteShortBlobs(String shortId) {
		return super.reTry( () -> priv_deleteShortBlobs(shortId));
	}


	private Result<Void> priv_deleteShortBlobs(String shortId) {
		return super.toJavaResult(
    			target.path(shortId).request()
    				.delete(), Void.class );
	}

}