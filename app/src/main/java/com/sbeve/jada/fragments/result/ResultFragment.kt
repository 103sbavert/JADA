package com.sbeve.jada.fragments.result

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.text.HtmlCompat.fromHtml
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sbeve.jada.R
import com.sbeve.jada.activities.MainActivity
import com.sbeve.jada.databinding.FragmentResultBinding
import com.sbeve.jada.databinding.MeaningLayoutBinding
import com.sbeve.jada.databinding.WordLayoutBinding
import com.sbeve.jada.retrofit_utils.Meaning
import com.sbeve.jada.retrofit_utils.Word
import retrofit2.Response

class ResultFragment : Fragment(R.layout.fragment_result) {
    
    //the currently running instance of the activity
    private val mainActivityContext: MainActivity by lazy {
        activity as MainActivity
    }
    private val viewModel: ResultViewModel by viewModels()
    private val navController: NavController by lazy {
        this.findNavController()
    }
    private val args: ResultFragmentArgs by navArgs()
    private val examplesTextColor = TypedValue()
    
    private lateinit var fragmentResultBinding: FragmentResultBinding
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentResultBinding = FragmentResultBinding.bind(view)
        
        fragmentResultBinding.toolbar.setNavigationOnClickListener {
            navController.navigateUp()
        }
        
        //getting the examples text color
        mainActivityContext.theme.resolveAttribute(R.attr.suppressed_text, examplesTextColor, true)
        
        viewModel.fetchWordInfo(args.queryText, args.queryLanguage)
        viewModel.fetchWordInfoResultType.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it) {
        
                    //FetchResult was failed (this logic is included in fetchWordInformation()) and now
                    //we wanna show an error message now and every time a configuration change happens
                    //until fetchWordInformation() is called again
                    ResultViewModel.NetworkRequestResult.Failure -> showError(viewModel.errorType)
        
                    //FetchResult was successful and now we wanna show the result fetched from the
                    //server now and every time a configuration change happens until
                    //fetchWordInformation() is called again
                    ResultViewModel.NetworkRequestResult.Success -> showResults(viewModel.wordInfo)
                }
            }
        }
    }
    
    //show the right error message based on what went wrong
    private fun showError(state: ResultViewModel.ErrorType) {
        fragmentResultBinding.loadingAnim.visibility = View.GONE
        
        //make errorMessage visible if it's not already
        fragmentResultBinding.loadingErrorMessage.visibility = View.VISIBLE
        when (state) {
            ResultViewModel.ErrorType.CallFailed -> fragmentResultBinding.loadingErrorMessage.text = getString(R.string.call_failed)
            ResultViewModel.ErrorType.NoMatch -> fragmentResultBinding.loadingErrorMessage.text = getString(R.string.no_match)
        }
    }
    
    //this function takes the output from fetchWordsInfo() and gets it in a structured manner back from the methods in the viewmodel then adds that
    //info one by one to the result_linear_layout
    private fun showResults(response: Response<List<Word>>) {
        
        //hide the loading animation
        fragmentResultBinding.loadingAnim.visibility = View.GONE
        fragmentResultBinding.resultLinearLayout.removeAllViews()
        
        //get the response output by the fetchWordInfo()
        val wordsList = response.body()!!
        
        for (WORD in wordsList) {
            
            //inflate word_item_layout to add information about the current word from the response
            val wordLayoutBinding = WordLayoutBinding.inflate(layoutInflater)
            
            //add the the word to word_title_textview
            wordLayoutBinding.wordTitleTextview.text = WORD.word
            
            //for some weird cases, words don't have available definitions, just information about their phonetics. We're going to skip such words
            if (WORD.meanings.isEmpty()) continue
            
            //if there is no provided information about the origin, don't add anything to the textview and set the textview to gone
            if (WORD.origin.isNullOrEmpty()) wordLayoutBinding.originTextview.visibility = View.GONE
            else wordLayoutBinding.originTextview.text = getString(R.string.origin_info, WORD.origin)
            
            //add the each meaning_layout to the current word_item_layout
            val meaningsLayoutArray = getMeaningsLayoutList(WORD, wordLayoutBinding)
            meaningsLayoutArray.forEach { wordLayoutBinding.wordLinearLayout.addView(it.root) }
            
            //add the word_item_layout for the current word to results_linear_layout
            fragmentResultBinding.resultLinearLayout.addView(wordLayoutBinding.root)
        }
        
        if (fragmentResultBinding.resultLinearLayout.isEmpty()) showError(ResultViewModel.ErrorType.NoMatch)
        else fragmentResultBinding.loadingErrorMessage.visibility = View.GONE
    }
    
    private fun getMeaningsLayoutList(
        word: Word,
        wordLayoutBinding: WordLayoutBinding,
    ): ArrayList<MeaningLayoutBinding> {
        val meaningLayoutArray = ArrayList<MeaningLayoutBinding>()
        
        //iterate over each provided meaning for the word
        for ((index, meaning) in word.meanings.withIndex()) {
    
            //add meanings_layout to add information about the current meanings of the current word
            val meaningLayoutBinding = MeaningLayoutBinding.inflate(layoutInflater, wordLayoutBinding.root, false)
    
            //add information about the part of speech if provided, make the textview gone if not
            if (meaning.partOfSpeech != null) meaningLayoutBinding.partofspeechTextview.text = meaning.partOfSpeech
            else meaningLayoutBinding.partofspeechTextview.visibility = View.GONE
            val definitionsText = getWordsLayoutList(meaning)
    
            if (index == word.meanings.lastIndex) {
                val meaningLayoutParams = meaningLayoutBinding.root.layoutParams as ConstraintLayout.LayoutParams
                meaningLayoutParams.setMargins(0, 0, 0, 0)
                meaningLayoutBinding.root.layoutParams = meaningLayoutParams
            }
    
            //add the definitions text to the textview
            meaningLayoutBinding.definitionsTextview.text = fromHtml(definitionsText, FROM_HTML_MODE_LEGACY)
            meaningLayoutArray.add(meaningLayoutBinding)
        }
        return meaningLayoutArray
    }
    
    private fun getWordsLayoutList(meaning: Meaning): String {
        
        //string var to add all the definitions provided for the current meaning
        var definitionsText = ""
        
        //iterate over each given definition-example pair
        for ((index, definitionExample) in meaning.definitions.withIndex()) {
    
            //add the definition
            definitionsText += getString(R.string.definition_text, index + 1, definitionExample.definition)
    
            //add a line break and then add the example if one is provided for the current pair
            if (!definitionExample.example.isNullOrEmpty()) {
                definitionsText += """<br/>"""
                definitionsText += getString(R.string.example_text, examplesTextColor.data.toString(), definitionExample.example)
            }
    
            //if it is not the last definition, add two line breaks to add the next. if it is, check if it the last meaning item and two line
            //if that condition is true to add spacing between the current and the next spacing
            if (index != meaning.definitions.lastIndex) definitionsText += """<br/><br/>"""
        }
        return definitionsText
    }
    
}
