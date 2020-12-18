package com.sbeve.jada.retrofit_utils

import com.google.gson.annotations.SerializedName

//class to be used by gson to deserialize the json output received from the server
data class Word(
    @SerializedName("word")
    val word: String,
    @SerializedName("origin")
    val origin: String?,
    @SerializedName("meanings")
    val meanings: List<Meaning>
)

data class Definition(
    @SerializedName("definition")
    val definition: String,
    @SerializedName("example")
    val example: String?,
//    @SerializedName("synonyms")
//    val synonyms: List<String>
)

data class Meaning(
    @SerializedName("partOfSpeech")
    val partOfSpeech: String?,
    @SerializedName("definitions")
    val definitions: List<Definition>
)

/*data class Phonetics(
    @SerializedName("text")
    val text: String,
    @SerializedName("audio")
    val audio: String
)*/
