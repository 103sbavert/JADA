package com.sbeve.jada.utils

data class Languages(
    val names: Array<String>,
    val codes: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        
        other as Languages
        
        if (!names.contentEquals(other.names)) return false
        if (!codes.contentEquals(other.codes)) return false
        
        return true
    }
    
    override fun hashCode(): Int {
        var result = names.contentHashCode()
        result = 31 * result + codes.contentHashCode()
        return result
    }
}

