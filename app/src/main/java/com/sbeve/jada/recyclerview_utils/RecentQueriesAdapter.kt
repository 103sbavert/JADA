package com.sbeve.jada.recyclerview_utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.sbeve.jada.databinding.QueryLayoutBinding
import com.sbeve.jada.retrofit_utils.RetrofitInit
import com.sbeve.jada.room_utils.RecentQuery
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class RecentQueriesAdapter(var dataSet: List<RecentQuery>, private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<RecentQueriesAdapter.RecentQueriesViewHolder>() {

    class RecentQueriesViewHolder(myItemView: QueryLayoutBinding, private val onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(myItemView.root) {

        private val queryText = myItemView.queryText
        private lateinit var queryTextValue: String
        private val time = myItemView.time
        private val queryLanguage = myItemView.language
        private var queryLanguageValue by Delegates.notNull<Int>()

        fun setLanguageText(languageValue: Int) {
            queryLanguageValue = languageValue
            queryLanguage.text = RetrofitInit.supportedLanguages.first[languageValue]
        }

        fun setQueryText(queryValue: String) {
            queryTextValue = queryValue
            queryText.text = HtmlCompat.fromHtml(queryValue, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        fun setTimeText(timeValue: Long) {
            time.text = SimpleDateFormat("HH:mm, dd MMM, yyyy", Locale.getDefault()).format(timeValue)
        }

        //setting on click listeners for each item and the delete button in each item
        init {
            myItemView.root.setOnClickListener { onClickListener.onItemClick(queryTextValue, queryLanguageValue) }
            myItemView.deleteButton.setOnClickListener { onClickListener.onDeleteButtonClick(queryText.text.toString(), queryLanguageValue) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentQueriesViewHolder {
        val binding = QueryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentQueriesViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: RecentQueriesViewHolder, position: Int) {
        val currentItem = dataSet[position]
        holder.setQueryText(currentItem.queryText)
        holder.setTimeText(currentItem.timeDate)
        holder.setLanguageText(currentItem.queryLanguage)
    }

    override fun getItemCount() = dataSet.size

    //custom interface to be implemented by the main activity to set up onClickListeners
    interface OnClickListener {
        fun onItemClick(query: String, queryLanguageIndex: Int)
        fun onDeleteButtonClick(query: String, queryLanguageIndex: Int)
    }
}
