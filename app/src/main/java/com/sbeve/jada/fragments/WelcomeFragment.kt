package com.sbeve.jada.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.sbeve.jada.R
import com.sbeve.jada.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {
    private val navController: NavController by lazy {
        this.findNavController()
    }

    //the currently running instance of the activity
    private val mainActivityContext: MainActivity by lazy {
        activity as MainActivity
    }

    //get the menu inflated in the activity from it to use the menu in onCreateOptionsMenu later
    private val inflatedMenu: Menu by lazy {
        mainActivityContext.mainActivityMenu
    }

    /*
    private val sharedPreferences: SharedPreferences by lazy {
        mainActivityContext.activitySharedPreferences
    }
    */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_welcome, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        //set on click listener to let the user change the language
        change_language_button.setOnClickListener {
            mainActivityContext.changeLanguageDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchView = inflatedMenu.findItem(R.id.search).actionView as SearchView
        searchView
            .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    //pass the query entered by the user to ResultFragment
                    navController.navigate(
                        WelcomeFragmentDirections.actionWelcomeFragmentToResultFragment(query)
                    )
                    return true
                }

                override fun onQueryTextChange(newText: String?) = false
            })

    }

}



