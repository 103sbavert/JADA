package com.sbeve.jada.ui.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbeve.jada.models.Word
import com.sbeve.jada.utils.Constants
import com.sbeve.jada.utils.retrofit.RetrofitAccessApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel
@Inject
constructor(private var retrofitAccessApi: RetrofitAccessApi) : ViewModel() {
    
    //type of failure that occurred while looking for a definition for the submitted query
    enum class ErrorType {
        CallFailed,
        NoMatch
    }
    
    //type of content to be shown on the screen when the fragment is recreated
    enum class NetworkRequestResult {
        Success,
        Error
    }
    
    //store information about the kind of content (error or word result) to be shown on the screen
    //next time the fragment is created. Also call the appropriate methods to finish the task
    //using the observer
    val fetchWordInfoResultType = MutableLiveData<NetworkRequestResult>()
    
    //tells the reason behind a result not being found (network fail or no match?)
    lateinit var errorType: ErrorType
    
    //store the response output by enqueue call, empty if the viewmodel has just been instantiated
    lateinit var wordInfo: List<Word>
    
    //make a call to the server
    fun fetchWordInfo(queriedWord: String, queryLanguageIndex: Int) {
        val savedLanguageCode = Constants.supportedLanguages.codes[queryLanguageIndex]
    
        //retrieve a new call object to make a request to the server
        viewModelScope.launch {
            try {
                val response = retrofitAccessApi.getDefinitions(queriedWord, savedLanguageCode)
                if (response.isSuccessful) {
    
                    //pass the response body to outputResponse to be used by updateUI() to show the
                    //result on the screen
                    wordInfo = response.body()!!
    
                    //set fetchResult to Success so the fragment shows the result fetched from the
                    //server now and on every configuration change from now until fetchWordInformation
                    //is called again
                    fetchWordInfoResultType.value = NetworkRequestResult.Success
                } else {
    
                    //set the type of error to NoMatch since a connection to the server was
                    //successful it's just that the entered word wasn't found in the database
                    errorType = ErrorType.NoMatch
    
                    //since the fragment has to shown an error message now and on every
                    //configuration from now until fetchWordInformation() is called again, set
                    //fetchResult to Failure
                    fetchWordInfoResultType.value = NetworkRequestResult.Error
                }
            } catch (e: Exception) {
    
                //set the type of error to CallFailed since a request to the server could not be
                //made
                errorType = ErrorType.CallFailed
    
                //since the fragment has to shown an error message now and on every
                //configuration from now until fetchWordInformation() is called again, set
                //fetchResult to Failure
                fetchWordInfoResultType.value = NetworkRequestResult.Error
            }
        }
    }
}
