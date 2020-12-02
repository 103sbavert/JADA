package com.sbeve.jada.util

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object RetrofitInit {

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
            "Brazilian Portuguese",
            "Turkish"
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

    val accessApiObject: AccessApi = Retrofit.Builder()
        .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AccessApi::class.java)
}

//set up the interface to be implemented by retrofit to create an access api
interface AccessApi {
    @GET("{language_selected}/{word_to_query}")
    fun getDefinitions(
        @Path("language_selected") language: String,
        @Path("word_to_query") word: String
    ): Call<List<Word>>
}
