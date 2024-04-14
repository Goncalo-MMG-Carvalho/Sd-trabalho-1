package tukano.clients;

import java.net.URI;

import tukano.api.java.Users;
import tukano.clients.rest.RestUsersClient;
import tukano.discovery.Discovery;


public class UserClientFactory {

	public static final String SERVICE = "UsersService";
	
	private static final String REST = "/rest";
	//private static final String GRPC = "/grpc"; //TODO

	public static Users get(String serverURI) {

		if (serverURI.endsWith(REST))
			return new RestUsersClient(URI.create(serverURI));
		
//		if (serverURI.endsWith(GRPC)) 				//TODO
//			return new GrpcUsersClient(serverURI);
		
		throw new RuntimeException("Unknown service type..." + serverURI);
	}

	public static Users getUsersClient() {
		Discovery discovery = Discovery.getInstance();
		URI[] domainserviceURI = discovery.knownUrisOf(SERVICE, 1);
		
		return UserClientFactory.get(domainserviceURI[0].toString());
	}
}