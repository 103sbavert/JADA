package com.sbeve.jada.utils.room

import androidx.test.filters.SmallTest
import com.sbeve.jada.models.Ids
import com.squareup.moshi.JsonDataException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.EOFException

@SmallTest
class TypeConvertersTest {
    
    private lateinit var typeConverters: TypeConverters
    
    @Before
    fun setup() {
        typeConverters = TypeConverters()
    }
    
    @Test
    fun toIds_aValidJsonElementWithTwoProperties_returnsAValidIdsObject() {
        val jsonString = """{"wordId":"word_id","lexicalCategoryId":"lexical_category_id"}"""
        
        val result = typeConverters.toIds(jsonString)
        
        assertEquals(Ids("word_id", "lexical_category_id"), result)
    }
    
    @Test
    fun toIds_aNonTranslatableJson_throwsError() {
        val jsonString = """[ {"wordId":"word_id"}, {"lexicalCategoryId":"lexical_category_id"} ]"""
        
        assertThrows(JsonDataException::class.java) {
            typeConverters.toIds(jsonString)
        }
    }
    
    @Test
    fun toIds_anInvalidJson_throwsException() {
        val jsonString = ""
        
        assertThrows(EOFException::class.java) {
            typeConverters.toIds(jsonString)
        }
    }
    
    @Test
    fun toJson_aValidIdsElement_aValidJsonString() {
        val ids = Ids("word_id", "lexical_category_id")
        
        val result = typeConverters.toJson(ids)
        
        val expected = """{"wordId":"word_id","lexicalCategoryId":"lexical_category_id"}"""
        assertEquals(expected, result)
    }
}
