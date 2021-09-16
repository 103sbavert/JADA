package com.sbeve.jada.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sbeve.jada.R
import com.sbeve.jada.databinding.*
import com.sbeve.jada.ui.fragments.DefinitionsViewModel.WordCallResult.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefinitionsFragment : Fragment(R.layout.fragment_definitions) {
    
    private lateinit var binding: FragmentDefinitionsBinding
    private val args: DefinitionsFragmentArgs by navArgs()
    private val viewModel: DefinitionsViewModel by viewModels()
    private lateinit var navController: NavController
    //private val examplesTextColor = TypedValue()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding = FragmentDefinitionsBinding.bind(view)
        navController = findNavController()
        
        binding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
        
        //requireActivity().theme.resolveAttribute(R.attr.suppressed_text, examplesTextColor, true)
        
        viewModel.fetchWordInfo(args.wordId, args.languageIndex, args.lexicalCategoryId)
        viewModel.callResult.observe(viewLifecycleOwner) {
            when (it!!) {
                is Success -> showResults(it as Success)
                is Error -> showError(it as Error)
            }
            
        }
    }
    
    //show the right error message based on what went wrong
    private fun showError(error: Error) {
        binding.loadingAnim.visibility = View.GONE
        
        //make errorMessage visible if it's not already
        binding.loadingErrorMessage.visibility = View.VISIBLE
        when (error.type) {
            ErrorType.NoMatch -> binding.loadingErrorMessage.text = getString(R.string.no_match)
            ErrorType.CallFailed -> binding.loadingErrorMessage.text = getString(R.string.call_failed)
        }
    }
    
    private fun showResults(success: Success) {
        val word = success.word
        
        binding.loadingErrorMessage.visibility = View.GONE
        binding.loadingAnim.visibility = View.GONE
        
        for (i in word.results) {
            val wordLayoutBinding = WordLayoutBinding.inflate(layoutInflater)
            wordLayoutBinding.wordTextview.text = i.word
            for (j in i.lexicalEntries) {
                val lexicalCategoryLayoutBinding = LexicalEntryLayoutBinding.inflate(layoutInflater)
                lexicalCategoryLayoutBinding.lexicalCategoryTextview.text = j.lexicalCategory.text
                for ((index, k) in j.entries.withIndex()) {
                    val entryLayoutBinding = EntryLayoutBinding.inflate(layoutInflater)
                    entryLayoutBinding.entryCountTextview.text = "${index + 1}:"
                    for ((letter, l) in k.senses.withIndex()) {
                        val senseLayoutBindingBinding = SenseLayoutBindingBinding.inflate(layoutInflater)
                        senseLayoutBindingBinding.senseIdTextview.text = "${Char(letter + 97)})"
                        for (m in l.definitions) {
                            val definitionLayoutBinding = DefinitionLayoutBinding.inflate(layoutInflater)
                            definitionLayoutBinding.definitionTextview.text = m
                            senseLayoutBindingBinding.linearLayout.addView(definitionLayoutBinding.root)
                        }
                        entryLayoutBinding.linearLayout.addView(senseLayoutBindingBinding.root)
                    }
                    lexicalCategoryLayoutBinding.linearLayout.addView(entryLayoutBinding.root)
                }
                wordLayoutBinding.linearLayout.addView(lexicalCategoryLayoutBinding.root)
            }
            binding.resultLinearLayout.addView(wordLayoutBinding.root)
        }
    }
}
