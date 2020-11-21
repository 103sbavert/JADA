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
        viewModel.outputResponse.observe(this) { it: Response<List<Word>>? ->
            updateUI(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar, menu)
        val searchViewItem = menu!!.findItem(R.id.search)
        val searchView = searchViewItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //call enqueue call when the user submits a query in the search bar
                viewModel.enqueueCall(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
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
        tV.textSize = resources.getDimension(R.dimen.word_text_size)
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
        tV.textSize = resources.getDimension(R.dimen.definition_text_size)
        return tV
    }

    private fun updateUI(response: Response<List<Word>>?) {
        //make the card view visible
        result_scroll_view.visibility = View.VISIBLE
        //remove all the views added to the linear layout by the previous search
        result_linear_layout.removeAllViews()

        //show a message informing the user that their query didn't hit a match
        if (response == null) {
            val tV = getWordTextView()
            tV.text = getString(R.string.no_results_found)
            result_linear_layout.addView(tV)
            return
        }

        // make a text view inside the linear layout for each word item included in the Json array
        for (i in response.body()!!) {
            getWordTextView().apply {
                text = i.word
                result_linear_layout.addView(this)
            }

            // make a new text view for each meaning of the word
            for ((b, j) in i.meanings.withIndex()) {
                val meaning = getDefTextView()

                //don't add numbering if there is only meaning
                if (i.meanings.size != 1) meaning.append("${b + 1}. \n")

                //append each definition for the current to the text view created for the current meaning
                for ((it, a) in j.definitions.zip('a'..'z')) {

                    //don't add numbering if there is only meaning
                    if (a != 'a') meaning.append("\n")
                    if (j.definitions.size != 1) meaning.append("\t $a)  ")
                    meaning.append(it.definition)
                }
                result_linear_layout.addView(meaning)
            }
        }
    }

}
