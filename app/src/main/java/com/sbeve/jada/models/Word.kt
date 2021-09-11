package com.sbeve.jada.models

//class to be used by gson to deserialize the json output received from the server
data class Word(
    val word: String,
    val origin: String?,
    val phonetics: List<Phonetics>,
    val meanings: List<Meaning>
)

data class Definition(
    val definition: String,
    val example: String?,
)

data class Meaning(
    val partOfSpeech: String?,
    val definitions: List<Definition>
)

data class Phonetics(
    val text: String,
//    @SerializedName("audio")
//    val audio: String
)
