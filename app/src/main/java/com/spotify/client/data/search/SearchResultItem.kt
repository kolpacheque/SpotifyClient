package com.spotify.client.data.search

import android.os.Parcelable
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.spotify.client.R
import kotlinx.android.parcel.Parcelize

/**
 * Represents a single search result that holds valuable data for UI
 */
@Parcelize
data class SearchResultItem(
    val thumbnail: String,
    val name: String,
    val description: String,
    val link: String
) : Parcelable {

    fun loadThumbnailIntoView(picasso: Picasso, view: ImageView) {
        if (thumbnail.isNotEmpty()) {
            picasso.load(thumbnail).into(view)
        } else {
            view.setImageResource(R.drawable.ic_spotify)
        }
    }

}
