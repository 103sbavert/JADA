package com.sbeve.jada.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sbeve.jada.R
import dagger.hilt.android.qualifiers.ApplicationContext

class SharedPreferencesUtilImpl(@ApplicationContext private val applicationContext: Context, mode: Int) : SharedPreferencesUtil {
    
    private val sharedPreferences = applicationContext.getSharedPreferences(applicationContext.getString(R.string.app_name), mode)
    
    private val _currentLanguage = MutableLiveData(Constants.supportedLanguages.names[getSavedLanguageIndex()])
    override val currentLanguage: LiveData<String>
        get() = _currentLanguage
    
    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }
    
    override fun getSavedLanguageIndex() = sharedPreferences.getInt(applicationContext.getString(R.string.language_setting_key), 0)
    
    override fun updateLanguageSettingKey(index: Int) {
        sharedPreferences.edit {
            putInt(applicationContext.getString(R.string.language_setting_key), index)
        }
    }
    
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            applicationContext.getString(R.string.language_setting_key) -> {
                _currentLanguage.value = Constants.supportedLanguages.names[getSavedLanguageIndex()]
            }
        }
    }
}
