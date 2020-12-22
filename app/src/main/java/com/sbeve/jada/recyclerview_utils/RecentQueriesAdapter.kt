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

class RecentQueriesAdapter(private val dataSet: List<RecentQuery>, private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<RecentQueriesAdapter.ViewHolder>() {

    class ViewHolder(myItemView: QueryLayoutBinding, private val onClickListener: OnClickListener) :
        RecyclerView.ViewHolder(myItemView.root) {
        private val queryText = myItemView.queryText
        private val time = myItemView.time
        private val queryLanguage = myItemView.language
        private var queryLanguageValue by Delegates.notNull<Int>()

        fun setLanguageText(languageValue: Int) {
            queryLanguageValue = languageValue
            queryLanguage.text = RetrofitInit.supportedLanguages.first[languageValue]

        }

        fun setQueryText(queryValue: String) {
            queryText.text = HtmlCompat.fromHtml(queryValue, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        fun setTimeText(timeValue: Long) {
            time.text = SimpleDateFormat.getDateTimeInstance().format(Date(timeValue))
        }

        init {
            myItemView.root.setOnClickListener { onClickListener.onItemClick(adapterPosition) }
            myItemView.deleteButton.setOnClickListener { onClickListener.onDeleteButtonClick(queryText.text.toString(), queryLanguageValue) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QueryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        holder.setQueryText(currentItem.queryText)
        holder.setTimeText(currentItem.timeDate)
        holder.setLanguageText(currentItem.queryLanguage)
    }

    override fun getItemCount() = dataSet.size

    interface OnClickListener {
        fun onItemClick(position: Int)
        fun onDeleteButtonClick(query: String, queryLanguageIndex: Int)
    }
}
