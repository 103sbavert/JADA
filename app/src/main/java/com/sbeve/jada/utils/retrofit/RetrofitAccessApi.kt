package com.sbeve.jada.utils.retrofit

import com.sbeve.jada.models.Word
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

//set up the interface to be implemented by retrofit to create an access api
interface RetrofitAccessApi {
    
    @GET("{language_selected}/{word_to_query}")
    suspend fun getDefinitions(
        @Path("word_to_query") word: String,
        @Path("language_selected") language: String
    ): Response<List<Word>>
}

