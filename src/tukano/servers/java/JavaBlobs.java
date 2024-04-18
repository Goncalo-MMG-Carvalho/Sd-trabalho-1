package tukano.servers.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Logger;

import tukano.api.Blob;
import tukano.api.java.Blobs;
import tukano.api.java.Result;
import tukano.persistence.Hibernate;
import tukano.api.java.Result.ErrorCode;
import tukano.api.java.Shorts;
import tukano.clients.ShortClientFactory;


public class JavaBlobs implements Blobs {

	private static Logger Log = Logger.getLogger(JavaBlobs.class.getName());
		
	@Override
	public Result<Void> upload(String blobId, byte[] bytes) { // TODO
		Log.info("Wants to upload blobId: " + blobId);
		
		// verify if bloburl is valid
		Shorts sclient = ShortClientFactory.getShortsClient();
		Result<String> res = sclient.verifyBlobURI(blobId);
		
		String blobUrl = res.value();
		
		if(blobUrl.equals("")) { // "" is the sign that the uri is wrong
			Log.info("BlobId is invalid.");
			return Result.error(ErrorCode.FORBIDDEN);
		}
		
		
		var blobList = Hibernate.getInstance().sql("SELECT * FROM Blob b WHERE b.blobId = '" + blobId + "'", Blob.class);
		
		if(!blobList.isEmpty()) {
			byte[] currBytes = getBytesFromFile(blobId);
			if(!Arrays.equals(currBytes, bytes)) {
				Log.info("New bytes are different from bytes.");
				return Result.error( ErrorCode.CONFLICT);
			}
			Log.info("Was already uploaded");
			return Result.ok();
		}
		
		// create file and write to file
		try {
			Path outputPath = Paths.get("", blobId);
		
			Files.createFile(outputPath);
		    Files.write(outputPath, bytes); // Write the byte array to the file
		} 
		catch (IOException e) {
			Log.info("\n\n Error writing to file. \n\n");
		    //e.printStackTrace();
		}
		
		Blob b = new Blob(blobId, blobUrl);
		
		Hibernate.getInstance().persist(b);
		
		Log.info("Successful upload blob: " + blobId);
		return Result.ok();
	}

	@Override
	public Result<byte[]> download(String blobId) {
		Log.info("Wants to download blobId: " + blobId);
		
		var blobList = Hibernate.getInstance().sql("SELECT * FROM Blob b WHERE b.blobId = '" + blobId + "'", Blob.class);
		if(blobList.isEmpty()) {
			Log.info("Blob does not exist.");
			return Result.error(ErrorCode.NOT_FOUND);
		}
		
//		for (Blob blob : blobList) {
//			Log.info("Blob found: " + blob.getBlobId());
//		}
//		Log.info("");
		
		/*
		Shorts sclient = ShortClientFactory.getShortsClient();
		Result<String> res = sclient.verifyBlobURI(blobId);
		String blobUrl = res.value();
		if(blobUrl == null ) {
			
		}*/
		
		return Result.ok(getBytesFromFile(blobId));
	}
	
	
	
	private byte[] getBytesFromFile(String blobId) {
		byte[] fileContent = null;
		
		try {
			Path path = Paths.get("", blobId);
			fileContent = Files.readAllBytes(path);
		}
		catch (IOException e) {
			Log.info("\n\n Error reading file. \n\n");
			//e.printStackTrace();
		}
		
		
		return fileContent;
	}

	@Override
	public Result<Void> deleteShortBlobs(String shortId) {
		
		Log.info("Entrou no deleteshortsblobs ...");
		
		var blobList = Hibernate.getInstance().sql("SELECT * FROM Blob b WHERE b.blobId = 'blob."+ shortId + "'", Blob.class);
		Log.info("FOUND BLOB? " + shortId + ", "+ blobList.isEmpty());
		if(!blobList.isEmpty()) {	
			for (Blob b : blobList) {
				Log.info("Deleting blob: " + b.getBlobId());
				Hibernate.getInstance().delete(b);
			}
			
			
		}
		
		blobList = Hibernate.getInstance().sql("SELECT * FROM Blob b WHERE b.blobId = 'blob."+ shortId + "'", Blob.class);
		Log.info("DID DELETE BLOB? " + shortId + ", "+ blobList.isEmpty());
		
		
		return Result.ok();
	}
	
}