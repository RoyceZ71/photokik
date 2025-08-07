package com.mocha.photokik.data.database

import androidx.room.TypeConverter
import com.mocha.photokik.data.model.PhotoCategory
import com.mocha.photokik.data.model.AppLanguage
import java.util.Date

class DatabaseConverters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromPhotoCategoryString(value: String?): PhotoCategory {
        return try {
            if (value.isNullOrBlank()) PhotoCategory.UNCATEGORIZED
            else PhotoCategory.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            PhotoCategory.UNCATEGORIZED
        }
    }
    
    @TypeConverter
    fun photoCategoryToString(category: PhotoCategory?): String? {
        return category?.name
    }
    
    @TypeConverter
    fun fromAppLanguageString(value: String?): AppLanguage {
        return if (value.isNullOrBlank()) AppLanguage.ENGLISH else AppLanguage.fromCode(value)
    }
    
    @TypeConverter
    fun appLanguageToString(language: AppLanguage?): String? {
        return language?.code
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.filter { it.isNotBlank() }
    }
}
