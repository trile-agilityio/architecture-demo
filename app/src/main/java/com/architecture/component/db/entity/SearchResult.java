package com.architecture.component.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import com.architecture.component.db.Converter.GithubTypeConverters;

import java.util.List;

@Entity(primaryKeys = {"query"})
@TypeConverters(GithubTypeConverters.class)
public class SearchResult {

    public final String query;
    public final List<Integer> repoIds;
    public final int totalCount;

    @Nullable
    public final Integer next;

    public SearchResult(String query, List<Integer> repoIds, int totalCount, Integer next) {
        this.query = query;
        this.repoIds = repoIds;
        this.totalCount = totalCount;
        this.next = next;
    }
}