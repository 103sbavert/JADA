package com.sbeve.jada.room_utils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_query")
data class RecentQuery(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "query_text")
    val queryText: String,

    @ColumnInfo(name = "query_language")
    val queryLanguage: String,

    @ColumnInfo(name = "time_date")
    val timeDate: Long,

    )
