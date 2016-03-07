package io.github.nafanya.vkdocs.domain.download.base;


public class BaseDownloadRequest {

    private int id;
    private String url;
    private String dest;
    private long bytes;
    private long totalBytes;
    private int docId;

    public BaseDownloadRequest(String url, String dest) {
        this.url = url;
        this.dest = dest;
    }

    public long getBytes() {
        return bytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getUrl() {
        return url;
    }

    public String getDest() {
        return dest;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseDownloadRequest request = (BaseDownloadRequest) o;
        return id == request.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }
}
