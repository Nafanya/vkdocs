package io.github.nafanya.vkdocs.data;

import com.vk.sdk.api.model.VKApiDocument;

public interface ServerSideNetworkRepository {
    void deleteSS(VKApiDocument doc);
    void addSS(VKApiDocument doc);
}
