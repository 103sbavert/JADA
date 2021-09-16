package com.sbeve.jada.utils.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sbeve.jada.models.RecentQuery

@Database(entities = [RecentQuery::class], version = 4, exportSchema = false)
@TypeConverters(com.sbeve.jada.utils.room.TypeConverters::class)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun getDao(): DictionaryDatabaseDAO
}
