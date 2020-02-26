package com.spotify.client.persistence.search

import android.arch.persistence.room.*
import com.spotify.client.data.search.SearchResultItem
import io.reactivex.Maybe

@Dao
abstract class SearchResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRequest(request: SearchRequest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertResults(results: List<SearchResult>)

    @Transaction
    @Query("SELECT * FROM searchRequest WHERE id = :request")
    abstract fun getSearchResults(request: String): Maybe<SearchResultsForRequest>

    @Query("DELETE FROM searchResult WHERE request = :request")
    abstract fun deleteResults(request: String)

    fun insertSearchRequest(request: String, results: List<SearchResultItem>) {
        insertRequest(SearchRequest(request))

        // Clear previous search results for this request...
        deleteResults(request)

        // ...and set new search results for this request
        val entities = results.map { SearchResult(request, it.thumbnail, it.name, it.description, it.link) }
        insertResults(entities)
    }

}