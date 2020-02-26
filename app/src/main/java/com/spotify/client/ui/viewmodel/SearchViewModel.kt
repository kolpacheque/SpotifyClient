package com.spotify.client.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.spotify.client.data.search.SearchResultItem
import com.spotify.client.data.search.SearchResultsCompleted

class SearchViewModel : ViewModel() {

    // region Search Request

    val searchRequest = MutableLiveData<ConsumableItem<String>>()

    fun onSearchRequest(request: String?) {
        searchRequest.value = ConsumableItem(request)
    }

    // endregion

    // region Search Results

    val searchResults = MutableLiveData<SearchResultsCompleted>()

    fun onSearchResults(results: SearchResultsCompleted?) {
        searchResults.value = results
    }

    // endregion

    // region Selected Item

    val selectedItem = MutableLiveData<ConsumableItem<SearchResultItem>>()

    fun onItemSelected(item: SearchResultItem?) {
        selectedItem.value = ConsumableItem(item)
    }

    // endregion

}