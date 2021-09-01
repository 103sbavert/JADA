package com.sbeve.jada.utils.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sbeve.jada.models.RecentQuery

@Dao
interface DictionaryDatabaseDAO {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuery(recentQuery: RecentQuery)
    
    @Delete
    suspend fun deleteQuery(recentQuery: RecentQuery)
    
    @Query("SELECT * FROM recent_query ORDER BY time_date DESC")
    fun getAllQueries(): LiveData<List<RecentQuery>>
    
    @Query("DELETE FROM recent_query")
    suspend fun clear()
}
