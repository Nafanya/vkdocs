package io.github.nafanya.vkdocs.domain.model;

import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.domain.download.DownloadRequest;

public class VkDocument extends VKApiDocument {
    public static int NONE = 0;
    public static int OFFLINE = 1;
    public static int CACHED = 2;
    public static int OFFLINE_IN_PROGRESS = 3;
    public static int CACHE_IN_PROGRESS = 4;

    private DownloadRequest downloadRequest;
    private int offlineType;

    public VkDocument() {}
    public VkDocument(VKApiDocument doc) {
        id = doc.id;
        owner_id = doc.owner_id;
        title = doc.title;
        size = doc.size;
        ext = doc.ext;
        url = doc.url;
        photo_100 = doc.photo_100;
        photo_130 = doc.photo_130;
        photo = doc.photo;
        access_key = doc.access_key;
    }

    public DownloadRequest getRequest() {
        return downloadRequest;
    }

    public boolean isOffline() {
        return offlineType == OFFLINE;
    }

    public boolean isOfflineInProgress() {
        return offlineType == OFFLINE_IN_PROGRESS;
    }

    public boolean isDownloading() {
        return downloadRequest != null;
    }

    public void resetRequest() {
        downloadRequest = null;
    }
}
