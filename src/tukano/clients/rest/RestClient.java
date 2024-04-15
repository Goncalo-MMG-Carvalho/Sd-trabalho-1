package tukano.clients.rest;

import static tukano.api.java.Result.*;

import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;

//import java.io.IOException; //TODO
//import java.net.InetAddress;
import java.net.URI;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

public class RestClient {
	private static Logger Log = Logger.getLogger(RestClient.class.getName());

	protected static final int READ_TIMEOUT = 5555;
	protected static final int CONNECT_TIMEOUT = 5555;

	protected static final int MAX_RETRIES = 10;
	protected static final int RETRY_SLEEP = 3333;

	final URI serverURI;
	protected final Client client;
	final ClientConfig config;

	protected RestClient(URI serverURI) {
		this.serverURI = serverURI;
		this.config = new ClientConfig();

		config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
		config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

		this.client = ClientBuilder.newClient(config);
	}

	protected <T> Result<T> reTry(Supplier<Result<T>> func) {
    	for (int i = 0; i < MAX_RETRIES; i++)
    		try {
    			return func.get();
    		} 
    		catch (ProcessingException x) {
    			Log.info("Going to sleep for" + RETRY_SLEEP + "ms ...");
    			mySleep(RETRY_SLEEP);
    			Log.info("Now retrying.");
    			
    		} 
    		catch (Exception x) {
    			x.printStackTrace();
    			return Result.error(ErrorCode.INTERNAL_ERROR);
    		}
    	return Result.error(ErrorCode.TIMEOUT);
    }

    protected <T> Result<T> toJavaResult(Response r, Class<T> entityType) {
    	try {
    		var status = r.getStatusInfo().toEnum();
    		if (status == Status.OK && r.hasEntity())
    			return ok(r.readEntity(entityType));
    		else 
    			if( status == Status.NO_CONTENT) return ok();
    		
    		return error(getErrorCodeFrom(status.getStatusCode()));
    	} 
    	finally {
    		r.close();
    	}
    }

	protected <T> Result<T> toJavaResult(Response r, GenericType<T> entityType) {
		try {
			var status = r.getStatusInfo().toEnum();
			if (status == Status.OK && r.hasEntity())
				return ok(r.readEntity(entityType));

			if (status == Status.NO_CONTENT)
				return ok();

			return error(getErrorCodeFrom(status.getStatusCode()));
		} 
		finally {
			r.close();
		}
	}

	public static ErrorCode getErrorCodeFrom(int status) {
		return switch (status) {
			case 200, 209 -> ErrorCode.OK;
			case 409 -> ErrorCode.CONFLICT;
			case 403 -> ErrorCode.FORBIDDEN;
			case 404 -> ErrorCode.NOT_FOUND;
			case 400 -> ErrorCode.BAD_REQUEST;
			case 500 -> ErrorCode.INTERNAL_ERROR;
			case 501 -> ErrorCode.NOT_IMPLEMENTED;
			default -> ErrorCode.INTERNAL_ERROR;
		};
	}

	@Override
	public String toString() {
		return serverURI.toString();
	}

	private void mySleep(int ms) {
		try {
			Thread.sleep(ms);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}