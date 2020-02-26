package com.spotify.client.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.spotify.client.R
import com.spotify.client.repo.OnErrorCallback
import com.spotify.client.repo.SearchRepository
import com.spotify.client.data.search.SearchResultsCompleted
import com.spotify.client.ui.search.DetailsFragment
import com.spotify.client.ui.viewmodel.SearchViewModel
import com.spotify.client.util.NetworkStateReceiver
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var navController: NavController
    private lateinit var viewModel: SearchViewModel

    @Inject
    lateinit var searchRepo: SearchRepository

    private lateinit var networkStateReceiver: NetworkStateReceiver
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        createUtils()
        setupUI()
    }

    override fun onDestroy() {
        teardown()

        super.onDestroy()
    }

    private fun createUtils() {
        navController = findNavController(R.id.nav_host_fragment)
        viewModel = ViewModelProviders.of(this)[SearchViewModel::class.java]

        networkStateReceiver = NetworkStateReceiver()
        // TODO: Switch to requestNetwork()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkStateReceiver, intentFilter)
        compositeDisposable.add(networkStateReceiver.networkAvailableObservable.subscribe { networkAvailable ->
            if (networkAvailable) {
                // Retry search for the last submitted request
                viewModel.searchRequest.value?.peek()?.let {
                    search(it)
                }
            }
            showSnackbar(if (networkAvailable) "You're online" else "You're offline")
        })

        compositeDisposable.add(searchRepo.getSearchResultsObservable().subscribe(
            { resultsCompleted -> updateResults(resultsCompleted) },
            { throwable -> Log.e(TAG, "search()::Failed", throwable) },
            { Log.d(TAG, "search()::Completed") }))
    }

    private fun setupUI() {
        viewModel.searchRequest.observe(this, Observer { requestItem ->
            // Do not search for request if the search was already performed (for example, on orientation change)
            requestItem?.get()?.let {
                search(it)
            }
        })

        viewModel.selectedItem.observe(this, Observer { item ->
            // Do not process the selected item in case it was already processed (for example, on orientation change)
            item?.get()?.let {
                navController.navigate(R.id.action_searchFragment_to_detailsFragment, DetailsFragment.newArgs(it))
            }
        })
    }

    private fun search(request: String) {
        searchRepo.search(request, object : OnErrorCallback {
            override fun onError(error: Throwable) {
                // TODO: Should be more user-friendly
                showSnackbar("Error: ${error.message}")
            }
        })
    }

    private fun updateResults(resultsCompleted: SearchResultsCompleted) {
        runOnUiThread {
            viewModel.onSearchResults(resultsCompleted)
        }
    }

    private fun teardown() {
        compositeDisposable.clear()

        unregisterReceiver(networkStateReceiver)

        searchRepo.teardown()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(root_main, message, Snackbar.LENGTH_SHORT).show()
    }

}
