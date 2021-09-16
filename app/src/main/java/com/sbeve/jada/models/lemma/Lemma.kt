package com.sbeve.jada.models.lemma

import com.sbeve.jada.models.LexicalCategory
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Lemma(
    val results: List<Result>
)

@JsonClass(generateAdapter = true)
data class Result(
    val lexicalEntries: List<LexicalEntry>,
)

@JsonClass(generateAdapter = true)
data class LexicalEntry(
    val inflectionOf: List<InflectionOf>,
    val lexicalCategory: LexicalCategory,
)

@JsonClass(generateAdapter = true)
data class InflectionOf(
    val id: String,
    val text: String
)
