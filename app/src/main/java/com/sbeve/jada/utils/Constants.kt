package com.sbeve.jada.utils

object Constants {
    
    const val SHARED_PREFERENCES_NAME = "JADA"
    const val LANGUAGE_SETTING_KEY = "language_setting"
    const val BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/"
    val supportedLanguages = Languages(
        arrayOf(
            "Hindi",
            "English",
            "Spanish",
            "French",
            "Japanese",
            "Russian",
            "German",
            "Italian",
            "Korean",
            //"Br Portuguese",
            "Turkish",
            //"Arabic"
        ),
        arrayOf(
            "hi",
            "en",
            "es",
            "fr",
            "ja",
            "ru",
            "de",
            "it",
            "ko",
            //"pt-BR",
            "tr",
            //"ar"
        )
    )
}
