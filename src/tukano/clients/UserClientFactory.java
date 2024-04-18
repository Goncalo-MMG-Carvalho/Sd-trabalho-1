package tukano.clients;

import java.net.URI;

import tukano.api.java.Users;
import tukano.clients.grpc.GrpcUsersClient;
import tukano.clients.rest.RestUsersClient;
import tukano.discovery.Discovery;


public class UserClientFactory {

	public static final String SERVICE = "users";
	
	private static final String REST = "/rest";
	private static final String GRPC = "/grpc";

	public static Users get(String serverURI) {

		if (serverURI.endsWith(REST))
			return new RestUsersClient(URI.create(serverURI));
		
		if (serverURI.endsWith(GRPC))
			return new GrpcUsersClient(URI.create(serverURI));
		
		throw new RuntimeException("We only support rest and grpc. uri = " + serverURI);
	}

	public static Users getUsersClient() {
		Discovery discovery = Discovery.getInstance();
		URI[] domainserviceURI = discovery.knownUrisOf(SERVICE, 1);
		
		return get(domainserviceURI[0].toString());
	}
}