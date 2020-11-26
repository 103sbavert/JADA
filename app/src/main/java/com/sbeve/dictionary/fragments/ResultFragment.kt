package com.sbeve.dictionary.fragments

import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sbeve.dictionary.R
import com.sbeve.dictionary.util.Word
import kotlinx.android.synthetic.main.fragment_result.*
import retrofit2.Response

class ResultFragment : Fragment() {
    private val viewModel: ResultViewModel by viewModels()
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)
        setHasOptionsMenu(true)

        viewModel.fetchResult.observe(this.viewLifecycleOwner) {
            loading_anim.visibility = View.GONE
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
                ResultViewModel.FetchResult.Success -> updateUI(viewModel.outputResponse)
            }
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar, menu)
        val searchView =
            menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                //hide the information about the word if it is visible to make room for the
                //error message
                if (result_scroll_view.visibility != View.GONE) result_scroll_view.visibility =
                    View.GONE
                //hide errorMessage if it is visible to make room for the result
                if (errorMessage.visibility != View.GONE) errorMessage.visibility = View.GONE
                //remove all the views added to the result_linear_view by the previous search
                result_linear_layout.removeAllViews()
                loading_anim.visibility = View.VISIBLE
                //fetch information about the word submitted by the user in the search bar
                viewModel.fetchWordInformation(query)
                return true
            }

            override fun onQueryTextChange(newText: String?) = false

        })
    }

    //get a text view with large font size for showing the word
    private fun getWordTextView(): TextView {
        val tV = TextView(this.context)
        val newLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        newLayoutParams.topMargin =
            resources.getDimension(R.dimen.standard_margin_padding).toInt()
        newLayoutParams.bottomMargin =
            resources.getDimension(R.dimen.small_margin_padding).toInt()
        tV.layoutParams = newLayoutParams
        tV.textSize = resources.getInteger(R.integer.word_text_size).toFloat()
        return tV
    }

    //get a text view with small font size for showing definitions
    private fun getDefTextView(): TextView {
        val tV = TextView(this.context)
        val newLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        newLayoutParams.bottomMargin =
            resources.getDimension(R.dimen.standard_margin_padding).toInt()
        tV.layoutParams = newLayoutParams
        tV.textSize = resources.getInteger(R.integer.definition_text_size).toFloat()
        return tV
    }

    //get a text view with smaller font for origin related
    private fun getOrgTextView(): TextView {
        val tV = TextView(this.context)
        val newLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        newLayoutParams.bottomMargin =
            resources.getDimension(R.dimen.standard_margin_padding).toInt()
        tV.layoutParams = newLayoutParams
        tV.textSize = resources.getInteger(R.integer.org_text_size).toFloat()
        return tV
    }

    private fun updateUI(response: Response<List<Word>>) {
        //make result_scroll_view visible if it's not already
        if (result_scroll_view.visibility != View.VISIBLE) result_scroll_view.visibility =
            View.VISIBLE
        // make a text view inside the linear layout for each word item included in the Json array
        for (i in response.body()!!) {
            val wordTv = getWordTextView()
            // make a text view to show the meanings of the word
            val meaningTv = getDefTextView()
            //add the word to the word text view
            wordTv.text = i.word
            //show the word text view on the screen
            result_linear_layout.addView(wordTv)
            showOrgInfo(i)

            //iterate over each meaning available for the current word
            for ((index, j) in i.meanings.withIndex()) {

                //don't add numbering if there is only meaning
                if (i.meanings.size != 1) meaningTv.append("${index + 1}. \n")

                //append each definition provided for the current meaning
                for ((k, numbering) in j.definitions.zip('a'..'z')) {

                    //don't add alphabet numbering if there is only definition
                    if (j.definitions.size != 1) meaningTv.append("\t $numbering)  ")
                    meaningTv.append(k.definition + "\n")
                }

                //if it's not the last meaning, leave an empty line for the next meaning to be
                // added
                if (index != i.meanings.lastIndex) meaningTv.append("\n")
            }

            //add the meaning text views and the word text view to the result_linear_layout
            result_linear_layout.addView(meaningTv)

            //hide the keyboard once the result is visible on the screen
            viewModel.hideKeyboard(requireActivity())
        }

    }

    //show information about the origin of the word (if available)
    private fun showOrgInfo(word: Word) {
        //check if the API has provided info about the word's origin
        if (!word.origin.isNullOrEmpty()) {
            val originTv = getOrgTextView()
            //add the information about the origin to the origin text view
            originTv.text = getString(R.string.originInfo, word.origin)
            result_linear_layout.addView(originTv)
        }
    }

    //show the right error message based on what went wrong
    private fun showError(state: ResultViewModel.ErrorType) {
        //make errorMessage visible if it's not already
        if (errorMessage.visibility != View.VISIBLE) errorMessage.visibility = View.VISIBLE
        when (state) {
            ResultViewModel.ErrorType.CallFailed -> errorMessage.text =
                getString(R.string.call_failed)
            ResultViewModel.ErrorType.NoMatch -> errorMessage.text = getString(R.string.no_match)
        }
    }
}
