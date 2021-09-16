package com.sbeve.jada.utils

import android.content.res.Resources
import android.util.TypedValue

data class Language(
    val languageName: String,
    val languageCode: String
)

fun Array<Language>.getLanguageNames(): Array<String> {
    val list = Array(size) {
        it.toString()
    }
    
    for (each in indices) {
        list[each] = this[each].languageName
    }
    
    return list
}

fun Float.dpToPixels() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    Resources.getSystem().displayMetrics
).toInt()

fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels



