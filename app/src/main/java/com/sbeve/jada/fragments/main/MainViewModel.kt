package com.sbeve.jada.fragments.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbeve.jada.myApplication
import com.sbeve.jada.retrofit_utils.RetrofitInit
import com.sbeve.jada.room_utils.DictionaryDatabase
import com.sbeve.jada.room_utils.DictionaryDatabaseDAO
import com.sbeve.jada.room_utils.RecentQuery
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    private var roomDatabase = DictionaryDatabase.getInstance(myApplication.getInstance())

    private val databaseDao: DictionaryDatabaseDAO = roomDatabase.getDao()

    val allQueries = databaseDao.getAllQueries()

    fun addQuery(languageIndex: Int, query: String) {
        viewModelScope.launch {
            withContext(IO) {
                val recentQuery = RecentQuery(0, query, RetrofitInit.supportedLanguages.first[languageIndex], System.currentTimeMillis())
                databaseDao.addQuery(recentQuery)

            }
        }
    }

}
