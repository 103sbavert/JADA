package com.sbeve.jada.room_utils

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_query")
data class RecentQuery(
    
    @PrimaryKey
    @ColumnInfo(name = "query_text")
    val queryText: String,
    
    @ColumnInfo(name = "query_language")
    val queryLanguage: Int,
    
    @ColumnInfo(name = "time_date")
    val timeDate: Long = System.currentTimeMillis()
)
