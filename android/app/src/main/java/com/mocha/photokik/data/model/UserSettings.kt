package com.mocha.photokik.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: String? = null,
    
    val language: AppLanguage = AppLanguage.ENGLISH,
    
    @ColumnInfo(name = "swipe_sensitivity")
    val swipeSensitivity: Float = 0.5f,
    
    @ColumnInfo(name = "auto_delete_blurry")
    val autoDeleteBlurry: Boolean = false,
    
    @ColumnInfo(name = "auto_categorize")
    val autoCategorize: Boolean = true,
    
    @ColumnInfo(name = "optimize_storage")
    val optimizeStorage: Boolean = false,
    
    @ColumnInfo(name = "system_optimization")
    val systemOptimization: Boolean = true,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)

data class LanguageOption(
    val language: AppLanguage,
    val isSelected: Boolean = false
)

// Settings categories for organization
enum class SettingsCategory {
    GENERAL,
    PERSONALIZATION,
    SMART_FEATURES,
    STORAGE,
    PRIVACY,
    ABOUT
}

data class SettingsItem(
    val title: String,
    val subtitle: String? = null,
    val category: SettingsCategory,
    val isToggleable: Boolean = false,
    val isEnabled: Boolean = true,
    val onClick: (() -> Unit)? = null
)
