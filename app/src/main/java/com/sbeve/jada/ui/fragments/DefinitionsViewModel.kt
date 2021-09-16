package com.sbeve.jada.ui.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbeve.jada.models.word.Word
import com.sbeve.jada.ui.fragments.DefinitionsViewModel.WordCallResult.*
import com.sbeve.jada.utils.Constants
import com.sbeve.jada.utils.retrofit.RetrofitAccessApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DefinitionsViewModel
@Inject
constructor(private var retrofitAccessApi: RetrofitAccessApi) : ViewModel() {
    
    
    sealed class WordCallResult {
        
        enum class ErrorType {
            NoMatch,
            CallFailed
        }
        
        class Success(val word: Word) : WordCallResult()
        class Error(val type: ErrorType) : WordCallResult()
    }
    
    private val _callResult = MutableLiveData<WordCallResult>()
    val callResult: LiveData<WordCallResult>
        get() = _callResult
    
    //make a call to the server
    fun fetchWordInfo(wordId: String, languageIndex: Int, lexicalCategoryId: String) {
        val savedLanguageCode = Constants.supportedDictionaries[languageIndex].languageCode
        
        //retrieve a new call object to make a request to the server
        viewModelScope.launch {
            try {
                val response = retrofitAccessApi.getDefinitions(wordId, savedLanguageCode, lexicalCategoryId)
                if (response.isSuccessful) {
                    _callResult.value = Success(response.body()!!)
                } else {
                    Log.e("TAG", "fetchWordInfo:" + response.errorBody() + ": " + response.code())
                    _callResult.value = Error(ErrorType.NoMatch)
                }
            } catch (e: Exception) {
                Log.e("TAG", "fetchWordInfo:", e)
                _callResult.value = Error(ErrorType.CallFailed)
            }
        }
    }
}
