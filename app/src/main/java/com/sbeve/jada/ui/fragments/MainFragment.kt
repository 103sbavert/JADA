package com.sbeve.jada.ui.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.sbeve.jada.models.Ids
import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.ui.recyclerview.adapters.RecentQueriesAdapter
import com.sbeve.jada.utils.Constants
import com.sbeve.jada.utils.getLanguageNames
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
    
        navController = findNavController()
        binding = FragmentMainBinding.bind(view)
    
        binding.changeLanguageGearIcon.setOnClickListener {
            createChangeLanguageDialog().show()
        }
        binding.clearAllButton.setOnClickListener {
            createDeleteConfirmationDialog().show()
        }
    
        viewModel.currentLanguage.observe(viewLifecycleOwner) {
            binding.currentLanguage.text = it
        }
    
        //set up an observer to update the recycler view whenever the database is updated
        viewModel.recentQueriesList.observe(viewLifecycleOwner) { list ->
            //making the error message visible if the list empty, hiding it again if it is not empty
            if (list.isNullOrEmpty()) {
                binding.noRecentQueries.visibility = View.VISIBLE
                binding.clearAllButton.visibility = View.GONE
            } else {
                binding.noRecentQueries.visibility = View.GONE
                binding.clearAllButton.visibility = View.VISIBLE
            }
    
            adapter.submitList(
                list.filter {
                    it.ids.wordId.contains(binding.searchView.query)
                }
            ) {
                binding.queriesRecyclerView.scrollToPosition(0)
            }
    
        }
    
        binding.queriesRecyclerView.setHasFixedSize(true)
        binding.queriesRecyclerView.adapter = adapter
    
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
            .setSingleChoiceItems(Constants.supportedDictionaries.getLanguageNames(), viewModel.getSavedLanguageIndex())
            { dialogInterface, item ->
                viewModel.updateLanguageSettingKey(item)
                dialogInterface.dismiss()
                action.invoke()
            }
            .create()
    
    private fun createDeleteConfirmationDialog() = MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.delete_confirmation_title)
        .setMessage(getString(R.string.delete_confirmation_message))
        .setPositiveButton(R.string.positive_text) { _: DialogInterface, _: Int ->
            viewModel.clear()
        }
        .setNegativeButton(R.string.negative_text, null)
        .create()
    
    
    //show a language selection dialog everytime a word is shared to the app. Navigate to the results fragment as soon as a language is selected
    private fun handleSharedText(query: String) {
        createChangeLanguageDialog {
            openBottomSheet(query, viewModel.getSavedLanguageIndex())
        }.show()
    }
    
    //an extension function on View that grabs the view token from the view it's called on to hide the soft keyboard
    private fun View.hideSoftKeyboard() {
        val imm: InputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    
    private fun openBottomSheet(query: String, languageIndex: Int) {
        binding.root.hideSoftKeyboard()
        navController.navigate(MainFragmentDirections.actionMainFragmentToLemmaListDialogFragment(query, languageIndex))
    }
    
    private fun navigateToResultsFragment(wordId: String, languageIndex: Int, lexicalCategoryId: String) {
        binding.root.hideSoftKeyboard()
        navController.navigate(MainFragmentDirections.actionMainFragmentToResultFragment(wordId, languageIndex, lexicalCategoryId))
    }
    
    //implement on onItemClick which opens the result fragment with the saved recent query and the provided language
    override fun onItemClick(wordId: String, queryLanguageIndex: Int, lexicalCategoryId: String) {
        navigateToResultsFragment(wordId, queryLanguageIndex, lexicalCategoryId)
    }
    
    //implement onDeleteButtonClick to delete the saved query the button of which is pressed
    override fun onDeleteButtonClick(wordId: String, word: String, queryLanguageIndex: Int, lexicalCategoryId: String, lexicalCategory: String) {
        Log.e("TAG", "onDeleteButtonClick: $wordId")
        viewModel.deleteQuery(RecentQuery(Ids(wordId, lexicalCategoryId), word, queryLanguageIndex, lexicalCategory))
    }
    
    //navigate to the result fragment and show the results for submitted query. Asynchronously update the database.
    override fun onQueryTextSubmit(query: String): Boolean {
        openBottomSheet(query, viewModel.getSavedLanguageIndex())
        return true
    }
    
    override fun onQueryTextChange(newText: String): Boolean {
        adapter.submitList(
            viewModel.recentQueriesList.value?.filter { it ->
                it.ids.wordId.contains(newText)
            }
        ) {
            binding.queriesRecyclerView.scrollToPosition(0)
        }
        return true
    }
}
