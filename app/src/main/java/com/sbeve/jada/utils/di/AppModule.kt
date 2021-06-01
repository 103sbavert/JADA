package com.sbeve.jada.utils.di

import android.content.Context
import androidx.room.Room
import com.sbeve.jada.utils.room.DictionaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): DictionaryDatabase {
        synchronized(this) {
            return Room.databaseBuilder(context, DictionaryDatabase::class.java, "dictionary_database")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
