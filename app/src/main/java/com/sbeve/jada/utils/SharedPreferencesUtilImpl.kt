package com.sbeve.jada.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class SharedPreferencesUtilImpl
@Inject
constructor(private val sharedPreferences: SharedPreferences) : SharedPreferencesUtil {
    
    //get the selected language's name as a live data
    private val _selectedLanguage = MutableLiveData(Constants.supportedDictionaries[getLanguageSetting()].languageName)
    override val selectedLanguage: LiveData<String>
        get() = _selectedLanguage
    
    init {
    
        //register an on shared preferences changed listener to update the livedata whenever the language setting changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    
    //get the index of the selected language
    override fun getLanguageSetting() = sharedPreferences.getInt(Constants.LANGUAGE_SETTING_KEY, 0)
    
    //update the preferences with the newly selected language's index
    override fun updateLanguageSetting(index: Int) = sharedPreferences.edit {
        putInt(Constants.LANGUAGE_SETTING_KEY, index)
    }
    
    //update the livedata's value whenever the setting changes
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Constants.LANGUAGE_SETTING_KEY -> {
                _selectedLanguage.value = Constants.supportedDictionaries[getLanguageSetting()].languageName
            }
        }
    }
}
