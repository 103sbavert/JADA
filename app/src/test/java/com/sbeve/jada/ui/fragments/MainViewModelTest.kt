package com.sbeve.jada.ui.fragments

import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.testutils.CoroutinesTestRule
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
    lateinit var mainViewModel: MainViewModel
    private val recentQuery: RecentQuery = RecentQuery("word", 1)
    
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()
    
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mainViewModel = MainViewModel(dictionaryDatabaseDAO)
    }
    
    @Test
    fun addQuery(): Unit = runBlockingTest {
        mainViewModel.addQuery(recentQuery)
        verify(dictionaryDatabaseDAO).addQuery(recentQuery)
    }
    
    @Test
    fun clear(): Unit = runBlockingTest {
        mainViewModel.clear()
        verify(dictionaryDatabaseDAO).clear()
    }
    
    @Test
    fun deleteQuery(): Unit = runBlockingTest {
        mainViewModel.deleteQuery(recentQuery)
        verify(dictionaryDatabaseDAO).deleteQuery(recentQuery)
    }
}
