package tukano.clients;

import java.net.URI;
import java.util.function.Function;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import tukano.api.java.Result;
import tukano.api.java.Users;
import tukano.discovery.Discovery;


public class UserClientFactory {

	private static final String REST = "/rest";
	private static final String GRPC = "/grpc";

	public static Users get(String serverURI) {

		if (serverURI.endsWith(REST))
			return new RestUsersClient(serverURI);
		
		if (serverURI.endsWith(GRPC))
			return new SoapUsersClient(serverURI);
		
		throw new RuntimeException("Unknown service type..." + serverURI);
	}

	public static Users getUsersClient(String domain) {
		Discovery discovery = Discovery.getInstance();
		URI[] domainserviceURI = discovery.knownUrisOf(domain+":users", 1);
		
		return UserClientFactory.get(domainserviceURI[0].toString());
	}
}