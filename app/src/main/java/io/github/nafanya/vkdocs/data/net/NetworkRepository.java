package io.github.nafanya.vkdocs.data.net;


import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

public interface NetworkRepository {
    List<VKApiDocument> getMyDocuments() throws Exception;
    void delete(VKApiDocument document) throws Exception;
}
