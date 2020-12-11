package com.sbeve.jada.fragments

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbeve.jada.R
import com.sbeve.jada.activities.MainActivity
import com.sbeve.jada.util.ResultsListAdaptor
import com.sbeve.jada.util.Word
import kotlinx.android.synthetic.main.fragment_result.*
import retrofit2.Response

class ResultFragment : Fragment() {
    private val viewModel: ResultViewModel by viewModels()
    private val args: ResultFragmentArgs by navArgs()

    //the currently running instance of the activity
    private val mainActivityContext: MainActivity by lazy {
        activity as MainActivity
    }

    //the language selected by the user for searches
    private val savedLanguageIndex: Int
        get() = mainActivityContext
            .applicationSharedPreferences
            .getInt(getString(R.string.language_setting_key), 0)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_result, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        //setting a layout manager for the recycler view
        result_recycler_view.layoutManager = LinearLayoutManager(mainActivityContext)
        viewModel.fetchWordInfoResult.observe(viewLifecycleOwner) {
            when (it) {

                //the value of fetchResult is null right when the viewModel is created and we only
                //wanna call fetchWordInformation() with the args and the name of the selected
                // language when the fragment is created for the first time (or when the viewmodel
                // is instantiated)
                null -> viewModel.fetchWordInfo(savedLanguageIndex, args.queryFromWelcomeFragment)

                //FetchResult was failed (this logic is included in fetchWordInformation()) and now
                //we wanna show an error message now and every time a configuration change happens
                //until fetchWordInformation() is called again
                ResultViewModel.FetchWordInfoResult.Failure -> showError(viewModel.errorType)

                //FetchResult was successful and now we wanna show the result fetched from the
                //server now and every time a configuration change happens until
                //fetchWordInformation() is called again
                ResultViewModel.FetchWordInfoResult.Success -> updateRecyclerView(viewModel.wordInfo)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        //get the search view widget from the menu that was inflated inside the activity
        val searchView =
            mainActivityContext
                .mainActivityMenu
                .findItem(R.id.search)
                .actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {

                //hide the information about the word if it is visible to make room for the
                //error message
                if (!result_card_view.isGone) result_card_view.visibility = View.GONE

                //hide errorMessage if it is visible to make room for the result
                if (!errorMessage.isGone) errorMessage.visibility = View.GONE
                loading_anim.visibility = View.VISIBLE

                //fetch information about the word submitted by the user in the search bar and the
                //language to be used
                viewModel.fetchWordInfo(savedLanguageIndex, query)
                return true
            }

            override fun onQueryTextChange(newText: String?) = false

        })
    }

    private fun updateRecyclerView(response: Response<List<Word>>) {
        loading_anim.visibility = View.GONE

        //make result_scroll_view visible if it's not already
        if (!result_card_view.isVisible) result_card_view.visibility = View.VISIBLE
        result_recycler_view.adapter =
            ResultsListAdaptor(viewModel.getWordsItemsList(response.body()!!))

        //hide the keyboard once the result is visible on the screen
        hideSoftKeyboard()
    }


    //hides the keyboard
    private fun hideSoftKeyboard() {
        val imm: InputMethodManager =
            mainActivityContext.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager

        //Find the currently focused view, so we can grab the correct window token from it.
        var view = mainActivityContext.currentFocus

        //If no view currently has focus, create a new one, just so we can grab a window token from
        // it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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
