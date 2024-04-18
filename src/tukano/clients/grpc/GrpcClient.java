package tukano.clients.grpc;

import java.net.URI;
import java.util.function.Supplier;
//import java.util.logging.Logger;

import org.checkerframework.checker.units.qual.t;

import io.grpc.StatusRuntimeException;
import io.grpc.Status;
import io.grpc.Status.Code;
import jakarta.ws.rs.ProcessingException;
import tukano.api.java.Result;
import tukano.api.java.Result.ErrorCode;

public class GrpcClient {

//	private static Logger Log = Logger.getLogger(GrpcClient.class.getName());

	protected static final int READ_TIMEOUT = 2000;
	protected static final int CONNECT_TIMEOUT = 2000;

	
	protected static final long GRPC_REQUEST_TIMEOUT = 2000;
	
	protected static final int MAX_RETRIES = 7; // CR7 SIIIIIII
	protected static final int RETRY_SLEEP = 2000;
	
	final URI serverURI;
	
	public GrpcClient(URI serverURI) { 
		this.serverURI = serverURI; 
	}

	protected <T> Result<T> reTry(Supplier<Result<T>> func) {
    	for (int i = 0; i < MAX_RETRIES; i++)
    		try {
    			return func.get();
    		} 
    		catch (ProcessingException x) {
//    			Log.info("Going to sleep for" + RETRY_SLEEP + "ms ...");
    			mySleep(RETRY_SLEEP);
//    			Log.info("Now retrying.");
    			
    		} 
    		catch (Exception x) {
    			x.printStackTrace();
    			return Result.error(ErrorCode.INTERNAL_ERROR);
    		}
    	return Result.error(ErrorCode.TIMEOUT);
    }
	
	
	protected <T> Result<T> toJavaResult(Supplier<T> func) {
		try {
			return Result.ok(func.get());
		} 
		catch(StatusRuntimeException sre) {
			var code = sre.getStatus().getCode();
			if( code == Code.UNAVAILABLE || code == Code.DEADLINE_EXCEEDED )
				throw sre;
			return Result.error( statusToErrorCode( sre.getStatus() ) );
		}
    }
	
//	public Result<Void> toJavaResult(Supplier<t> func) {
//		try {
//			func.get();
//			return Result.ok();
//		} 
//		catch(StatusRuntimeException sre) {
//			var code = sre.getStatus().getCode();
//			if( code == Code.UNAVAILABLE || code == Code.DEADLINE_EXCEEDED )
//				throw sre;
//			return Result.error( statusToErrorCode( sre.getStatus() ) );
//		}
//	}
	
	
	static ErrorCode statusToErrorCode( Status status ) {
    	return switch( status.getCode() ) {
    		case OK -> ErrorCode.OK;
    		case NOT_FOUND -> ErrorCode.NOT_FOUND;
    		case ALREADY_EXISTS -> ErrorCode.CONFLICT;
    		case PERMISSION_DENIED -> ErrorCode.FORBIDDEN;
    		case INVALID_ARGUMENT -> ErrorCode.BAD_REQUEST;
    		case UNIMPLEMENTED -> ErrorCode.NOT_IMPLEMENTED;
    		default -> ErrorCode.INTERNAL_ERROR;
    	};
    }
	
	
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
