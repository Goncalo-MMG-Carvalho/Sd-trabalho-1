package tukano.api;

public class Blob {
    private String blobId;
    private String filePath;

    public Blob() {
    }

    public Blob(String blobId, String filePath) {
        this.blobId = blobId;
        this.filePath = filePath;
    }

    public String getBlobId() {
        return blobId;
    }

    public void setBlobId(String blobId) {
        this.blobId = blobId;
    }

    public String getFilePath() {
        return filePath;
    }   

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
