package io.github.nafanya.vkdocs.domain.download;

import java.util.List;

public interface RequestStorage {
    /*
    Request:
        id
        url
        dest
        bytes
        total_bytes
    */

    List<BaseRequest> getAll();
    void update(BaseRequest request);
    void add(BaseRequest request);
    void delete(BaseRequest request);
}
