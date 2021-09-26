package com.sbeve.jada.utils.room

import androidx.room.TypeConverter
import com.sbeve.jada.models.Ids
import com.squareup.moshi.Moshi

class TypeConverters {
    
    //instance of moshi to be used for conversion
    private val moshi = Moshi.Builder()
        .build()
    
    //convert the json string to an Ids object
    @TypeConverter
    fun toIds(json: String): Ids {
        val adapter = moshi.adapter(Ids::class.java)
        return adapter.fromJson(json)!!
    }
    
    //convert the Ids object to a json string
    @TypeConverter
    fun toJson(ids: Ids): String {
        val adapter = moshi.adapter(Ids::class.java)
        return adapter.toJson(ids)
    }
}
