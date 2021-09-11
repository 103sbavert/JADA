package com.sbeve.jada.ui.fragments

import android.content.Context.INPUT_METHOD_SERVICE
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
import com.sbeve.jada.ui.recyclerview.RecentQueriesAdapter
import com.sbeve.jada.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), SearchView.OnQueryTextListener, RecentQueriesAdapter.ViewHolderClickListener {
    
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
            createChangeLanguageDialog().show()
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
    private fun createChangeLanguageDialog(action: (() -> Unit) = {}) =
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.choose_a_language))
            .setSingleChoiceItems(Constants.supportedLanguages.names, viewModel.getSavedLanguageIndex())
            { dialogInterface, item ->
                viewModel.updateLanguageSettingKey(item)
                dialogInterface.dismiss()
                action.invoke()
            }
            .create()
    
    //show a language selection dialog everytime a word is shared to the app. Navigate to the results fragment as soon as a language is selected
    private fun handleSharedText(sharedText: String) {
        createChangeLanguageDialog {
            navigateToResultsFragment(sharedText, viewModel.getSavedLanguageIndex(), true)
        }.show()
    }
    
    //set up an observer for the all queries livedata that updates the recycler view adapter
    private fun updateRecyclerView() {
        viewModel.allQueries.observe(viewLifecycleOwner) {
            
            //making the error message visible if the list empty, hiding it again if it is not empty
            binding.noRecentQueries.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            
            adapter.submitList(it) {
                binding.queriesRecyclerView.scrollToPosition(0)
            }
        }
    }
    
    //an extension function on View that grabs the view token from the view it's called on to hide the soft keyboard
    private fun View.hideSoftKeyboard() {
        val imm: InputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    
    //navigate to the results fragment and save the query that is being passed to the results fragment. Also hide the soft keyboard.
    private fun navigateToResultsFragment(query: String, languageIndex: Int, isQueryToBeSaved: Boolean) {
        navController.navigate(MainFragmentDirections.actionMainFragmentToResultFragment(query, languageIndex))
        if (isQueryToBeSaved) viewModel.addQuery(RecentQuery(query, languageIndex))
        binding.root.hideSoftKeyboard()
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
