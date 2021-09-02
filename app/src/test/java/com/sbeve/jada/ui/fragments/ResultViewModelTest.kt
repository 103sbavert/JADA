package com.sbeve.jada.ui.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sbeve.jada.models.Definition
import com.sbeve.jada.models.Meaning
import com.sbeve.jada.models.Phonetics
import com.sbeve.jada.models.Word
import com.sbeve.jada.testutils.CoroutinesTestRule
import com.sbeve.jada.testutils.getOrAwaitValue
import com.sbeve.jada.utils.retrofit.RetrofitAccessApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class ResultViewModelTest {
    
    @Mock
    private lateinit var retrofitAccessApi: RetrofitAccessApi
    private lateinit var resultViewModel: ResultViewModel
    
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()
    
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        resultViewModel = ResultViewModel(retrofitAccessApi)
    }
    
    @Test
    fun fetchWordInfo_successfulResponse_updatesLiveDataCorrectly() = runBlockingTest {
        `when`(retrofitAccessApi.getDefinitions(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Response.success(201, getWordList()))
        
        resultViewModel.fetchWordInfo("doesn't matter", 1)
        
        val fetchWordInfoResultType = resultViewModel.fetchWordInfoResultType.getOrAwaitValue()
        val wordInfo = resultViewModel.wordInfo
        
        assertEquals(ResultViewModel.NetworkRequestResult.Success, fetchWordInfoResultType)
        assertThrows(UninitializedPropertyAccessException::class.java) {
            resultViewModel.errorType
        }
        assertEquals(wordInfo, getWordList())
    }
    
    @Test
    fun fetchWordInfo_errorResponse_updatesLiveDataCorrectly() = runBlockingTest {
        `when`(retrofitAccessApi.getDefinitions(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Response.error(404, ResponseBody.create(MediaType.get("text/plain"), "No such word was found")))
        
        resultViewModel.fetchWordInfo("no worries", 2)
        
        val fetchWordInfoResultType = resultViewModel.fetchWordInfoResultType.getOrAwaitValue()
        val errorType = resultViewModel.errorType
        
        assertEquals(ResultViewModel.NetworkRequestResult.Error, fetchWordInfoResultType)
        assertEquals(ResultViewModel.ErrorType.NoMatch, errorType)
        assertThrows(UninitializedPropertyAccessException::class.java) {
            resultViewModel.wordInfo
        }
    }
    
    @Test
    fun fetchWordInfo_exception_doesNotUpdateLiveData() = runBlockingTest {
        `when`(retrofitAccessApi.getDefinitions(Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException())
        
        resultViewModel.fetchWordInfo("idk", 1)
        
        val fetchWordInfoResultType = resultViewModel.fetchWordInfoResultType.getOrAwaitValue()
        val errorType = resultViewModel.errorType
        
        assertEquals(fetchWordInfoResultType, ResultViewModel.NetworkRequestResult.Error)
        assertEquals(ResultViewModel.ErrorType.CallFailed, errorType)
        assertThrows(UninitializedPropertyAccessException::class.java) {
            resultViewModel.wordInfo
        }
    }
    
    private fun getWordList(): List<Word> {
        val word1 = Word("some word 1", "some language 1", getPhonetics(), getMeanings())
        val word2 = Word("some word 2", "some language 2", getPhonetics(), getMeanings())
        val word3 = Word("some word 3", "some language 3", getPhonetics(), getMeanings())
        val word4 = Word("some word 4", "some language 4", getPhonetics(), getMeanings())
        val word5 = Word("some word 5", "some language 5", getPhonetics(), getMeanings())
        return listOf(word1, word2, word3, word4, word5)
    }
    
    private fun getPhonetics(): List<Phonetics> {
        val phonetics1 = Phonetics("some phonetic 1")
        val phonetics2 = Phonetics("some phonetic 2")
        val phonetics3 = Phonetics("some phonetic 3")
        val phonetics4 = Phonetics("some phonetic 4")
        return listOf(phonetics1, phonetics2, phonetics3, phonetics4)
    }
    
    private fun getMeanings(): List<Meaning> {
        val meaning1 = Meaning("some part of speech 1", getDefinitions())
        val meaning2 = Meaning("some part of speech 2", getDefinitions())
        val meaning3 = Meaning("some part of speech 3", getDefinitions())
        val meaning4 = Meaning("some part of speech 4", getDefinitions())
        val meaning5 = Meaning("some part of speech 5", getDefinitions())
        return listOf(meaning1, meaning2, meaning3, meaning4, meaning5)
    }
    
    private fun getDefinitions(): List<Definition> {
        val definition1 = Definition("some definition 1", "some example 1")
        val definition2 = Definition("some definition 2", "some example 2")
        val definition3 = Definition("some definition 3", "some example 3")
        val definition4 = Definition("some definition 4", "some example 4")
        return listOf(definition1, definition2, definition3, definition4)
    }
}
