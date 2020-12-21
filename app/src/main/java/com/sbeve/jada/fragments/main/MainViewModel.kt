package com.sbeve.jada.fragments.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbeve.jada.MyApplication
import com.sbeve.jada.retrofit_utils.RetrofitInit
import com.sbeve.jada.room_utils.DictionaryDatabase
import com.sbeve.jada.room_utils.DictionaryDatabaseDAO
import com.sbeve.jada.room_utils.RecentQuery
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    //get the application context to use it to make the database
    private val applicationContext = MyApplication.getInstance()

    //get the instance of the database
    private var roomDatabase = DictionaryDatabase.getInstance(applicationContext)

    //get the dao from the database
    private val databaseDao: DictionaryDatabaseDAO = roomDatabase.getDao()

    //get all the queries as a list of RecentQuery to show in the recycler view
    val allQueries = databaseDao.getAllQueries()

    //clear all the queries on button press
    fun clear() {
        viewModelScope.launch {
            withContext(IO) {
                databaseDao.clear()

            }
        }
    }

    //add a query whenever a new word is searched
    fun addQuery(languageIndex: Int, query: String) {
        viewModelScope.launch {
            withContext(IO) {
                val recentQuery = RecentQuery(query, RetrofitInit.supportedLanguages.first[languageIndex], System.currentTimeMillis())
                databaseDao.addQuery(recentQuery)

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}
