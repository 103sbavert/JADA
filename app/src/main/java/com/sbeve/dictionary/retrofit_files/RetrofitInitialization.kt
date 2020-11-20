package com.sbeve.dictionary.retrofit_files

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitialization(val language: String) {

    val accessApiObject: AccessApi = Retrofit
        .Builder()
        .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/$language/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AccessApi::class.java)

}
