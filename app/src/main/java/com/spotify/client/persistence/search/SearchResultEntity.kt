package com.spotify.client.persistence.search

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Relation

@Entity(tableName = "searchRequest")
data class SearchRequest(
    @PrimaryKey val id: String
)

@Entity(tableName = "searchResult")
data class SearchResult(
    val request: String,
    val thumbnail: String,
    val name: String,
    val description: String,
    val link: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

data class SearchResultsForRequest(
    @Embedded val request: SearchRequest
) {
    @Relation(
        parentColumn = "id",
        entityColumn = "request"
    )
    lateinit var results: List<SearchResult>
}