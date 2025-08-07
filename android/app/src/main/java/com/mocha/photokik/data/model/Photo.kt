package com.mocha.photokik.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val filename: String,
    
    @ColumnInfo(name = "file_path")
    val filePath: String,
    
    @ColumnInfo(name = "file_size")
    val fileSize: Long? = null,
    
    val width: Int? = null,
    val height: Int? = null,
    val category: PhotoCategory = PhotoCategory.UNCATEGORIZED,
    
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false,
    
    @ColumnInfo(name = "blur_score")
    val blurScore: Float? = null,
    
    @ColumnInfo(name = "ai_tags")
    val aiTags: String? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)

enum class PhotoCategory(val displayName: String) {
    MEMORIES("Memories"),
    DOCUMENTS("Documents"),
    DUPLICATES("Duplicates"),
    BLURRY("Blurry"),
    UNCATEGORIZED("Uncategorized")
}

data class PhotoWithStats(
    val photo: Photo,
    val duplicateCount: Int = 0,
    val similarPhotos: List<Photo> = emptyList()
)

data class GalleryStats(
    val totalPhotos: Int,
    val memories: Int,
    val documents: Int,
    val duplicates: Int,
    val blurry: Int,
    val favorites: Int,
    val storageUsed: Long
)
