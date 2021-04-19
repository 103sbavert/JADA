package com.sbeve.jada.room_utils

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecentQuery::class], version = 3, exportSchema = false)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun getDao(): DictionaryDatabaseDAO
}
