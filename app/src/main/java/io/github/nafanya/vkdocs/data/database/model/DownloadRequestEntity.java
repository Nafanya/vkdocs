package io.github.nafanya.vkdocs.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import io.github.nafanya.vkdocs.data.database.DocumentsDatabase;

@Table(database = DocumentsDatabase.class)
public class DownloadRequestEntity extends BaseModel {

    @PrimaryKey
    private int id;

    @Column
    private String url;

    @Column
    private String dest;

    @Column
    private long bytes;

    @Column
    private long totalBytes;

    public DownloadRequestEntity() {
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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(long bytes) {
        this.bytes = bytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }
}
