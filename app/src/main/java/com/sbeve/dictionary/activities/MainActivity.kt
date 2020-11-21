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
                viewModel.enqueueCall(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return true
    }

    private fun getWordTextView(): TextView {
        val tV = TextView(this)
        val newLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        newLayoutParams.topMargin = 15
        newLayoutParams.bottomMargin = 10
        tV.layoutParams = newLayoutParams
        tV.textSize = 25F
        return tV
    }

    private fun getDefTextView(): TextView {
        val tV = TextView(this)
        val newLayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        newLayoutParams.bottomMargin = 15
        tV.layoutParams = newLayoutParams
        tV.textSize = 15F
        return tV
    }

    private fun updateUI(response: Response<List<Word>>?) {
        //make the card view visible
        result_scroll_view.visibility = View.VISIBLE
        //remove all the views added to the linear layout by the previous search
        result_linear_layout.removeAllViews()

        if (response == null) {
            val tV = getWordTextView()
            tV.text = "No such word was found"
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

                meaning.append("${b + 1}. \n")

                //append each definition for the current to the text view created for the current meaning
                for ((it, a) in j.definitions.zip('a'..'z')) {
                    if (a != 'a') meaning.append("\n")
                    meaning.append("\t $a)  ")
                    meaning.append(it.definition)
                }
                result_linear_layout.addView(meaning)
            }
        }
    }

}
