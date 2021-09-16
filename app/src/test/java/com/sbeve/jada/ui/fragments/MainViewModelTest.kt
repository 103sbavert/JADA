package com.sbeve.jada.ui.fragments

import androidx.test.filters.SmallTest
import com.sbeve.jada.models.Ids
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
@SmallTest
class MainViewModelTest {
    
    @Mock
    lateinit var dictionaryDatabaseDAO: DictionaryDatabaseDAO
    
    @Mock
    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    
    private lateinit var mainViewModel: MainViewModel
    private val recentQuery: RecentQuery = RecentQuery(Ids("word", "verb"), "word", 1, "Verb")
    
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mainViewModel = MainViewModel(dictionaryDatabaseDAO, sharedPreferencesUtil)
    }

//    @Test
//    fun addQuery_dictionaryDatabaseDao_callsAddQueryMethod(): Unit = runBlockingTest {
//        mainViewModel.addQuery(recentQuery)
//        verify(dictionaryDatabaseDAO).addQuery(recentQuery)
//    }
    
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
        println(mainViewModel.getSavedLanguageIndex())
        verify(sharedPreferencesUtil).getLanguageSetting()
    }
    
    @Test
    fun updateLanguageSettingKey_sharedPreferencesUtil_callsUpdateLanguageSettingKey() {
        mainViewModel.updateLanguageSettingKey(0)
        verify(sharedPreferencesUtil).updateLanguageSetting(0)
    }
}
