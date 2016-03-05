package io.github.nafanya.vkdocs.domain.model;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.domain.download.DownloadRequest;

public class DownloadableDocument {
    private VKApiDocument doc;
    private DownloadRequest request;

    public DownloadableDocument(VKApiDocument doc, DownloadRequest request) {
        this.doc = doc;
        this.request = request;
    }

    public VKApiDocument getDoc() {
        return doc;
    }

    public DownloadRequest getRequest() {
        return request;
    }
}
