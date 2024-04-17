package tukano.clients.grpc;

import static tukano.impl.grpc.common.DataModelAdaptor.GrpcUser_to_User;
import static tukano.impl.grpc.common.DataModelAdaptor.User_to_GrpcUser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannelBuilder;
import tukano.impl.grpc.generated_java.UsersProtoBuf.CreateUserArgs;
import tukano.impl.grpc.generated_java.UsersProtoBuf.GetUserArgs;
import tukano.impl.grpc.generated_java.UsersProtoBuf.UpdateUserArgs;
import tukano.impl.grpc.generated_java.UsersProtoBuf.DeleteUserArgs;
import tukano.impl.grpc.generated_java.UsersProtoBuf.SearchUserArgs;
import tukano.impl.grpc.generated_java.UsersGrpc;
import tukano.api.User;
import tukano.api.java.Result;
import tukano.api.java.Users;

public class GrpcUsersClient extends GrpcClient implements Users {

	final UsersGrpc.UsersBlockingStub stub;
	
	
	protected GrpcUsersClient(URI serverURI) {
		super(serverURI);
		var channel = ManagedChannelBuilder.forAddress(serverURI.getHost(), serverURI.getPort()).usePlaintext().build();
		stub = UsersGrpc.newBlockingStub( channel ).withDeadlineAfter(GrpcClient.GRPC_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
	}
	
	
	
	private Result<String> priv_createUser(User user) {
		return toJavaResult(() -> {
			var res = stub.createUser(CreateUserArgs.newBuilder()
				.setUser(User_to_GrpcUser(user))
				.build());
			return res.getUserId();
		});
	}
	
	private Result<User> priv_getUser(String userId, String pwd) {
		return toJavaResult(() -> {
			var res = stub.getUser(GetUserArgs.newBuilder().setUserId(userId).setPassword(pwd).build());
			return GrpcUser_to_User(res.getUser());
		});
	}
	
	private Result<User> priv_updateUser(String userId, String pwd, User user) {
		return toJavaResult(() -> {
			var res = stub.updateUser(UpdateUserArgs.newBuilder().setUserId(userId).setPassword(pwd).setUser(User_to_GrpcUser(user)).build());
			return GrpcUser_to_User(res.getUser());
		});
	}
	
	private Result<User> priv_deleteUser(String userId, String pwd) {
		return toJavaResult(() -> {
			var res = stub.deleteUser(DeleteUserArgs.newBuilder().setUserId(userId).setPassword(pwd).build());
			return GrpcUser_to_User(res.getUser());
		});
	}
	
	private Result<List<User>> priv_searchUsers(String pattern) {
		return toJavaResult(() -> {
			var res = stub.searchUsers(SearchUserArgs.newBuilder().setPattern(pattern).build());
			List<User> l = new ArrayList<>();
			
//			for(; res.hasNext(); ) {
//				GrpcUser u = res.next();
//				l.add(GrpcUser_to_User(u));
//			}
			
			res.forEachRemaining((u) -> {
				l.add(GrpcUser_to_User(u));
			});
			
			return l;
		});
	}
	
	

	@Override
	public Result<String> createUser(User user) {
		return super.reTry(() -> priv_createUser(user));
	}

	@Override
	public Result<User> getUser(String userId, String pwd) {
		return super.reTry(() -> priv_getUser(userId, pwd));
	}

	@Override
	public Result<User> updateUser(String userId, String pwd, User user) {
		return super.reTry(() -> priv_updateUser(userId, pwd, user));
	}

	@Override
	public Result<User> deleteUser(String userId, String pwd) {
		return super.reTry(() -> priv_deleteUser(userId, pwd));
	}

	@Override
	public Result<List<User>> searchUsers(String pattern) {
		return super.reTry(() -> priv_searchUsers(pattern));
	}
}
