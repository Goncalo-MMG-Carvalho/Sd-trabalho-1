package tukano.clients;

import java.net.URI;

import tukano.api.java.Blobs;
import tukano.clients.grpc.GrpcBlobsClient;
import tukano.clients.rest.RestBlobsClient;
import tukano.discovery.Discovery;


public class BlobClientFactory {

	public static final String SERVICE = "blobs";
	
	private static final String REST = "/rest";
	private static final String GRPC = "/grpc"; 

	public static Blobs get(String serverURI) {

		if (serverURI.endsWith(REST))
			return new RestBlobsClient(URI.create(serverURI));
		
		if (serverURI.endsWith(GRPC)) 				
			return new GrpcBlobsClient(URI.create(serverURI));
		
		throw new RuntimeException("We only support rest and grpc. uri = " + serverURI);
	}

	public static Blobs getBlobsClient() {
		Discovery discovery = Discovery.getInstance();
		URI[] domainserviceURI = discovery.knownUrisOf(SERVICE, 1);
		
		int rand = (int)((Math.random()-0.001) * (domainserviceURI.length));
		return get(domainserviceURI[rand].toString());
	}
}