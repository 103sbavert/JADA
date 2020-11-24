package com.sbeve.dictionary.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.sbeve.dictionary.R
import com.sbeve.dictionary.retrofit_files.Word
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(activity_toolbar)

        //update the UI if a new response has been output by enqueueCall()
        viewModel.outputResponse.observe(this) {
            //only show the results if the content shown before the configuration change (if any)
            //was indeed results from the server
            if (viewModel.shownBeforeConfigurationChange == MainViewModel.ShownBeforeConfigurationChange.Result) {
                updateUI(it)
                //hide errorMessage if it is visible to make room for the new results
                if (errorMessage.visibility != View.GONE) errorMessage.visibility = View.GONE
                loading_anim.visibility = View.GONE
            }
        }

        viewModel.failType.observe(this) {
            //only show the error if the content shown before the configuration change (if any)
            //was indeed an error message
            if (viewModel.shownBeforeConfigurationChange == MainViewModel.ShownBeforeConfigurationChange.Error) {
                showError(it)
                //hide result_scroll_view if it is visible to make room for the error message
                if (result_scroll_view.visibility != View.GONE) result_scroll_view.visibility =
                    View.GONE
                loading_anim.visibility = View.GONE
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar, menu)
        val searchViewItem = menu!!.findItem(R.id.search)
        val searchView = searchViewItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                loading_anim.visibility = View.VISIBLE
                //remove all the views added to the result_linear_view by the previous search
                result_linear_layout.removeAllViews()
                //fetch information about the word submitted by the user in the search bar
                viewModel.fetchWordInformation(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return true
    }

    //get a text view with large font size for showing the word
    private fun getWordTextView(): TextView {
        val tV = TextView(this)
        val newLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        newLayoutParams.topMargin = resources.getDimension(R.dimen.standard_margin_padding).toInt()
        newLayoutParams.bottomMargin = resources.getDimension(R.dimen.small_margin_padding).toInt()
        tV.layoutParams = newLayoutParams
        tV.textSize = resources.getInteger(R.integer.word_text_size).toFloat()
        return tV
    }

    //get a text view with small font size for showing definitions
    private fun getDefTextView(): TextView {
        val tV = TextView(this)
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
        val tV = TextView(this)
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
        //make result_scroll_view visible if its not already
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
            viewModel.hideKeyboard(this)
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
    private fun showError(state: MainViewModel.State) {
        //make errorMessage visible if it's not already
        if (errorMessage.visibility != View.VISIBLE) errorMessage.visibility = View.VISIBLE
        when (state) {
            MainViewModel.State.CallFailed -> errorMessage.text = getString(R.string.call_failed)
            MainViewModel.State.NoMatch -> errorMessage.text = getString(R.string.no_match)
        }
    }
}
