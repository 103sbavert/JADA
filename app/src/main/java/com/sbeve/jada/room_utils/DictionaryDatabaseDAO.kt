package com.sbeve.jada.room_utils

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DictionaryDatabaseDAO {

    @Insert
    fun addQuery(recentQuery: RecentQuery)

    @Delete
    fun deleteQuery(recentQuery: RecentQuery)

    @Query("SELECT * FROM recent_query ORDER BY id DESC")
    fun getAllQueries(): LiveData<List<RecentQuery>>

    @Query("DELETE FROM recent_query")
    fun clear()
}
