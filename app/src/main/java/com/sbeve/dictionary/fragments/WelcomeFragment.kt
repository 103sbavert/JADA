package com.sbeve.dictionary.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.sbeve.dictionary.R
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {
    val navController: NavController by lazy {
        this.findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar, menu)
        val searchViewItem = menu.findItem(R.id.search)
        //set on click listener for activate_search for users to be able to activate the search bar
        //from a secondary button
        activate_search.setOnClickListener {
            searchViewItem.expandActionView()
        }
        val searchView = searchViewItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //pass the query entered by the user to ResultFragment
                navController.navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToResultFragment(
                        query
                    )
                )
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })

    }
}



