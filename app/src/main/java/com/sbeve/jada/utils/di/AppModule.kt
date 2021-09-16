package com.sbeve.jada.utils.di

import android.content.Context
import androidx.room.Room
import com.sbeve.jada.utils.Constants
import com.sbeve.jada.utils.SharedPreferencesUtil
import com.sbeve.jada.utils.SharedPreferencesUtilImpl
import com.sbeve.jada.utils.retrofit.RetrofitAccessApi
import com.sbeve.jada.utils.room.DictionaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Headers
import okhttp3.OkHttpClient
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
    fun providesRetrofitAccessApi(): RetrofitAccessApi {
        val headers = Headers.Builder()
            .add("Accept", "application/json")
            .add("app_id", "dec33f76")
            .add("app_key", "a339ad038db622f0af596df68670c2ff")
            .build()
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                it.proceed(it.request().newBuilder().headers(headers).build())
            }
            .build()
        
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(RetrofitAccessApi::class.java)
    }
    
    @Provides
    fun providesSharedPreferencesUtil(@ApplicationContext applicationContext: Context): SharedPreferencesUtil = SharedPreferencesUtilImpl(
        applicationContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    )
}
