package tukano.clients;

import java.net.URI;

import tukano.api.java.Blobs;
import tukano.clients.rest.RestBlobsClient;
import tukano.discovery.Discovery;


public class BlobClientFactory {

	public static final String SERVICE = "blobs";
	
	private static final String REST = "/rest";
	//private static final String GRPC = "/grpc"; //TODO

	public static Blobs get(String serverURI) {

		if (serverURI.endsWith(REST))
			return new RestBlobsClient(URI.create(serverURI));
		
//		if (serverURI.endsWith(GRPC)) 				//TODO
//			return new GrpcUsersClient(serverURI);
		
		throw new RuntimeException("Unknown service type..." + serverURI);
	}

	public static Blobs getBlobsClient() {
		Discovery discovery = Discovery.getInstance();
		URI[] domainserviceURI = discovery.knownUrisOf(SERVICE, 1);
		int numberOfServices = domainserviceURI.length;
		
		/*if (numberOfServices == 0)
			throw new RuntimeException("No service found for " + SERVICE);*/
		int rand = (int)(Math.random() * (numberOfServices - 1));
		return BlobClientFactory.get(domainserviceURI[rand].toString());
	}
}