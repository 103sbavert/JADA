package com.sbeve.dictionary.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbeve.dictionary.R
import com.sbeve.dictionary.activities.MainActivity
import com.sbeve.dictionary.util.ResultsListAdaptor
import com.sbeve.dictionary.util.Word
import com.sbeve.dictionary.util.WordItem
import kotlinx.android.synthetic.main.fragment_result.*
import retrofit2.Response

class ResultFragment : Fragment() {
    private val viewModel: ResultViewModel by viewModels()
    private val args: ResultFragmentArgs by navArgs()

    //the currently running instance of the activity
    private val mainActivityContext: MainActivity by lazy {
        activity as MainActivity
    }

    /*
    private val sharedPreferences: SharedPreferences by lazy {
        mainActivityContext.activitySharedPreferences
    }
    */

    //get the menu inflated in the activity from it to use the menu in onCreateOptionsMenu later
    private val inflatedMenu: Menu by lazy {
        mainActivityContext.activityMenu
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        //setting a layout manager for the recycler view
        result_recycler_view.layoutManager = LinearLayoutManager(mainActivityContext)
        viewModel.fetchResult.observe(this.viewLifecycleOwner) {
            when (it) {
                //the value of fetchResult is null right when the viewModel is created and we only
                //wanna call fetchWordInformation() with the args when the fragment
                // is created for the first time (or when the viewmodel is instantiated)
                null -> viewModel.fetchWordInformation(args.query)
                //FetchResult was failed (this logic is included in fetchWordInformation()) and now
                //we wanna show an error message now and every time a configuration change happens
                //until fetchWordInformation() is called again
                ResultViewModel.FetchResult.Failure -> showError(viewModel.errorType)
                //FetchResult was successful and now we wanna show the result fetched from the
                //server now and every time a configuration change happens until
                //fetchWordInformation() is called again
                ResultViewModel.FetchResult.Success -> updateRecyclerView(viewModel.outputResponse)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //get the search view widget from the menu that was inflated inside the activity
        val searchView = inflatedMenu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                //hide the information about the word if it is visible to make room for the
                //error message
                if (!result_card_view.isGone) result_card_view.visibility = View.GONE
                //hide errorMessage if it is visible to make room for the result
                if (!errorMessage.isGone) errorMessage.visibility = View.GONE
                loading_anim.visibility = View.VISIBLE
                //fetch information about the word submitted by the user in the search bar
                viewModel.fetchWordInformation(query)
                return true
            }

            override fun onQueryTextChange(newText: String?) = false

        })
    }

    private fun updateRecyclerView(response: Response<List<Word>>) {
        loading_anim.visibility = View.GONE
        //make result_scroll_view visible if it's not already
        if (!result_card_view.isVisible) result_card_view.visibility = View.VISIBLE

        //make the list of words to be passed to the recycler view adapter
        val wordList = mutableListOf<WordItem>()
        for (i in response.body()!!) {
            //for some weird cases, the database does get a hit for the query but there is no
            //available meanings for the matched word. We're going to skip such cases.
            if (i.meanings.isEmpty()) continue

            //content string that is going to collect definitions for different meanings of the
            //matched word as it goes through the for loop below and then will be added as the text
            //to show in content_text_view
            var contentString = ""

            //iterate over each meaning available for the current word
            for ((index, j) in i.meanings.withIndex()) {

                //don't add numbering if there is only meaning (MTOne = more than one)
                val meaningsMTOne = i.meanings.size > 1
                if (meaningsMTOne) contentString += ("${index + 1}. ")
                //add information about the part of speech of the current meaning for the word
                contentString += "(${j.partOfSpeech})\n"

                //append each definition provided for the current meaning
                for ((k, numbering) in j.definitions.zip('a'..'z')) {
                    //add tap spacing if there are multiple meanings for the current word and
                    //serialization has been done
                    if (meaningsMTOne) contentString += "\t"
                    //don't add alphabet numbering if there is only definition
                    if (j.definitions.size > 1) contentString += ("$numbering) ")
                    contentString += (k.definition + "\n")
                }

                //if it's not the last meaning, leave an empty line for the next meaning to be
                //added
                if (index < i.meanings.lastIndex) contentString += ("\n")
            }
            //information about the current for it to be added to the wordList
            wordList.add(WordItem(i.word, i.origin, contentString))
            result_recycler_view.adapter = ResultsListAdaptor(wordList)

            //hide the keyboard once the result is visible on the screen
            viewModel.hideKeyboard(mainActivityContext)
        }

    }

    //show the right error message based on what went wrong
    private fun showError(state: ResultViewModel.ErrorType) {
        loading_anim.visibility = View.GONE
        //make errorMessage visible if it's not already
        if (!errorMessage.isVisible) errorMessage.visibility = View.VISIBLE
        when (state) {
            ResultViewModel.ErrorType.CallFailed -> errorMessage.text =
                getString(R.string.call_failed)
            ResultViewModel.ErrorType.NoMatch -> errorMessage.text =
                getString(R.string.no_match)
        }
    }
}
