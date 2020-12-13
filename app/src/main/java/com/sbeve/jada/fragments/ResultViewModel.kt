package com.sbeve.jada.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sbeve.jada.util.*
import com.sbeve.jada.util.RetrofitInit.accessApiObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultViewModel : ViewModel() {

    //type of failure that occurred while looking for a definition for the submitted query
    enum class ErrorType {
        CallFailed,
        NoMatch
    }

    //type of content to be shown on the screen when the fragment is recreated
    enum class FetchWordInfoResult {
        Success,
        Failure
    }

    //tells the reason behind a result not being found (network fail or no match?)
    lateinit var errorType: ErrorType

    //store the response output by enqueue call, empty if the viewmodel has just been instantiated
    lateinit var wordInfo: Response<List<Word>>

    //store information about the kind of content (error or word result) to be shown on the screen
    //next time the fragment is created. Also call the appropriate methods to finish the task
    //using the observer
    val fetchWordInfoResult = MutableLiveData<FetchWordInfoResult>(null)

    lateinit var query: String

    //make a call to the server
    fun fetchWordInfo(savedLanguageIndex: Int, queriedWord: String) {
        val savedLanguageCode = RetrofitInit.supportedLanguages.second[savedLanguageIndex]
        query = queriedWord
        //retrieve a new call object to make a request to the server
        accessApiObject
            .getDefinitions(savedLanguageCode, queriedWord)
            .enqueue(object : Callback<List<Word>> {
                override fun onResponse(call: Call<List<Word>>, response: Response<List<Word>>) {
                    if (!response.isSuccessful) {

                        //set the type of error to NoMatch since a connection to the server was
                        //successful it's just that the entered word wasn't found in the database
                        errorType = ErrorType.NoMatch

                        //since the fragment has to shown an error message now and on every
                        //configuration from now until fetchWordInformation() is called again, set
                        //fetchResult to Failure
                        fetchWordInfoResult.value = FetchWordInfoResult.Failure
                        return
                    }

                    //pass the response body to outputResponse to be used by updateUI() to show the
                    //result on the screen
                    wordInfo = response

                    //set fetchResult to Success so the fragment shows the result fetched from the
                    //server now and on every configuration change from now until fetchWordInformation
                    //is called again
                    fetchWordInfoResult.value = FetchWordInfoResult.Success
                }

                override fun onFailure(call: Call<List<Word>>, t: Throwable) {

                    //set the type of error to CallFailed since a request to the server could not be
                    //made
                    errorType = ErrorType.CallFailed

                    //since the fragment has to shown an error message now and on every
                    //configuration from now until fetchWordInformation() is called again, set
                    //fetchResult to Failure
                    fetchWordInfoResult.value = FetchWordInfoResult.Failure
                }
            })
    }

    fun getWordsItemsList(words: List<Word>): MutableList<WordItem> {

        //make the list of words to be passed to the recycler view adapter
        val wordsItems = mutableListOf<WordItem>()
        for (i in words) {

            //for some weird cases, the database does get a hit for the query but there is no
            //available meanings for the matched word. We're going to skip such cases.
            if (i.meanings.isEmpty()) continue

            //information about the current for it to be added to the wordList
            wordsItems.add(WordItem(i.word, i.origin, getMeaningsList(i.meanings)))
        }
        return wordsItems
    }

    private fun getMeaningsList(meanings: List<Meaning>): List<MeaningItem> {

        val list: MutableList<MeaningItem> = mutableListOf()
        for (meaning in meanings) {
            var contentBody = ""
            var partOfSpeech = ""
            //add information about the part of speech of the current meaning for the word
            //(don't add anything if part of speech says "undefined")
            partOfSpeech += if (meaning.partOfSpeech != "undefined") meaning.partOfSpeech else null
            contentBody += getDefinitionText(meaning)
            list.add(MeaningItem(partOfSpeech, contentBody))
        }

        return list
    }


    private fun getDefinitionText(meaning: Meaning): String {
        var subContentBody = ""

        //append each definition provided for the current meaning
        for ((_definitionObject, numbering) in meaning.definitions.zip('a'..'z')) {
            val definition = _definitionObject.definition
            val example = _definitionObject.example

            //don't add alphabet numbering if there is only definition
            if (meaning.definitions.size > 1) subContentBody += ("$numbering) ")
            subContentBody += (definition + "\n")
            if (!example.isNullOrEmpty()) subContentBody += "\t(Example: \"$example\")\n"
            if (meaning.definitions.last() != _definitionObject) subContentBody += "\n"
        }
        return subContentBody
    }
}
