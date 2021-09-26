package com.sbeve.jada.utils

object Constants {
    
    const val SHARED_PREFERENCES_NAME = "JADA"
    const val LANGUAGE_SETTING_KEY = "language_setting"
    const val BASE_URL = "https://od-api.oxforddictionaries.com/api/v2/"
    
    //list of supported languages as dictionaries
    val supportedDictionaries = arrayOf(
        Language("English US", "en-us"),
        Language("English UK", "en-gb"),
        Language("Hindi", "hi"),
        Language("French", "fr"),
        Language("Gujarati", "gu"),
        Language("Latvian", "lv"),
        Language("Romanian", "ro"),
        Language("Tamil", "ta"),
        Language("Swahili", "sw"),
        Language("Spanish", "es")
    )
}
