package com.spotify.client.ui.search

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.spotify.client.R
import com.spotify.client.data.search.SearchResultItem
import kotlinx.android.synthetic.main.fragment_details.view.*

class DetailsFragment : Fragment() {

    companion object {
        const val TAG = "DetailsFragment"

        private const val ARG_SEARCH_RESULT_ITEM = "com.spotify.client.ui.search.SEARCH_RESULT_ITEM"

        fun newArgs(item: SearchResultItem): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(ARG_SEARCH_RESULT_ITEM, item)
            return bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_details, container, false)

        arguments?.let {
            val item = it.get(ARG_SEARCH_RESULT_ITEM) as SearchResultItem
            setupUI(rootView, item)
        }

        return rootView
    }

    private fun setupUI(rootView: View, item: SearchResultItem) {
        item.loadThumbnailIntoView(Picasso.with(requireContext().applicationContext), rootView.iv_details_thumbnail)
        rootView.tv_details_title.text = item.name
        rootView.tv_details_description.text = item.description

        val link = SpannableString(item.link)
        link.setSpan(UnderlineSpan(), 0, link.length, 0)
        link.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark)), 0, link.length, 0)
        rootView.tv_details_link.text = link

        rootView.tv_details_link.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
            startActivity(intent)
        }
    }

}