package com.sbeve.jada.ui.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.models.lemma.LexicalEntry
import com.sbeve.jada.ui.fragments.LemmaViewModel.LemmaCallResult.*
import com.sbeve.jada.utils.Constants
import com.sbeve.jada.utils.retrofit.RetrofitAccessApi
import com.sbeve.jada.utils.room.DictionaryDatabaseDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LemmaViewModel
@Inject
constructor(
    private val databaseDAO: DictionaryDatabaseDAO,
    private val accessApi: RetrofitAccessApi
) : ViewModel() {
    
    sealed class LemmaCallResult {
        
        enum class ErrorType {
            NoMatch,
            CallFailed
        }
        
        class Success(val lexicalEntries: List<LexicalEntry>) : LemmaCallResult()
        class Error(val type: ErrorType) : LemmaCallResult()
    }
    
    private val _callResult = MutableLiveData<LemmaCallResult>()
    val callResult: LiveData<LemmaCallResult>
        get() = _callResult
    
    fun fetchLemmas(queryText: String, queryLanguageIndex: Int) = viewModelScope.launch {
        try {
            val response = accessApi.getLemmas(queryText, Constants.supportedDictionaries[queryLanguageIndex].languageCode)
            if (response.isSuccessful) {
                val lexicalEntries = response.body()!!.results[0].lexicalEntries.filter { it.lexicalCategory.id != "other" }
                _callResult.value = Success(lexicalEntries)
            } else {
                _callResult.value = Error(ErrorType.NoMatch)
                Log.e(LemmaViewModel::class.simpleName, "fetchLemmas:" + response.code() + ": $queryText")
            }
        } catch (e: Exception) {
            Log.e(LemmaViewModel::class.simpleName, "fetchLemmas:", e)
            _callResult.value = Error(ErrorType.CallFailed)
        }
    }
    
    fun addQuery(recentQuery: RecentQuery) = viewModelScope.launch {
        databaseDAO.addQuery(recentQuery)
    }
}
