package com.sbeve.jada.room_utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RecentQuery::class], version = 3, exportSchema = false)
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun getDao(): DictionaryDatabaseDAO

    companion object {

        @Volatile
        private var databaseInstance: DictionaryDatabase? = null

        fun getInstance(context: Context): DictionaryDatabase {
            synchronized(this) {
                var instance = databaseInstance
                if (instance == null) {
                    instance = Room.databaseBuilder(context, DictionaryDatabase::class.java, "dictionary_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
                databaseInstance = instance
                return instance
            }
        }

    }
}
