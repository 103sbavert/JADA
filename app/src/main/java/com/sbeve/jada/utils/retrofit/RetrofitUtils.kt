package com.sbeve.jada.utils.retrofit

import com.sbeve.jada.models.Word
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

object RetrofitUtils {
    
    const val BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/"
    
    val supportedLanguages = Pair(
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
            "Br Portuguese",
            "Turkish",
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
            "pt-BR",
            "tr",
        )
    )
    
    //set up the interface to be implemented by retrofit to create an access api
    interface AccessApi {
        @GET("{language_selected}/{word_to_query}")
        suspend fun getDefinitions(
            @Path("word_to_query") word: String,
            @Path("language_selected") language: String
        ): Response<List<Word>>
    }
}
