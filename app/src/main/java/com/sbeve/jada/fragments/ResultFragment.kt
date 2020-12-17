package com.sbeve.jada.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.text.HtmlCompat.fromHtml
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sbeve.jada.R
import com.sbeve.jada.activities.MainActivity
import com.sbeve.jada.util.Word
import kotlinx.android.synthetic.main.fragment_result.*
import kotlinx.android.synthetic.main.meaning_layout.view.*
import kotlinx.android.synthetic.main.word_item_layout.view.*
import retrofit2.Response

class ResultFragment : Fragment(R.layout.fragment_result) {
    private val viewModel: ResultViewModel by viewModels()

    private val navController: NavController by lazy {
        this.findNavController()
    }

    private val args: ResultFragmentArgs by navArgs()

    //the currently running instance of the activity
    private val mainActivityContext: MainActivity by lazy {
        activity as MainActivity
    }

    private val examplesColor = TypedValue()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
        toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }

        mainActivityContext.theme.resolveAttribute(R.attr.examples_color, examplesColor, true)
        viewModel.fetchWordInfo(mainActivityContext.savedLanguageIndex, args.queryFromWelcomeFragment)

        viewModel.fetchWordInfoResultType.observe(viewLifecycleOwner) {
            when (it!!) {
                //FetchResult was failed (this logic is included in fetchWordInformation()) and now
                //we wanna show an error message now and every time a configuration change happens
                //until fetchWordInformation() is called again
                ResultViewModel.FetchWordInfoResultType.Failure -> showError(viewModel.errorType)

                //FetchResult was successful and now we wanna show the result fetched from the
                //server now and every time a configuration change happens until
                //fetchWordInformation() is called again
                ResultViewModel.FetchWordInfoResultType.Success -> showResults(viewModel.wordInfo)
            }
        }
    }

    private fun showResults(response: Response<List<Word>>) {
        //hide the loading animation
        loading_anim.visibility = View.GONE

        //get the response output by the fetchWordInfo()
        val wordsList = viewModel.getWordsItemsList(response.body()!!)

        for (i in wordsList) {

            //inflate word_item_layout to add information about the current word from the response
            val wordItemLayout = layoutInflater.inflate(R.layout.word_item_layout, result_linear_layout, false)

            //add the the word to word_title_textview
            wordItemLayout.word_title_textview.text = i.wordTitleItem

            //if there is no provided information about the origin, don't add anything to the textview and set the textview to gone
            if (i.originItem.isNullOrEmpty()) wordItemLayout.origin_textview.visibility = View.GONE
            else wordItemLayout.origin_textview.text = getString(R.string.origin_info, i.originItem)


            //iterate over each provided meaning for the word
            for ((a, j) in i.meaningsListItem.withIndex()) {

                //add meanings_layout to add information about the current meanings of the current word
                val meaningLayout = layoutInflater.inflate(R.layout.meaning_layout, wordItemLayout.word_linear_layout, false)

                //add information about the part of speech if provided, make the textview gone if not
                if (j.partOfSpeechItem != null) meaningLayout.partofspeech_textview.text = j.partOfSpeechItem
                else meaningLayout.partofspeech_textview.visibility = View.GONE

                //string var to add all the definitions provided for the current meaning
                var definitionsText = ""

                //iterate over each given definition-example pair
                for ((b, k) in j.definitions.withIndex()) {

                    //add the definition
                    definitionsText += getString(R.string.definition_text, b + 1, k.first)

                    //add a line break and then add the example if one is provided for the current pair
                    if (k.second.isNotEmpty()) {
                        definitionsText += """<br/>"""
                        definitionsText += getString(R.string.example_text, examplesColor.data.toString(), k.second)
                    }

                    //if it is not the last definition, add two line breaks to add the next. if it is, check if it the last meaning item and two line
                    //if that condition is true to add spacing between the current and the next spacing
                    if (b != j.definitions.lastIndex || a != i.meaningsListItem.lastIndex) definitionsText += """<br/><br/>"""
                }

                //add the definitions text to the textview
                meaningLayout.definitions_textview.text = fromHtml(definitionsText, FROM_HTML_MODE_LEGACY)

                //add the current meaning_layout to the current word_item_layout
                wordItemLayout.word_linear_layout.addView(meaningLayout)
            }

            //add the word_item_layout for the current word to results_linear_layout
            result_linear_layout.addView(wordItemLayout)
        }
    }

    //show the right error message based on what went wrong
    private fun showError(state: ResultViewModel.ErrorType) {
        loading_anim.visibility = View.GONE

        //make errorMessage visible if it's not already
        if (!error_message.isVisible) error_message.visibility = View.VISIBLE
        when (state) {
            ResultViewModel.ErrorType.CallFailed -> error_message.text =
                getString(R.string.call_failed)
            ResultViewModel.ErrorType.NoMatch -> error_message.text =
                getString(R.string.no_match)
        }
    }
}
