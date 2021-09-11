package com.sbeve.jada.utils.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sbeve.jada.models.RecentQuery

@Database(entities = [RecentQuery::class], version = 3, exportSchema = false)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun getDao(): DictionaryDatabaseDAO
}
