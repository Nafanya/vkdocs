package io.github.nafanya.vkdocs.domain.model;

public class DocumentsInfo {
    public int totalFiles;
    public long totalSize;

    public DocumentsInfo(int totalFiles, long totalSize) {
        this.totalFiles = totalFiles;
        this.totalSize = totalSize;
    }
}
