package com.spotify.client.persistence.search

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [SearchRequest::class, SearchResult::class], version = 1, exportSchema = false)
abstract class SearchResultsDatabase : RoomDatabase() {

    companion object {
        private const val DB_NAME = "search_results.db";

        fun get(context: Context): SearchResultsDatabase {
            return Room.databaseBuilder(context, SearchResultsDatabase::class.java, DB_NAME).build()
        }
    }

    abstract fun getDao(): SearchResultDao

}