package com.sbeve.jada.utils.room

import androidx.room.TypeConverter
import com.sbeve.jada.models.Ids
import com.squareup.moshi.Moshi

class TypeConverters {
    
    private val moshi = Moshi.Builder()
        .build()
    
    @TypeConverter
    fun toStringPair(json: String): Ids {
        val adapter = moshi.adapter(Ids::class.java)
        return adapter.fromJson(json)!!
    }
    
    @TypeConverter
    fun toJson(pair: Ids): String {
        val adapter = moshi.adapter(Ids::class.java)
        return adapter.toJson(pair)
    }
}
