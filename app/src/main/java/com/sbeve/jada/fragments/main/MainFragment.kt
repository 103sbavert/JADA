package com.sbeve.jada.fragments.main

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbeve.jada.R
import com.sbeve.jada.activities.MainActivity
import com.sbeve.jada.databinding.FragmentMainBinding
import com.sbeve.jada.recyclerview_utils.RecentQueriesAdapter
import com.sbeve.jada.retrofit_utils.RetrofitInit


class MainFragment : Fragment(R.layout.fragment_main), RecentQueriesAdapter.OnItemClickListener {
    private val navController: NavController by lazy {
        this.findNavController()
    }

    //the currently running instance of the activity
    private val mainActivityContext: MainActivity by lazy {
        activity as MainActivity
    }
    private val stayInPlaceAnimation: Animation? by lazy {
        val anim: Animation = AlphaAnimation(1.0F, 1.0F)
        anim.duration = 150
        anim
    }

    private val viewModel: MainViewModel by viewModels()

    private lateinit var fragmentMainBinding: FragmentMainBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentMainBinding = FragmentMainBinding.bind(view)
        fragmentMainBinding.queriesRecyclerView.layoutManager = LinearLayoutManager(mainActivityContext)

        fragmentMainBinding.currentLanguage.text = RetrofitInit.supportedLanguages.first[mainActivityContext.savedLanguageIndex]
        fragmentMainBinding.changeLanguageGearIcon.setOnClickListener {
            createChangeLanguageDialog().show()
        }
        fragmentMainBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                //work
                viewModel.addQuery(mainActivityContext.savedLanguageIndex, query)
                navController.navigate(MainFragmentDirections.actionMainFragmentToResultFragment(query))
                hideSoftKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })
        setAdapter()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int) = stayInPlaceAnimation

    private fun createChangeLanguageDialog() =
        MaterialAlertDialogBuilder(mainActivityContext)
            .setTitle(getString(R.string.choose_a_language))
            .setSingleChoiceItems(RetrofitInit.supportedLanguages.first, mainActivityContext.savedLanguageIndex)
            { dialogInterface, i ->
                fragmentMainBinding.currentLanguage.text = RetrofitInit.supportedLanguages.first[i]
                mainActivityContext.applicationSharedPreferences
                    .edit()
                    .putInt(getString(R.string.language_setting_key), i)
                    .apply()
                dialogInterface.dismiss()
            }
            .create()

    //hides the keyboard
    private fun hideSoftKeyboard() {
        val imm: InputMethodManager = mainActivityContext.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager

        //Find the currently focused view, so we can grab the correct window token from it.
        var view = mainActivityContext.currentFocus

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun setAdapter() {
        viewModel.allQueries.observe(viewLifecycleOwner) {
            val adapter = RecentQueriesAdapter(it, this)
            fragmentMainBinding.queriesRecyclerView.adapter = adapter
        }
    }

    override fun onItemClick(position: Int) {
        val queryText = viewModel.allQueries.value!![position].queryText
        navController.navigate(MainFragmentDirections.actionMainFragmentToResultFragment(queryText))
    }
}
