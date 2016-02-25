package io.github.nafanya.vkdocs.data.database;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import io.github.nafanya.vkdocs.data.Mapper;
import io.github.nafanya.vkdocs.data.database.model.DownloadRequestEntity;
import io.github.nafanya.vkdocs.domain.download.DownloadRequest;
import io.github.nafanya.vkdocs.domain.download.base.RequestStorage;

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
    public void add(DownloadRequest request) {
        mapper.transformInv(request).insert();
    }

    @Override
    public void delete(DownloadRequest request) {
        mapper.transformInv(request).delete();
    }
}
