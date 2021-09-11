package com.sbeve.jada.utils

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

interface SharedPreferencesUtil : SharedPreferences.OnSharedPreferenceChangeListener {
    
    val currentLanguage: LiveData<String>
    
    fun getSavedLanguageIndex(): Int
    fun updateLanguageSettingKey(index: Int)
}
