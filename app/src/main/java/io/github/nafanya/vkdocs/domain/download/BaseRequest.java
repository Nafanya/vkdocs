package io.github.nafanya.vkdocs.domain.download;


public class BaseRequest {
    public interface RequestObserver {
        void onProgress(int percentage);
        void onComplete();
        void onError(Exception e);
        void onInfiniteProgress();
    }

    private int id;
    private String url;
    private String dest;
    private long bytes;
    private long totalBytes;

    public BaseRequest(String url, String dest) {
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

    public String getUrl() {
        return url;
    }

    public String getDest() {
        return dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseRequest request = (BaseRequest) o;
        return id == request.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
