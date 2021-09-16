package com.sbeve.jada.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LexicalCategory(
    val id: String,
    val text: String
)
