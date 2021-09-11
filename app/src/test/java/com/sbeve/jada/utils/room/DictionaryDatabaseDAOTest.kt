package com.sbeve.jada.utils.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sbeve.jada.models.RecentQuery
import com.sbeve.jada.testutils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class DictionaryDatabaseDAOTest {
    
    lateinit var dictionaryDatabase: DictionaryDatabase
    lateinit var dictionaryDatabaseDAO: DictionaryDatabaseDAO
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @Before
    fun setUp() {
        dictionaryDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), DictionaryDatabase::class.java)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        
        dictionaryDatabaseDAO = dictionaryDatabase.getDao()
    }
    
    @Test
    fun addQuery_sampleRecentQuery_addsRecentQueryToTheDatabase() = runBlockingTest {
        val sampleRecentQuery = getSampleRecentQuery()
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery)
        
        
        val selectAllSqliteQuery = SupportSQLiteQueryBuilder.builder("recent_query")
            .orderBy("time_date")
            .create()
        val cursor = dictionaryDatabase.query(selectAllSqliteQuery)
        cursor.moveToFirst()
        val queryText = cursor.getString(cursor.getColumnIndexOrThrow("query_text"))
        val queryLanguage = cursor.getInt(cursor.getColumnIndexOrThrow("query_language"))
        val time = cursor.getLong(cursor.getColumnIndexOrThrow("time_date"))
        val returnedRecentQuery = RecentQuery(queryText, queryLanguage, time)
        
        assertEquals(sampleRecentQuery, returnedRecentQuery)
        
        cursor.close()
    }
    
    @Test
    fun deleteQuery_addingFourAndDeletingOneQuery_sizeIsThree() = runBlockingTest {
        val sampleRecentQuery0 = getSampleRecentQuery("word0")
        val sampleRecentQuery1 = getSampleRecentQuery("word1")
        val sampleRecentQuery2 = getSampleRecentQuery("word2")
        val sampleRecentQuery3 = getSampleRecentQuery("word3")
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery0)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery1)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery2)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery3)
        
        dictionaryDatabaseDAO.deleteQuery(sampleRecentQuery0)
        
        val selectAllSqliteQuery = SupportSQLiteQueryBuilder.builder("recent_query")
            .orderBy("time_date")
            .create()
        val cursor = dictionaryDatabase.query(selectAllSqliteQuery)
        assertEquals(3, cursor.count)
        
        cursor.close()
    }
    
    @Test
    fun getAllQueries_addMultipleRecentQueries_returnsLiveDataWithInsertedRecentQueries() = runBlockingTest {
        val sampleRecentQuery1 = getSampleRecentQuery("first")
        val sampleRecentQuery2 = getSampleRecentQuery("second")
        val sampleRecentQuery3 = getSampleRecentQuery("third")
        val sampleRecentQuery4 = getSampleRecentQuery("fourth")
        
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery1)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery2)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery3)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery4)
        
        val results = dictionaryDatabaseDAO.getAllQueries().getOrAwaitValue()
        
        for (each in listOf(sampleRecentQuery1, sampleRecentQuery2, sampleRecentQuery3, sampleRecentQuery4).zip(results)) {
            assertEquals(each.first, each.second)
        }
    }
    
    @After
    fun tearDown() {
        dictionaryDatabase.close()
    }
    
    private fun getSampleRecentQuery(primaryKey: String = "word"): RecentQuery {
        return RecentQuery(primaryKey, 0)
    }
}
