package com.sbeve.jada.utils

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.sbeve.jada.testutils.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@SmallTest
class SharedPreferencesUtilImplTest {
    
    @Mock
    private lateinit var sharedPreferences: SharedPreferences
    
    @Mock
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    
    private lateinit var sharedPreferencesUtilImpl: SharedPreferencesUtilImpl
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        
        sharedPreferencesUtilImpl = SharedPreferencesUtilImpl(sharedPreferences)
        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
    }
    
    @Test
    fun init_newSharedPreferences_shouldRegisterAListener() {
        verify(sharedPreferences).registerOnSharedPreferenceChangeListener(any())
    }
    
    @Test
    fun init_newSharedPreference_liveDataShouldHaveSavedValue() {
        val result = sharedPreferencesUtilImpl.selectedLanguage.getOrAwaitValue()
        
        assertEquals("English US", result)
    }
    
    @Test
    fun updateLanguageSetting_someIntegerValueAsIndex_shouldUpdateSharedPreference() {
        sharedPreferencesUtilImpl.updateLanguageSetting(0)
        
        verify(sharedPreferences).edit()
        verify(sharedPreferences.edit()).putInt(Constants.LANGUAGE_SETTING_KEY, 0)
    }
    
    @Test
    fun updateLanguageSetting_someIntegerValue_shouldUpdateLiveData() {
        sharedPreferencesUtilImpl.updateLanguageSetting(0)
        
        val result = sharedPreferencesUtilImpl.selectedLanguage.getOrAwaitValue()
        assertEquals(Constants.supportedDictionaries[0].languageName, result)
    }
    
    @Test
    fun getLanguageSetting_newSharedPreferences_shouldInvokeGetInt() {
        sharedPreferencesUtilImpl.getLanguageSetting()
        
        verify(sharedPreferences, atLeastOnce()).getInt(Constants.LANGUAGE_SETTING_KEY, 0)
    }
}

