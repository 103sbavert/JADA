package com.sbeve.jada.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sbeve.jada.R
import com.sbeve.jada.databinding.DialogFragmentLemmaBinding
import com.sbeve.jada.models.Ids
import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.models.lemma.LexicalEntry
import com.sbeve.jada.ui.fragments.LemmaViewModel.LemmaCallResult.*
import com.sbeve.jada.ui.recyclerview.adapters.LemmasAdapter
import com.sbeve.jada.ui.recyclerview.itemdecorations.SpacesItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LemmaDialogFragment : BottomSheetDialogFragment(), LemmasAdapter.ViewHolderClickListener {
    
    private val viewModel: LemmaViewModel by viewModels()
    private val args: LemmaDialogFragmentArgs by navArgs()
    
    private lateinit var binding: DialogFragmentLemmaBinding
    private lateinit var lemmasAdapter: LemmasAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogFragmentLemmaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        (binding.root.parent as View).layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        
        lemmasAdapter = LemmasAdapter(this)
        viewModel.fetchLemmas(args.queryWord, args.languageIndex)
        
        binding.lemmasRecyclerview.adapter = lemmasAdapter
        binding.lemmasRecyclerview.addItemDecoration(SpacesItemDecoration(resources.getDimension(R.dimen.margin_padding_400).toInt()))
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        
        viewModel.callResult.observe(viewLifecycleOwner) {
            when (it!!) {
                is Error -> {
                    when ((it as Error).type) {
                        ErrorType.NoMatch -> showNoMatchError()
                        ErrorType.CallFailed -> showCallFailed()
                    }
                }
                is Success -> showResults(it as Success)
            }
            
        }
    }
    
    private fun showResults(CallResult: Success) {
        lemmasAdapter.updateDataSet(CallResult.lexicalEntries)
        binding.loadingAnim.visibility = View.GONE
        binding.loadingErrorMessage.visibility = View.GONE
    }
    
    private fun showNoMatchError() {
        binding.loadingAnim.visibility = View.GONE
        binding.loadingErrorMessage.text = getString(R.string.no_match)
    }
    
    private fun showCallFailed() {
        binding.loadingAnim.visibility = View.GONE
        binding.loadingErrorMessage.text = getString(R.string.call_failed)
    }
    
    override fun onItemClick(currentItem: LexicalEntry) {
        val recentQuery = RecentQuery(
            Ids(currentItem.inflectionOf[0].id, currentItem.lexicalCategory.id),
            currentItem.inflectionOf[0].text,
            args.languageIndex,
            currentItem.lexicalCategory.text
        )
        
        Log.e("TAG", "onItemClick: ${recentQuery.ids}")
        
        findNavController().navigate(
            LemmaDialogFragmentDirections.actionLemmaListDialogFragmentToResultFragment(
                recentQuery.ids.wordId,
                recentQuery.languageIndex,
                recentQuery.ids.lexicalCategoryId
            )
        )
        
        viewModel.addQuery(recentQuery)
    }
    
}
