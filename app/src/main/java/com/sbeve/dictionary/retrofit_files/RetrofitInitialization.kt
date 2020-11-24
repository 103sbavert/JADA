package com.sbeve.dictionary.retrofit_files

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

//initialize a retrofit access api
class RetrofitInitialization(language: String) {

    val accessApiObject: AccessApi = Retrofit
        .Builder()
        .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/$language/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AccessApi::class.java)

}

//set up the interface to be implemented by retrofit to create an access api
interface AccessApi {
    @GET("{word_to_query}")
    fun getDefinitions(@Path("word_to_query") word: String): Call<List<Word>>
}
