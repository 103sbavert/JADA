package com.sbeve.jada.utils.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.sbeve.jada.models.Ids
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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DictionaryDatabaseDAOTest {
    
    private lateinit var dictionaryDatabase: DictionaryDatabase
    private lateinit var dictionaryDatabaseDAO: DictionaryDatabaseDAO
    private val typeConverters = TypeConverters()
    
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
    fun addQuery_uniqueSampleRecentQuery_addsRecentQueryToTheDatabase() = runBlockingTest {
        val sampleRecentQuery = getSampleRecentQuery()
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery)
        
        val selectAllSqliteQuery = SupportSQLiteQueryBuilder.builder("recent_query")
            .orderBy("time_date")
            .create()
        val cursor = dictionaryDatabase.query(selectAllSqliteQuery)
        cursor.moveToFirst()
        val idsJson = cursor.getString(cursor.getColumnIndexOrThrow("ids"))
        val ids = typeConverters.toIds(idsJson)
        val word = cursor.getString(cursor.getColumnIndexOrThrow("word"))
        val languageIndex = cursor.getInt(cursor.getColumnIndexOrThrow("language_index"))
        val lexicalCategory = cursor.getString(cursor.getColumnIndexOrThrow("lexical_category"))
        val time = cursor.getLong(cursor.getColumnIndexOrThrow("time_date"))
        val returnedRecentQuery = RecentQuery(ids, word, languageIndex, lexicalCategory, time)
        assertEquals(sampleRecentQuery, returnedRecentQuery)
        
        cursor.close()
    }
    
    @Test
    fun addQuery_twoRecentQueryWithSamePrimaryKey_oneReplacesTheOther() = runBlockingTest {
        val sampleRecentQuery1 = getSampleRecentQuery(Ids("wordId", "lexicalCategoryId"))
        val sampleRecentQuery2 = getSampleRecentQuery(Ids("wordId", "lexicalCategoryId"))
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery1)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery2)
        
        val selectAllSqliteQuery = SupportSQLiteQueryBuilder.builder("recent_query")
            .orderBy("time_date")
            .create()
        val cursor = dictionaryDatabase.query(selectAllSqliteQuery)
        cursor.moveToFirst()
        val ids = cursor.getString(cursor.getColumnIndexOrThrow("ids"))
        val idPair = typeConverters.toIds(ids)
        val word = cursor.getString(cursor.getColumnIndexOrThrow("word"))
        val languageIndex = cursor.getInt(cursor.getColumnIndexOrThrow("language_index"))
        val lexicalCategory = cursor.getString(cursor.getColumnIndexOrThrow("lexical_category"))
        val time = cursor.getLong(cursor.getColumnIndexOrThrow("time_date"))
        val returnedRecentQuery = RecentQuery(idPair, word, languageIndex, lexicalCategory, time)
        assertEquals(cursor.count, 1)
        assertEquals(sampleRecentQuery2, returnedRecentQuery)
        
        cursor.close()
    }
    
    @Test
    fun deleteQuery_addingFourAndDeletingOneQuery_sizeIsThree() = runBlockingTest {
        val sampleRecentQuery0 = getSampleRecentQuery(Ids("word0", "word0"))
        val sampleRecentQuery1 = getSampleRecentQuery(Ids("word1", "word1"))
        val sampleRecentQuery2 = getSampleRecentQuery(Ids("word2", "word2"))
        val sampleRecentQuery3 = getSampleRecentQuery(Ids("word3", "word3"))
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery0)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery1)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery2)
        dictionaryDatabaseDAO.addQuery(sampleRecentQuery3)
    
        dictionaryDatabaseDAO.deleteQuery(sampleRecentQuery0)
    
        val selectAllSqliteQuery = SupportSQLiteQueryBuilder.builder("recent_query")
            .orderBy("time_date")
            .create()
        val cursor = dictionaryDatabase.query(selectAllSqliteQuery)
        assertEquals(cursor.count, 3)
        
        cursor.close()
    }
    
    @Test
    fun getAllQueries_addMultipleRecentQueries_returnsLiveDataWithInsertedRecentQueries() = runBlockingTest {
        val sampleRecentQuery1 = getSampleRecentQuery(Ids("first", "first"))
        val sampleRecentQuery2 = getSampleRecentQuery(Ids("second", "second"))
        val sampleRecentQuery3 = getSampleRecentQuery(Ids("third", "third"))
        val sampleRecentQuery4 = getSampleRecentQuery(Ids("fourth", "fourth"))
    
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
    
    private fun getSampleRecentQuery(primaryKey: Ids = Ids("word_id", "lexical_category_id")): RecentQuery {
        return RecentQuery(primaryKey, "word", 0, "lexical_category")
    }
}
