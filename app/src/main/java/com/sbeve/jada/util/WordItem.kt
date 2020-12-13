package com.sbeve.jada.util

data class WordItem(
    val wordTitleItem: String,
    val originItem: String?,
    val meaningsListItem: List<MeaningItem>,
)

data class MeaningItem(
    val partOfSpeechItem: String?,
    val definitions: String
)
