package com.sbeve.jada.fragments.result

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sbeve.jada.retrofit_utils.RetrofitInit
import com.sbeve.jada.retrofit_utils.RetrofitInit.accessApiObject
import com.sbeve.jada.retrofit_utils.Word
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
    enum class FetchWordInfoResultType {
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
    val fetchWordInfoResultType = MutableLiveData<FetchWordInfoResultType>()
    private lateinit var query: String

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
                        fetchWordInfoResultType.value = FetchWordInfoResultType.Failure
                        return
                    }

                    //pass the response body to outputResponse to be used by updateUI() to show the
                    //result on the screen
                    wordInfo = response

                    //set fetchResult to Success so the fragment shows the result fetched from the
                    //server now and on every configuration change from now until fetchWordInformation
                    //is called again
                    fetchWordInfoResultType.value = FetchWordInfoResultType.Success
                }

                override fun onFailure(call: Call<List<Word>>, t: Throwable) {

                    //set the type of error to CallFailed since a request to the server could not be
                    //made
                    errorType = ErrorType.CallFailed

                    //since the fragment has to shown an error message now and on every
                    //configuration from now until fetchWordInformation() is called again, set
                    //fetchResult to Failure
                    fetchWordInfoResultType.value = FetchWordInfoResultType.Failure
                }
            })
    }
}
