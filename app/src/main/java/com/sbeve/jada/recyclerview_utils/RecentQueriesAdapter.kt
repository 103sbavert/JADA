package com.sbeve.jada.recyclerview_utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbeve.jada.databinding.QueryLayoutBinding
import com.sbeve.jada.retrofit_utils.RetrofitInit
import com.sbeve.jada.room_utils.RecentQuery
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class RecentQueriesAdapter(dataSet: List<RecentQuery>, private val viewHolderClickListener: ViewHolderClickListener) :
    RecyclerView.Adapter<RecentQueriesAdapter.RecentQueriesViewHolder>() {

    var dataSet = dataSet
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentQueriesViewHolder {
        return RecentQueriesViewHolder.recentQueriesViewHolder(this, parent)
    }

    override fun onBindViewHolder(holder: RecentQueriesViewHolder, position: Int) {
        val currentItem = dataSet[position]
        holder.provideCurrentItem(currentItem)
    }

    override fun getItemCount() = dataSet.size

    //custom interface to be implemented by the main activity to set up onClickListeners
    interface ViewHolderClickListener {
        fun onItemClick(query: String, queryLanguageIndex: Int)
        fun onDeleteButtonClick(query: String, queryLanguageIndex: Int)
    }

    class RecentQueriesViewHolder(myItemView: QueryLayoutBinding, private val viewHolderClickListener: ViewHolderClickListener) :
        RecyclerView.ViewHolder(myItemView.root) {

        //setting on click listeners for each item and the delete button in each item
        init {
            myItemView.root.setOnClickListener { viewHolderClickListener.onItemClick(queryTextValue, queryLanguageValue) }
            myItemView.deleteButton.setOnClickListener { viewHolderClickListener.onDeleteButtonClick(queryText.text.toString(), queryLanguageValue) }
        }

        companion object {
            fun recentQueriesViewHolder(recentQueriesAdapter: RecentQueriesAdapter, parent: ViewGroup): RecentQueriesViewHolder {
                val binding = QueryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RecentQueriesViewHolder(binding, recentQueriesAdapter.viewHolderClickListener)
            }
        }

        private val queryText = myItemView.queryText
        private lateinit var queryTextValue: String
        private val queryLanguage = myItemView.language
        private var queryLanguageValue by Delegates.notNull<Int>()
        private val time = myItemView.time

        //function to be called from onBindViewHolder() to provide the current item to the ViewHolder.
        fun provideCurrentItem(currentItem: RecentQuery) {
            bindQuery(currentItem.queryText)
            bindTime(currentItem.timeDate)
            bindLanguage(currentItem.queryLanguage)
        }

        private fun bindLanguage(languageValue: Int) {
            queryLanguageValue = languageValue
            queryLanguage.text = RetrofitInit.supportedLanguages.first[languageValue]
        }

        private fun bindQuery(queryValue: String) {
            queryTextValue = queryValue
            queryText.text = queryValue
        }

        private fun bindTime(timeValue: Long) {
            time.text = SimpleDateFormat("hh:mm a; dd MMMM, yyyy", Locale.getDefault()).format(timeValue)
        }
    }
}
