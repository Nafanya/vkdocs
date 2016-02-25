package io.github.nafanya.vkdocs.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.vk.sdk.api.model.VKApiDocument;

import io.github.nafanya.vkdocs.data.database.DocumentsDatabase;

@Table(database = DocumentsDatabase.class)
public class VKDocument extends BaseModel {
    public static int DELETED = 1;
    public static int SYNCHRONIZED = 0;

    @PrimaryKey
    private int id;

    @Column
    private String title;

    /*
    sync == 0, обычный документ
    sync == 1, если документ удален офлайн и это еще не синхронизировано с сервером
     */
    @Column
    private int sync;

    @Column
    private int ownerId;

    public VKDocument() {}

    public VKDocument(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public VKDocument(VKApiDocument vkDoc) {
        this.id = vkDoc.id;
        this.title = vkDoc.title;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
