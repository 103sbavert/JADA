package com.sbeve.jada.fragments.main

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbeve.jada.R
import com.sbeve.jada.activities.MainActivity
import com.sbeve.jada.databinding.FragmentMainBinding
import com.sbeve.jada.recyclerview_utils.RecentQueriesAdapter
import com.sbeve.jada.recyclerview_utils.ViewHolderClickListener
import com.sbeve.jada.retrofit_utils.RetrofitInit
import com.sbeve.jada.room_utils.RecentQuery

class MainFragment : Fragment(R.layout.fragment_main), SearchView.OnQueryTextListener, ViewHolderClickListener,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val navController: NavController by lazy {
        this.findNavController()
    }
    
    //the currently running instance of the activity
    private val mainActivityContext: MainActivity by lazy {
        activity as MainActivity
    }
    
    //animation to be played when a fragment transaction happens from this activity a pop action transaction to this activity happens so the fragment
    //doesn't disappear from the background
    private val stayInPlaceAnimation: Animation? by lazy {
        val anim: Animation = AlphaAnimation(1.0F, 1.0F)
        anim.duration = resources.getInteger(R.integer.animation_duration).toLong()
        anim
    }
    private val viewModel: MainViewModel by viewModels()
    private lateinit var fragmentMainBinding: FragmentMainBinding
    
    //getting the saved language setting's value through a custom getter
    private val savedLanguageIndex: Int
        get() = mainActivityContext.applicationPreferences
            .getInt(getString(R.string.language_setting_key), 0)
    
    //adapter with empty list as the list will be provided when the database emits information
    private val adapter by lazy {
        RecentQueriesAdapter(this)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentMainBinding = FragmentMainBinding.bind(view)
        fragmentMainBinding.changeLanguageGearIcon.setOnClickListener {
            createChangeLanguageDialog(null).show()
        }
        fragmentMainBinding.clearAllButton.setOnClickListener {
            viewModel.clear()
        }
        fragmentMainBinding.queriesRecyclerView.setHasFixedSize(true)
        fragmentMainBinding.queriesRecyclerView.adapter = adapter
    
        //set up an observer to update the recycler view whenever the database is updated
        updateRecyclerView()
    
        if (mainActivityContext.intent.action == Intent.ACTION_SEND) {
            handleSharedText(mainActivityContext.intent.getStringExtra(Intent.EXTRA_TEXT)!!)
            mainActivityContext.intent.action = Intent.ACTION_MAIN
        }
    
        //set the text view's text to show whichever language is selected and update the text whenever the setting is changed
        fragmentMainBinding.currentLanguage.text = RetrofitInit.supportedLanguages.first[savedLanguageIndex]
        mainActivityContext.applicationPreferences.registerOnSharedPreferenceChangeListener(this)
    
        fragmentMainBinding.searchView.setOnQueryTextListener(this)
    }
    
    //play an empty animation to keep the fragment from disappearing from the background when the enter animation for other fragments is playing
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int) = stayInPlaceAnimation
    
    //change language dialog to change the language to be used by the dictionary
    private fun createChangeLanguageDialog(action: ((dialogInterface: DialogInterface, item: Int) -> Unit)?) =
        MaterialAlertDialogBuilder(mainActivityContext)
            .setTitle(getString(R.string.choose_a_language))
            .setSingleChoiceItems(RetrofitInit.supportedLanguages.first, savedLanguageIndex)
            { dialogInterface, item ->
                mainActivityContext.applicationPreferences
                    .edit()
                    .putInt(getString(R.string.language_setting_key), item)
                    .apply()
                dialogInterface.dismiss()
                action?.invoke(dialogInterface, item)
            }
            .create()
    
    
    private fun handleSharedText(sharedText: String) {
        createChangeLanguageDialog { _, _ ->
            navController.navigate(MainFragmentDirections.actionMainFragmentToResultFragment(sharedText, savedLanguageIndex))
            viewModel.addQuery(RecentQuery(sharedText, savedLanguageIndex))
        }.show()
    }
    
    private fun updateRecyclerView() {
        viewModel.allQueries.observe(viewLifecycleOwner) {
            
            //making the error message visible if the list empty, hiding it again if it is not empty
            fragmentMainBinding.noRecentQueries.visibility = if (it.isNotEmpty()) View.GONE else View.VISIBLE
            adapter.submitList(it)
        }
        
        //scroll the recycler view to the top every time a new item is inserted (submitList(it) is asynchronous)
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                fragmentMainBinding.queriesRecyclerView.scrollToPosition(0)
            }
    
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                fragmentMainBinding.queriesRecyclerView.scrollToPosition(0)
            }
        })
    }
    
    //implement on onItemClick which opens the result fragment with the saved recent query and the provided language
    override fun onItemClick(query: String, queryLanguageIndex: Int) {
        navController.navigate(MainFragmentDirections.actionMainFragmentToResultFragment(query, queryLanguageIndex))
    }
    
    //implement onDeleteButtonClick to delete the saved query the button of which is pressed
    override fun onDeleteButtonClick(query: String, queryLanguageIndex: Int) {
        viewModel.deleteQuery(RecentQuery(query, queryLanguageIndex))
    }
    
    //update the current language textview whenever the SharedPreference updates
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            getString(R.string.language_setting_key) -> {
                fragmentMainBinding.currentLanguage.text = RetrofitInit.supportedLanguages.first[savedLanguageIndex]
            }
        }
    }
    
    //navigate to the result fragment and show the results for submitted query. Asynchronously update the database.
    override fun onQueryTextSubmit(query: String): Boolean {
        viewModel.addQuery(RecentQuery(query, savedLanguageIndex))
        navController.navigate(MainFragmentDirections.actionMainFragmentToResultFragment(query, savedLanguageIndex))
        return true
    }
    
    //nothing to implement
    override fun onQueryTextChange(newText: String?) = false
}
