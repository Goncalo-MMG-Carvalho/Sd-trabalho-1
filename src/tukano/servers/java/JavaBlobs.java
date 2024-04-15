package tukano.servers.java;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Blob;
import java.util.Arrays;
import java.util.logging.Logger;


//import tukano.api.Blob;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.persistence.Hibernate;
import tukano.api.java.Result.ErrorCode;


public class JavaBlobs implements Blobs {

	private static Logger Log = Logger.getLogger(JavaBlobs.class.getName());
		
	@Override
	public Result<Void> upload(String blobId, byte[] bytes) { // TODO
		Log.info("Wants to upload blobId: " + blobId);
		
		// TODO verify if bloburl is valid
		/*
		Shorts sclient = ShortsClientFactory.getShortsClient();
		Result<Boolean> res = sclient.verifyBlobURI(blobId);
		
		boolean isValid = res.value();
		
		if(!isValid) {
			Log.info("BlobId is invalid.");
			return Result.error(errorCode.FORBIDDEN);
		}
		*/
		
		var blobList = Hibernate.getInstance().sql("SELECT * FROM Blobs b WHERE b.blobId = '" + blobId + "'", Blob.class);
		
		String blobUrl = JavaShorts.generateBlobUrl(blobId);
		
		if(!blobList.isEmpty()) {
			byte[] currBytes = getBytesFromFile(blobUrl);
			if(!Arrays.equals(currBytes, bytes)) {
				Log.info("New bytes are different from bytes.");
				return Result.error( ErrorCode.CONFLICT);
			}
			
			return Result.ok();
		}
		
		
		// TODO create file and write to file
		
		
		return Result.ok();
	}

	@Override
	public Result<byte[]> download(String blobId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	private byte[] getBytesFromFile(String url) {
		File file = new File(url);
		byte[] fileContent = null;
		
		try {
			fileContent = Files.readAllBytes(file.toPath());
		}
		catch (IOException e) {
			Log.info("Error reading file.");
			e.printStackTrace();
		}
		
		
		return fileContent;
	}
	
}