package com.architecture.component.db.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.architecture.component.db.dao.RepoDao;
import com.architecture.component.db.entity.Repo;
import com.architecture.component.db.entity.SearchResult;

/**
 * Main database description.
 */
@Database(entities = {Repo.class, SearchResult.class}, version = 1)
public abstract class GithubDb extends RoomDatabase {

    abstract public RepoDao repoDao();
}
