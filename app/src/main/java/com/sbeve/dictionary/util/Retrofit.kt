package com.sbeve.dictionary.util

import android.content.Context
import com.sbeve.dictionary.HiDictionary
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object RetrofitInit {

    var appContext: Context = HiDictionary.mContext

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
            "tr",
        )
    )

    fun changeLanguage(newLang: Int) {
        this.accessApiObject = Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/${supportedLanguages.second[newLang]}/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AccessApi::class.java)
    }


    private val savedSetting = appContext
        .getSharedPreferences("application", Context.MODE_PRIVATE)
        .getInt("language_setting_key", 0)


    var accessApiObject: AccessApi = Retrofit.Builder()
        .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/${supportedLanguages.second[savedSetting]}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AccessApi::class.java)
        private set

}

//set up the interface to be implemented by retrofit to create an access api
interface AccessApi {
    @GET("{word_to_query}")
    fun getDefinitions(@Path("word_to_query") word: String): Call<List<Word>>
}
