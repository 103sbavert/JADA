package com.sbeve.jada.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext

class SharedPreferencesUtilImpl(@ApplicationContext private val applicationContext: Context, mode: Int) : SharedPreferencesUtil {
    
    private val sharedPreferences = applicationContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, mode)
    
    private val _selectedLanguage = MutableLiveData(Constants.supportedLanguages.names[getLanguageSetting()])
    override val selectedLanguage: LiveData<String>
        get() = _selectedLanguage
    
    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    
    override fun getLanguageSetting() = sharedPreferences.getInt(Constants.LANGUAGE_SETTING_KEY, 0)
    
    override fun updateLanguageSetting(index: Int) = sharedPreferences.edit {
        putInt(Constants.LANGUAGE_SETTING_KEY, index)
    }
    
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Constants.LANGUAGE_SETTING_KEY -> {
                _selectedLanguage.value = Constants.supportedLanguages.names[getLanguageSetting()]
            }
        }
    }
}