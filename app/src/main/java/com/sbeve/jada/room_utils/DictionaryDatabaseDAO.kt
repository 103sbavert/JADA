package com.sbeve.jada.room_utils

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DictionaryDatabaseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addQuery(recentQuery: RecentQuery)

    @Delete
    fun deleteQuery(recentQuery: RecentQuery)

    @Query("SELECT * FROM recent_query ORDER BY time_date DESC")
    fun getAllQueries(): LiveData<List<RecentQuery>>

    @Query("DELETE FROM recent_query")
    fun clear()
}
