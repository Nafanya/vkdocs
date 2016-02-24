package io.github.nafanya.vkdocs.data.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = DocumentsDatabase.NAME, version = DocumentsDatabase.VERSION)
public class DocumentsDatabase {
    public static final String NAME = "DocumentsDatabase_db";

    public static final int VERSION = 1;
}
