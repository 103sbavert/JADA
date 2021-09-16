package com.sbeve.jada.ui.fragments

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.sbeve.jada.models.word.Word
import com.sbeve.jada.testutils.CoroutinesTestRule
import com.sbeve.jada.testutils.getOrAwaitValue
import com.sbeve.jada.ui.fragments.DefinitionsViewModel.WordCallResult.*
import com.sbeve.jada.utils.retrofit.RetrofitAccessApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
@SmallTest
class DefinitionsViewModelTest {
    
    @Mock
    private lateinit var retrofitAccessApi: RetrofitAccessApi
    private lateinit var definitionsViewModel: DefinitionsViewModel
    
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        definitionsViewModel = DefinitionsViewModel(retrofitAccessApi)
    }
    
    @Test
    fun fetchWordInfo_successfulResponse_updatesLiveDataCorrectly() = runBlockingTest {
        `when`(retrofitAccessApi.getDefinitions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Response.success(201, getWord()))
    
        definitionsViewModel.fetchWordInfo(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())
    
        val callResult = definitionsViewModel.callResult.getOrAwaitValue()
        assertTrue(callResult is Success)
        assertEquals((callResult as Success).word, getWord())
    }
    
    @Test
    fun fetchWordInfo_errorResponse_setsCallResultLiveDataToErrorAndNoMatch() = runBlockingTest {
        `when`(retrofitAccessApi.getDefinitions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(Response.error(404, ResponseBody.create(MediaType.get("text/plain"), "No such word was found")))
        
        definitionsViewModel.fetchWordInfo(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())
        
        val callResult = definitionsViewModel.callResult.getOrAwaitValue()
        assertTrue(callResult is Error)
        assertEquals((callResult as Error).type, ErrorType.NoMatch)
    }
    
    @Test
    fun fetchWordInfo_exception_setsCallResultLiveDataToErrorAndCallFailed() = runBlockingTest {
        `when`(retrofitAccessApi.getDefinitions(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
            .thenThrow(RuntimeException())
        
        definitionsViewModel.fetchWordInfo(Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())
        
        val callResult = definitionsViewModel.callResult.getOrAwaitValue()
        assertTrue(callResult is Error)
        assertEquals((callResult as Error).type, ErrorType.CallFailed)
    }
    
    private fun getWord(): Word {
        return Word("id", emptyList(), "word")
    }
}
