package com.spotify.client.ui.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.constraint.motion.MotionLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.spotify.client.R
import com.spotify.client.data.search.SearchResultItem
import com.spotify.client.data.search.SearchResultsCompleted
import com.spotify.client.ui.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment(), OnResultSelectedCallback {

    companion object {
        const val TAG = "SearchFragment"

        private const val ACTION_BAR_ANIMATION_DELAY_MS = 200L

        private const val ALPHA_VISIBLE = 1f
        private const val ALPHA_INVISIBLE = 0f
    }

    private lateinit var rootView: MotionLayout

    lateinit var viewModel: SearchViewModel
    lateinit var adapter: SearchResultsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_search, container, false) as MotionLayout

        createUtils()
        setupUI()

        return rootView
    }

    override fun onResume() {
        super.onResume()

        rootView.postDelayed({ rootView.transitionToEnd() }, ACTION_BAR_ANIMATION_DELAY_MS)
    }

    override fun onPause() {
        rootView.transitionToStart()

        super.onPause()
    }

    override fun onSelected(item: SearchResultItem) {
        viewModel.onItemSelected(item)
    }

    private fun createUtils() {
        viewModel = requireActivity().run { ViewModelProviders.of(this)[SearchViewModel::class.java] }
        adapter = SearchResultsAdapter(this)
    }

    private fun setupUI() {
        setupSearchUI()
        setupSearchResultsUI()
    }

    private fun setupSearchUI() {
        rootView.et_search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        rootView.btn_search.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        var request = rootView.et_search.text.toString().trim()
        if (request.isEmpty()) {
//            request = "rick astley never gonna give you up"
            Toast.makeText(requireActivity(), "Search request cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        hideKeyboard()

        searchStarted()

        viewModel.onSearchRequest(request)
    }

    private fun setupSearchResultsUI() {
        rootView.rv_search_results.adapter = adapter
        rootView.rv_search_results.layoutManager = LinearLayoutManager(context)
        rootView.rv_search_results.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        viewModel.searchResults.observe(this, Observer { resultsCompleted ->
            searchUpdate(resultsCompleted)
        })
    }

    private fun searchStarted() {
        // Clear previous results
        adapter.items = emptyList()
        adapter.notifyDataSetChanged()

        // Search started - show progress bar
        rootView.progress_search_results.alpha = ALPHA_VISIBLE
        rootView.tv_search_no_results.alpha = ALPHA_INVISIBLE
    }

    private fun searchUpdate(resultsCompleted: SearchResultsCompleted?) {
        resultsCompleted?.let {
            rootView.tv_search_no_results.alpha = ALPHA_INVISIBLE

            val searchResults = resultsCompleted.searchResultItems
            if (resultsCompleted.completed) {
                if (searchResults.isNotEmpty()) {
                    // Don't overwrite incomplete results with empty list
                    adapter.items = searchResults
                    adapter.notifyDataSetChanged()
                }

                // Search completed - hide progress bar
                rootView.progress_search_results.alpha = ALPHA_INVISIBLE
                if (adapter.items.isEmpty()) {
                    // Search completed - show no results
                    rootView.tv_search_no_results.alpha = ALPHA_VISIBLE
                }
            } else {
                if (searchResults.isNotEmpty()) {
                    // Don't overwrite incomplete results with empty list
                    adapter.items = searchResults
                    adapter.notifyDataSetChanged()

                    // Non-empty results available - hide progress bar
                    rootView.progress_search_results.alpha = ALPHA_INVISIBLE
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(rootView.windowToken, 0)
    }

}