package com.spotify.client.data.search

/**
 * Represents the intermediate or completed search results
 */
data class SearchResultsCompleted(
    val searchResultItems: List<SearchResultItem>,
    val completed: Boolean
)