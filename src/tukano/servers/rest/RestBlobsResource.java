package tukano.servers.rest;

import tukano.api.java.Blobs;
import tukano.api.rest.RestBlobs;
import tukano.servers.java.JavaBlobs;

public class RestBlobsResource extends BigBoyRest implements RestBlobs {

	final Blobs impl;
	
	public RestBlobsResource() {
		this.impl = new JavaBlobs();
	}
	
	@Override
	public void upload(String blobId, byte[] bytes) {
		super.fromJavaResult(impl.upload(blobId, bytes));
	}

	@Override
	public byte[] download(String blobId) {
		return super.fromJavaResult(impl.download(blobId));
	}
    
}