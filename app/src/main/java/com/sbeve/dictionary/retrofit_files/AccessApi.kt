package com.sbeve.dictionary.retrofit_files

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AccessApi {
    @GET("{word_to_query}")
    fun getDefinitions(@Path("word_to_query") word: String): Call<List<Word>>
}
