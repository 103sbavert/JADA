package com.sbeve.jada.recyclerview_utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sbeve.jada.databinding.QueryLayoutBinding
import com.sbeve.jada.room_utils.RecentQuery
import java.text.SimpleDateFormat
import java.util.*

class RecentQueriesAdapter(private val dataSet: List<RecentQuery>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecentQueriesAdapter.ViewHolder>() {

    class ViewHolder(myItemView: QueryLayoutBinding, private val onItemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(myItemView.root), View.OnClickListener {
        init {
            myItemView.root.setOnClickListener(this)
        }

        private val queryText = myItemView.queryText
        fun setQueryText(queryValue: String) {
            queryText.text = HtmlCompat.fromHtml(queryValue, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        private val time = myItemView.time
        fun setTimeText(timeValue: Long) {
            time.text = SimpleDateFormat.getDateTimeInstance().format(Date(timeValue))
        }

        override fun onClick(v: View?) {
            onItemClickListener.onItemClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QueryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        holder.setQueryText(currentItem.queryText)
        holder.setTimeText(currentItem.timeDate)
    }

    override fun getItemCount() = dataSet.size

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}
