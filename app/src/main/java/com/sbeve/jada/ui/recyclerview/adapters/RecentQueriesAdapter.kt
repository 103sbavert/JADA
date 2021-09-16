package com.sbeve.jada.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sbeve.jada.databinding.QueryLayoutBinding
import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class RecentQueriesAdapter(private val viewHolderClickListener: ViewHolderClickListener) :
    ListAdapter<RecentQuery, RecentQueriesAdapter.RecentQueryViewHolder>(RecentQueriesDiffUtil()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentQueryViewHolder {
        return RecentQueryViewHolder.getViewHolderInstance(viewHolderClickListener, parent)
    }
    
    override fun onBindViewHolder(holder: RecentQueryViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.provideCurrentItem(currentItem)
    }
    
    class RecentQueryViewHolder
    private constructor(binding: QueryLayoutBinding, viewHolderClickListener: ViewHolderClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        
        //setting on click listeners for each item and the delete button in each item
        init {
            binding.root.setOnClickListener {
                viewHolderClickListener.onItemClick(
                    currentItem.ids.wordId,
                    currentItem.languageIndex,
                    currentItem.ids.lexicalCategoryId
                )
            }
            binding.deleteButton.setOnClickListener {
                viewHolderClickListener.onDeleteButtonClick(
                    currentItem.ids.wordId,
                    currentItem.word,
                    currentItem.languageIndex,
                    currentItem.ids.lexicalCategoryId,
                    currentItem.lexicalCategory
                )
            }
        }
        
        companion object {
            fun getViewHolderInstance(viewHolderClickListener: ViewHolderClickListener, parent: ViewGroup): RecentQueryViewHolder {
                val binding = QueryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RecentQueryViewHolder(binding, viewHolderClickListener)
            }
        }
        
        private val queryTextview = binding.queryTextview
        private val languageTextview = binding.languageTextview
        private val lexicalCategoryTextview = binding.lexicalCategoryTextview
        private val timeDateTextview = binding.timeDateTextview
        
        private lateinit var currentItem: RecentQuery
        
        //function to be called from onBindViewHolder() to provide the current item to the ViewHolder.
        fun provideCurrentItem(currentItem: RecentQuery) {
            this.currentItem = currentItem
            bindWord()
            bindLanguage()
            bindLexicalCategory()
            bindTime()
        }
        
        private fun bindLanguage() {
            languageTextview.text = Constants.supportedDictionaries[currentItem.languageIndex].languageName
        }
        
        private fun bindWord() {
            queryTextview.text = currentItem.word
        }
        
        private fun bindLexicalCategory() {
            lexicalCategoryTextview.text = "(${currentItem.lexicalCategory})"
        }
        
        private fun bindTime() {
            timeDateTextview.text = SimpleDateFormat("hh:mm a; dd MMMM, yyyy", Locale.getDefault()).format(currentItem.timeDate)
        }
    }
    
    //custom interface to be implemented by the main fragment to set up onClickListeners
    interface ViewHolderClickListener {
        fun onItemClick(wordId: String, queryLanguageIndex: Int, lexicalCategoryId: String)
        fun onDeleteButtonClick(wordId: String, word: String, queryLanguageIndex: Int, lexicalCategoryId: String, lexicalCategory: String)
    }
    
    class RecentQueriesDiffUtil : DiffUtil.ItemCallback<RecentQuery>() {
        
        override fun areItemsTheSame(oldItem: RecentQuery, newItem: RecentQuery): Boolean {
            return oldItem.ids == newItem.ids
        }
        
        override fun areContentsTheSame(oldItem: RecentQuery, newItem: RecentQuery): Boolean {
            return oldItem.timeDate == newItem.timeDate
        }
    }
}
