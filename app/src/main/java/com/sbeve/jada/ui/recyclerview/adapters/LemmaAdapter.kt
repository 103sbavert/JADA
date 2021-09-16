package com.sbeve.jada.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbeve.jada.R
import com.sbeve.jada.databinding.LemmaLayoutBinding
import com.sbeve.jada.models.lemma.LexicalEntry
import com.sbeve.jada.utils.dpToPixels
import com.sbeve.jada.utils.getScreenWidth


class LemmasAdapter(
    private val viewHolderClickListener: ViewHolderClickListener,
) : RecyclerView.Adapter<LemmasAdapter.LemmaViewHolder>() {
    
    private var dataset: List<LexicalEntry> = emptyList()
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LemmaViewHolder {
        return LemmaViewHolder.getInstance(viewHolderClickListener, parent)
    }
    
    override fun onBindViewHolder(holder: LemmaViewHolder, position: Int) {
        holder.provideCurrentItem(dataset[position])
    }
    
    override fun getItemCount(): Int {
        return dataset.size
    }
    
    fun updateDataSet(dataset: List<LexicalEntry>) {
        this.dataset = dataset
        notifyDataSetChanged()
    }
    
    class LemmaViewHolder
    private constructor(
        binding: LemmaLayoutBinding,
        viewHolderClickListener: ViewHolderClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        
        //setting on click listeners for each item and the delete button in each item
        init {
            binding.root.setOnClickListener { viewHolderClickListener.onItemClick(currentItem) }
            val marginPadding400Pixels = binding.root.context.resources.getDimension(R.dimen.margin_padding_400).dpToPixels()
            val heightPixels = ViewGroup.LayoutParams.WRAP_CONTENT
            val widthPixels = (getScreenWidth() - (marginPadding400Pixels * 3)) / 2
            binding.root.layoutParams = GridLayoutManager.LayoutParams(widthPixels, heightPixels)
        }
        
        companion object {
            fun getInstance(viewHolderClickListener: ViewHolderClickListener, parent: ViewGroup): LemmaViewHolder {
                val binding = LemmaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return LemmaViewHolder(binding, viewHolderClickListener)
            }
        }
        
        private val lemmaTextView = binding.lemmaTextview
        private val lexicalCategoryTextview = binding.lexicalCategoryTextview
        private lateinit var currentItem: LexicalEntry
        
        fun provideCurrentItem(currentItem: LexicalEntry) {
            this.currentItem = currentItem
            lemmaTextView.text = currentItem.inflectionOf[0].text
            lexicalCategoryTextview.text = currentItem.lexicalCategory.text
        }
    }
    
    //custom interface to be implemented by the main fragment to set up onClickListeners
    interface ViewHolderClickListener {
        fun onItemClick(currentItem: LexicalEntry)
    }
}
