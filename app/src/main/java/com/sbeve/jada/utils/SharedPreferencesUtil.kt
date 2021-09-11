package com.sbeve.jada.utils

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

interface SharedPreferencesUtil : SharedPreferences.OnSharedPreferenceChangeListener {
    
    val selectedLanguage: LiveData<String>
    
    fun getLanguageSetting(): Int
    fun updateLanguageSetting(index: Int)
}
