package com.sbeve.jada.ui.fragments

import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.testutils.CoroutinesTestRule
import com.sbeve.jada.utils.SharedPreferencesUtil
import com.sbeve.jada.utils.room.DictionaryDatabaseDAO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    
    @Mock
    lateinit var dictionaryDatabaseDAO: DictionaryDatabaseDAO
    
    @Mock
    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    
    lateinit var mainViewModel: MainViewModel
    private val recentQuery: RecentQuery = RecentQuery("word", 1)
    
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()
    
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel(dictionaryDatabaseDAO, sharedPreferencesUtil)
    }
    
    @Test
    fun addQuery_dictionaryDatabaseDao_callsAddQueryMethod(): Unit = runBlockingTest {
        mainViewModel.addQuery(recentQuery)
        verify(dictionaryDatabaseDAO).addQuery(recentQuery)
    }
    
    @Test
    fun clear_dictionaryDatabaseDao_callsClearMethod(): Unit = runBlockingTest {
        mainViewModel.clear()
        verify(dictionaryDatabaseDAO).clear()
    }
    
    @Test
    fun deleteQuery_dictionaryDatabaseDao_callsDeleteQuery(): Unit = runBlockingTest {
        mainViewModel.deleteQuery(recentQuery)
        verify(dictionaryDatabaseDAO).deleteQuery(recentQuery)
    }
    
    @Test
    fun getSavedLanguageIndex_sharedPreferencesUtil_callsGetSavedLanguagheMethod() {
        mainViewModel.getSavedLanguageIndex()
        verify(sharedPreferencesUtil).getLanguageSetting()
    }
    
    @Test
    fun updateLanguageSettingKey_sharedPreferencesUtil_callsUpdateLanguageSettingKey() {
        mainViewModel.updateLanguageSettingKey(0)
        verify(sharedPreferencesUtil).updateLanguageSetting(0)
    }
}
