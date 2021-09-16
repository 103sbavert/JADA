package com.sbeve.jada.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "recent_query")
data class RecentQuery(
    
    @PrimaryKey
    @ColumnInfo(name = "ids")
    val ids: Ids,
    
    @ColumnInfo(name = "word")
    val word: String,
    
    @ColumnInfo(name = "language_index")
    val languageIndex: Int,
    
    @ColumnInfo(name = "lexical_category")
    val lexicalCategory: String,
    
    @ColumnInfo(name = "time_date")
    val timeDate: Long = System.currentTimeMillis()
)


@JsonClass(generateAdapter = true)
data class Ids(
    val wordId: String,
    val lexicalCategoryId: String
)
