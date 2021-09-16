package com.sbeve.jada.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class SharedPreferencesUtilImpl
@Inject
constructor(private val sharedPreferences: SharedPreferences) : SharedPreferencesUtil {
    
    private val _selectedLanguage = MutableLiveData(Constants.supportedDictionaries[getLanguageSetting()].languageName)
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
                _selectedLanguage.value = Constants.supportedDictionaries[getLanguageSetting()].languageName
            }
        }
    }
}
