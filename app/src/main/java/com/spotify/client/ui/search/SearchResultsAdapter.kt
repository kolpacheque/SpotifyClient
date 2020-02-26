package com.spotify.client.ui.search

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.spotify.client.R
import com.spotify.client.data.search.SearchResultItem
import kotlinx.android.synthetic.main.item_search_result.view.*

interface OnResultSelectedCallback {
    fun onSelected(item: SearchResultItem)
}

class SearchResultsAdapter(private val callback: OnResultSelectedCallback) : RecyclerView.Adapter<SearchResultsAdapter.ItemViewHolder>() {

    var items = listOf<SearchResultItem>()

    private lateinit var picasso: Picasso

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        picasso = Picasso.with(parent.context.applicationContext)

        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ItemViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var item: SearchResultItem? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun bindData(item: SearchResultItem) {
            this.item = item

            item.loadThumbnailIntoView(picasso, itemView.iv_result_item_thumbnail)
            itemView.tv_result_item_name.text = item.name
            itemView.tv_result_item_description.text = item.description
        }

        override fun onClick(v: View) {
            item?.let {
                callback.onSelected(it)
            }
        }

    }

}