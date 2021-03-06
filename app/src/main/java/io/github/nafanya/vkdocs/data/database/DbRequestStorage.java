package io.github.nafanya.vkdocs.data.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.DownloadRequestEntity;
import io.github.nafanya.vkdocs.net.impl.download.DownloadRequest;
import io.github.nafanya.vkdocs.net.base.download.RequestStorage;

public class DbRequestStorage implements RequestStorage<DownloadRequest> {
    private Mapper<DownloadRequestEntity, DownloadRequest> mapper;

    public DbRequestStorage(Mapper<DownloadRequestEntity, DownloadRequest> mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<DownloadRequest> getAll() {
        return mapper.transform(SQLite.select().from(DownloadRequestEntity.class).queryList());
    }

    @Override
    public void update(DownloadRequest request) {
        mapper.transformInv(request).update();
    }

    @Override
    public void insert(DownloadRequest request) {
        DownloadRequestEntity downloadRequestEntity = mapper.transformInv(request);
        downloadRequestEntity.insert();
        request.setId(downloadRequestEntity.getId());
    }

    @Override
    public void delete(DownloadRequest request) {
        mapper.transformInv(request).delete();
    }
}
