package tukano.clients;

import java.net.URI;

import tukano.api.java.Shorts;
import tukano.clients.rest.RestShortsClient;
import tukano.clients.grpc.GrpcShortsClient;
import tukano.discovery.Discovery;


public class ShortClientFactory {

	public static final String SERVICE = "shorts";
	
	private static final String REST = "/rest";
	private static final String GRPC = "/grpc";

	public static Shorts get(String serverURI) {

		if (serverURI.endsWith(REST))
			return new RestShortsClient(URI.create(serverURI));
		
		if (serverURI.endsWith(GRPC))
			return new GrpcShortsClient(URI.create(serverURI));
		
		throw new RuntimeException("We only support rest and grpc. uri = " + serverURI);
	}

	public static Shorts getShortsClient() {
		Discovery discovery = Discovery.getInstance();
		URI[] domainserviceURI = discovery.knownUrisOf(SERVICE, 1);
		
		return get(domainserviceURI[0].toString());
	}
}