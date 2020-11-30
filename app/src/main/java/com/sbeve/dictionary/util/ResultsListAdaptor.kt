package com.sbeve.dictionary.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sbeve.dictionary.R
import kotlinx.android.synthetic.main.word_item_layout.view.*

class ResultsListAdaptor(private val wordsItemList: List<WordItem>) :
    RecyclerView.Adapter<ResultsListAdaptor.CustomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.word_item_layout, parent, false)
        return CustomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentItem = wordsItemList[position]
        holder.wordTitle.text = currentItem.wordTitleItem
        //assign information about the origin as the value for originText so the custom setter can
        //take care of update origin_textview properly
        holder.originText = currentItem.originItem
        holder.content.text = currentItem.contentItem
    }

    override fun getItemCount() = wordsItemList.size

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTitle: TextView = itemView.word_title_textview
        var originText: String? = null
            //custom setter to update the origin_textview manually inside the viewholder
            set(value) {
                //don't update the text for origin text view and also set it's visibility to gone
                //if no information about the origin of the word is provided.
                if (value.isNullOrEmpty()) origin.visibility = View.GONE
                else origin.text = itemView.context.getString(R.string.origin_info, value)
            }
        private val origin: TextView = itemView.origin_textview
        val content: TextView = itemView.content_textview

    }
}
