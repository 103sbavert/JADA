package com.sbeve.jada.ui.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.utils.SharedPreferencesUtil
import com.sbeve.jada.utils.room.DictionaryDatabaseDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val databaseDao: DictionaryDatabaseDAO,
    private val sharedPreferencesUtilImpl: SharedPreferencesUtil
) : ViewModel() {
    
    //get all the queries as a list of RecentQuery to show in the recycler view
    val allQueries = databaseDao.getAllQueries()
    val currentLanguage = sharedPreferencesUtilImpl.currentLanguage
    
    //clear all the queries on button press
    fun clear() = viewModelScope.launch {
        databaseDao.clear()
    }
    
    //delete the query when the trash button is clicked
    fun deleteQuery(recentQuery: RecentQuery) = viewModelScope.launch {
        databaseDao.deleteQuery(recentQuery)
    }
    
    //add a query whenever a new word is searched
    fun addQuery(recentQuery: RecentQuery) = viewModelScope.launch {
        databaseDao.addQuery(recentQuery)
    }
    
    fun getSavedLanguageIndex() = sharedPreferencesUtilImpl.getSavedLanguageIndex()
    
    fun updateLanguageSettingKey(index: Int) = sharedPreferencesUtilImpl.updateLanguageSettingKey(index)
}
