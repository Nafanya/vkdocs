package io.github.nafanya.vkdocs.data.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.vk.sdk.api.model.VKApiDocument;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.DownloadRequest;
import io.github.nafanya.vkdocs.data.database.model.VKDocument;
import io.github.nafanya.vkdocs.domain.download.BaseRequest;
import io.github.nafanya.vkdocs.domain.download.DownloadManager;
import io.github.nafanya.vkdocs.domain.download.RequestStorage;

public class RequestStorageImpl implements RequestStorage {
    private Mapper<DownloadRequest, BaseRequest> mapper;

    @Override
    public List<BaseRequest> getAll() {
        return mapper.transform(SQLite.select().from(DownloadRequest.class).queryList());
    }

    @Override
    public void update(BaseRequest request) {

    }

    @Override
    public void add(BaseRequest request) {

    }

    @Override
    public void delete(BaseRequest request) {

    }
}
