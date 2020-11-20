package com.sbeve.dictionary.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
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

        viewModel.queriedWord.observe(this, Observer {
            viewModel.enqueueCall(it)
        })
        viewModel.outputResponse.observe(this, Observer {
            updateUI(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar, menu)
        val searchViewItem = menu!!.findItem(R.id.search)
        val searchView = searchViewItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.queriedWord.value = query
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        return true
    }

    private fun getWordTextView() = TextView(this).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textSize = 28F
    }

    private fun getDefTextView() = TextView(this).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textSize = 15F
    }

    private fun updateUI(response: Response<List<Word>>) {
        //make the card view visible and remove all the views added to the linear layout by the previous search
        result_scroll_view.visibility = View.VISIBLE
        result_linear_layout.removeAllViews()

        // make a text view inside the linear layout for each word item included in the Json array
        for (i in response.body()!!) {
            getWordTextView().apply {
                text = "\n" + i.word + "\n"
                result_linear_layout.addView(this)
            }
            // append each definition to the text view created for the current word's definitions each given definition for the current word
            for (j in i.meanings) for (l in j.definitions) {
                result_linear_layout.addView(
                    getDefTextView().apply {
                        append(l.definition + "\n")
                    }
                )
            }
        }
    }

}
