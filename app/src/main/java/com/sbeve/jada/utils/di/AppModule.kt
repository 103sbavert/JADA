package com.sbeve.jada.utils.di

import android.content.Context
import androidx.room.Room
import com.sbeve.jada.utils.retrofit.RetrofitUtils
import com.sbeve.jada.utils.room.DictionaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Singleton
    @Provides
    fun providesRoomDatabase(@ApplicationContext context: Context) = synchronized(this) {
        Room.databaseBuilder(context, DictionaryDatabase::class.java, "dictionary_database")
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun providesDatabaseDAO(dictionaryDatabase: DictionaryDatabase) = dictionaryDatabase.getDao()
    
    @Singleton
    @Provides
    fun providesRetrofitAccessApi(): RetrofitUtils.AccessApi = Retrofit.Builder()
        .baseUrl(RetrofitUtils.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(RetrofitUtils.AccessApi::class.java)
}
