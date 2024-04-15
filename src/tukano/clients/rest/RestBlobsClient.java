package tukano.clients.rest;

import tukano.api.Blob;
import tukano.api.java.Result;
import tukano.api.java.Blobs;
import tukano.api.rest.RestBlobs;

import java.net.URI;
import java.util.List;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.GenericType;

public class RestBlobsClient extends RestClient implements Blobs {

    protected RestBlobsClient(URI serverURI) {
        super(serverURI);
        //TODO Auto-generated constructor stub
    }

    @Override
    public Result<Void> upload(String blobId, byte[] bytes) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'upload'");
    }

    @Override
    public Result<byte[]> download(String blobId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'download'");
    }

}