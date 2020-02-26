package com.spotify.client.repo

import android.util.Log
import com.spotify.client.data.search.SearchResultItem
import com.spotify.client.data.search.SearchResultsCompleted
import com.spotify.client.persistence.search.SearchResultsDatabase
import com.spotify.client.spotify.SearchResponse
import com.spotify.client.spotify.SpotifyApi
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

interface OnErrorCallback {
    fun onError(error: Throwable)
}

class SearchRepository(
    retrofit: Retrofit,
    private val database: SearchResultsDatabase
) {

    companion object {
        const val TAG = "SearchRepository"
    }

    private val spotifyApi: SpotifyApi = retrofit.create(SpotifyApi::class.java)

    private val searchResultsObservable: Subject<SearchResultsCompleted> = PublishSubject.create()
    private val compositeDisposable = CompositeDisposable()

    fun getSearchResultsObservable(): Flowable<SearchResultsCompleted> = searchResultsObservable.toFlowable(BackpressureStrategy.BUFFER)

    /**
     * The Flowable in this case is just an error-less channel for data (search results)
     * The errors are being emitted through a separate channel (via callback)
     */
    fun search(request: String, errorCallback: OnErrorCallback? = null) {
        val searchRequest = request.toLowerCase(Locale.getDefault())

        // Dispose previous database/server fetching
        compositeDisposable.clear()

        // Fetch cached results from database
        compositeDisposable.add(searchInDatabase(searchRequest).subscribe(
            { searchResults -> searchResultsObservable.onNext(SearchResultsCompleted(searchResults, false)) },
            { throwable ->
                Log.e(TAG, "searchInDatabase()::Failed", throwable)
                errorCallback?.onError(throwable)
            },
            { Log.d(TAG, "searchInDatabase()::Completed") }))

        // Fetch up-to-date results from server
        compositeDisposable.add(searchOnServer(searchRequest).subscribe(
            { searchResults ->
                searchResultsObservable.onNext(SearchResultsCompleted(searchResults, true))
                Log.d(TAG, "searchOnServer()::Completed")
            },
            { throwable ->
                Log.e(TAG, "searchOnServer()::Failed", throwable)
                errorCallback?.onError(throwable)
                searchResultsObservable.onNext(SearchResultsCompleted(emptyList(), true))
            }
        ))

//        return Maybe.concat(
//            searchInDatabase(searchRequest)
//                .onErrorComplete { error ->
//                    errorCallback?.onError(error)
//                    true
//                },
//            searchOnServer(searchRequest)
//                .onErrorComplete { error ->
//                    errorCallback?.onError(error)
//                    true
//                })
    }

    fun teardown() {
        compositeDisposable.clear()

        database.close()
    }

    private fun searchInDatabase(request: String): Maybe<List<SearchResultItem>> {
        return database.getDao().getSearchResults(request)
            .subscribeOn(Schedulers.io())
            .map {
                it.results.map { result ->
                    SearchResultItem(
                        thumbnail = result.thumbnail,
                        name = result.name,
                        description = result.description,
                        link = result.link)
                }
            }
    }

    private fun searchOnServer(request: String): Single<List<SearchResultItem>> {
        return spotifyApi
            .searchSpotify(request, "track,artist")
            .subscribeOn(Schedulers.io())
            .map {
                val items = parseSearchResultsResponse(it)
                database.getDao().insertSearchRequest(request, items)
                items
            }
    }

    private fun parseSearchResultsResponse(response: SearchResponse): List<SearchResultItem> {
        val items = ArrayList<SearchResultItem>()
        items.addAll(response.artists.items.map {
            SearchResultItem(
                thumbnail = if (it.images.isEmpty()) "" else it.images[0].url,
                name = "Artist: ${it.name}",
                description = "Genres: ${it.genres.joinToString(separator = ", ")}",
                link = it.external_urls.spotify)
        })
        items.addAll(response.tracks.items.map {
            SearchResultItem(
                thumbnail = if (it.album.images.isEmpty()) "" else it.album.images[0].url,
                name = "Track: ${it.name}",
                description = "Artists: ${it.artists.joinToString(separator = ", ") { artist -> artist.name }}",
                link = it.external_urls.spotify)
        })
        return items
    }

}