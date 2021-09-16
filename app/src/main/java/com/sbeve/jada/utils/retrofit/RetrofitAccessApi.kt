package com.sbeve.jada.utils.retrofit

import com.sbeve.jada.models.lemma.Lemma
import com.sbeve.jada.models.word.Word
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//set up the interface to be implemented by retrofit to create an access api
interface RetrofitAccessApi {
    
    @GET("entries/{language_code}/{word_id}?fields=definitions&strictMatch=true")
    suspend fun getDefinitions(
        @Path("word_id") word: String,
        @Path("language_code") language: String,
        @Query("lexicalCategory") lexicalCategoryId: String
    ): Response<Word>
    
    @GET("lemmas/{language_code}/{word_id}")
    suspend fun getLemmas(
        @Path("word_id") wordId: String,
        @Path("language_code") languageCode: String
    ): Response<Lemma>
}

