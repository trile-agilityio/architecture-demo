package com.architecture.component.db.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.architecture.component.db.dao.RepoDao;
import com.architecture.component.db.entity.Contributor;
import com.architecture.component.db.entity.Repo;
import com.architecture.component.db.entity.SearchResult;

/**
 * Main database description.
 */
@Database(entities = {Repo.class, SearchResult.class,
        Contributor.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "app-db";

    abstract public RepoDao repoDao();
}
