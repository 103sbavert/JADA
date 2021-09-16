package com.sbeve.jada.models.word

import com.sbeve.jada.models.LexicalCategory
import com.squareup.moshi.JsonClass

// Word: Array<Result>
// Result: Array<LexicalEntry>
// LexicalEntries: Array<Entry>
// Entry: Array<Sense>
// Sense: Array<Definitions>
// Definition: String

@JsonClass(generateAdapter = true)
data class Word(
    val id: String,
    val results: List<Result>,
    val word: String
)

@JsonClass(generateAdapter = true)
data class Result(
    val id: String,
    val lexicalEntries: List<LexicalEntry>,
    val word: String
)

@JsonClass(generateAdapter = true)
data class LexicalEntry(
    val entries: List<Entry>,
    val lexicalCategory: LexicalCategory,
    val text: String
)

@JsonClass(generateAdapter = true)
data class Entry(
    val senses: List<Sense>
)

@JsonClass(generateAdapter = true)
data class Sense(
    val definitions: List<String>,
    val id: String,
    val subsenses: List<Subsense>?
)

@JsonClass(generateAdapter = true)
data class Subsense(
    val definitions: List<String>,
    val id: String
)
