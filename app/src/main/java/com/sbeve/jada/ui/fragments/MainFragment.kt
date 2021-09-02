package com.sbeve.jada.ui.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbeve.jada.R
import com.sbeve.jada.databinding.FragmentMainBinding
import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.utils.Constants
import com.sbeve.jada.utils.recyclerview.RecentQueriesAdapter
import com.sbeve.jada.utils.recyclerview.ViewHolderClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), SearchView.OnQueryTextListener, ViewHolderClickListener {
    
    private lateinit var navController: NavController
    
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()
    
    //adapter with empty list as the list will be provided when the database emits information
    private val adapter = RecentQueriesAdapter(this)
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        navController = this.findNavController()
        binding = FragmentMainBinding.bind(view)
        
        binding.changeLanguageGearIcon.setOnClickListener {
            createChangeLanguageDialog(null).show()
        }
        binding.clearAllButton.setOnClickListener {
            viewModel.clear()
        }
        
        viewModel.currentLanguage.observe(viewLifecycleOwner) {
            binding.currentLanguage.text = it
        }
        
        binding.queriesRecyclerView.setHasFixedSize(true)
        binding.queriesRecyclerView.adapter = adapter
        
        //set up an observer to update the recycler view whenever the database is updated
        updateRecyclerView()
        
        if (requireActivity().intent.action == Intent.ACTION_SEND) {
            handleSharedText(requireActivity().intent.getStringExtra(Intent.EXTRA_TEXT)!!)
            requireActivity().intent.action = Intent.ACTION_MAIN
        }
        
        binding.searchView.setOnQueryTextListener(this)
    }
    
    //play an empty animation to keep the fragment from disappearing from the background when the enter animation for other fragments is playing
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int) = AlphaAnimation(1.0F, 1.0F).apply {
        duration = resources.getInteger(R.integer.animation_duration).toLong()
    }
    
    //change language dialog to change the language to be used by the dictionary
    private fun createChangeLanguageDialog(action: ((dialogInterface: DialogInterface, item: Int) -> Unit)?) =
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.choose_a_language))
            .setSingleChoiceItems(Constants.supportedLanguages.names, viewModel.getSavedLanguageIndex())
            { dialogInterface, item ->
                viewModel.updateLanguageSettingKey(item)
                dialogInterface.dismiss()
                action?.invoke(dialogInterface, item)
            }
            .create()
    
    private fun handleSharedText(sharedText: String) {
        createChangeLanguageDialog { _, _ ->
            navigateToResultsFragment(sharedText, viewModel.getSavedLanguageIndex(), true)
        }.show()
    }
    
    private fun updateRecyclerView() {
        viewModel.allQueries.observe(viewLifecycleOwner) {
    
            //making the error message visible if the list empty, hiding it again if it is not empty
            binding.noRecentQueries.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
    
            adapter.submitList(it) {
                binding.queriesRecyclerView.scrollToPosition(0)
            }
        }
    }
    
    private fun View.hideSoftKeyboard() {
        val imm: InputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    
    private fun navigateToResultsFragment(query: String, languageIndex: Int, isQueryToBeSaved: Boolean) {
        navController.navigate(MainFragmentDirections.actionMainFragmentToResultFragment(query, languageIndex))
        if (isQueryToBeSaved) viewModel.addQuery(RecentQuery(query, languageIndex))
        binding.queriesRecyclerView.hideSoftKeyboard()
    }
    
    //implement on onItemClick which opens the result fragment with the saved recent query and the provided language
    override fun onItemClick(query: String, queryLanguageIndex: Int) {
        navigateToResultsFragment(query, queryLanguageIndex, false)
    }
    
    //implement onDeleteButtonClick to delete the saved query the button of which is pressed
    override fun onDeleteButtonClick(query: String, queryLanguageIndex: Int) {
        viewModel.deleteQuery(RecentQuery(query, queryLanguageIndex))
    }
    
    //navigate to the result fragment and show the results for submitted query. Asynchronously update the database.
    override fun onQueryTextSubmit(query: String): Boolean {
        navigateToResultsFragment(query, viewModel.getSavedLanguageIndex(), true)
        return true
    }
    
    //nothing to implement
    override fun onQueryTextChange(newText: String?) = false
}
