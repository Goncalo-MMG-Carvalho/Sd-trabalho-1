package tukano.clients;

import java.net.URI;

import tukano.api.java.Shorts;
import tukano.clients.rest.RestShortsClient;
import tukano.discovery.Discovery;


public class ShortClientFactory {

	public static final String SERVICE = "shorts";
	
	private static final String REST = "/rest";
	//private static final String GRPC = "/grpc"; //TODO

	public static Shorts get(String serverURI) {

		if (serverURI.endsWith(REST))
			return new RestShortsClient(URI.create(serverURI));
		
//		if (serverURI.endsWith(GRPC)) 				//TODO
//			return new GrpcUsersClient(serverURI);
		
		throw new RuntimeException("Unknown service type..." + serverURI);
	}

	public static Shorts getShortsClient() {
		Discovery discovery = Discovery.getInstance();
		URI[] domainserviceURI = discovery.knownUrisOf(SERVICE, 1);
		
		return ShortClientFactory.get(domainserviceURI[0].toString());
	}
}