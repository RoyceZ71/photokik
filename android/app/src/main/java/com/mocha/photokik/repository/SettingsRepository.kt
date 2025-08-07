package com.mocha.photokik.repository

import com.mocha.photokik.data.database.UserSettingsDao
import com.mocha.photokik.data.model.UserSettings
import com.mocha.photokik.data.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val userSettingsDao: UserSettingsDao
) {
    
    /**
     * Get user settings as Flow (reactive)
     */
    fun getUserSettings(userId: String? = null): Flow<UserSettings> = 
        userSettingsDao.getUserSettings(userId).map { settings ->
            settings ?: UserSettings() // Return default settings if none exist
        }
    
    /**
     * Get user settings synchronously
     */
    suspend fun getUserSettingsSync(userId: String? = null): UserSettings = 
        userSettingsDao.getUserSettingsSync(userId) ?: UserSettings()
    
    /**
     * Insert new user settings
     */
    suspend fun insertUserSettings(settings: UserSettings): Long = 
        userSettingsDao.insertUserSettings(settings)
    
    /**
     * Update existing user settings
     */
    suspend fun updateUserSettings(settings: UserSettings) = 
        userSettingsDao.updateUserSettings(settings)
    
    /**
     * Delete user settings
     */
    suspend fun deleteUserSettings(settings: UserSettings) = 
        userSettingsDao.deleteUserSettings(settings)
    
    /**
     * Update language preference
     */
    suspend fun updateLanguage(language: AppLanguage, userId: String? = null) = 
        userSettingsDao.updateLanguage(language.code, userId)
    
    /**
     * Update swipe sensitivity
     */
    suspend fun updateSwipeSensitivity(sensitivity: Float, userId: String? = null) = 
        userSettingsDao.updateSwipeSensitivity(sensitivity, userId)
    
    /**
     * Update auto delete blurry photos setting
     */
    suspend fun updateAutoDeleteBlurry(autoDelete: Boolean, userId: String? = null) = 
        userSettingsDao.updateAutoDeleteBlurry(autoDelete, userId)
    
    /**
     * Update auto categorize setting
     */
    suspend fun updateAutoCategorize(autoCategorize: Boolean, userId: String? = null) = 
        userSettingsDao.updateAutoCategorize(autoCategorize, userId)
    
    /**
     * Update optimize storage setting
     */
    suspend fun updateOptimizeStorage(optimizeStorage: Boolean, userId: String? = null) = 
        userSettingsDao.updateOptimizeStorage(optimizeStorage, userId)
    
    /**
     * Update system optimization setting
     */
    suspend fun updateSystemOptimization(systemOptimization: Boolean, userId: String? = null) = 
        userSettingsDao.updateSystemOptimization(systemOptimization, userId)
    
    /**
     * Reset all settings to defaults
     */
    suspend fun resetSettings(userId: String? = null) = 
        userSettingsDao.resetSettings(userId)
    
    /**
     * Get total number of users
     */
    suspend fun getTotalUsersCount(): Int = 
        userSettingsDao.getTotalUsersCount()
    
    /**
     * Get language usage statistics
     */
    suspend fun getLanguageStats(): Map<String, Int> {
        // Note: Room doesn't support Map return types directly for @Query
        // This would need to be implemented differently or use a custom query processor
        return emptyMap()
    }
    
    /**
     * Initialize default settings for a new user
     */
    suspend fun initializeDefaultSettings(userId: String? = null): UserSettings {
        val defaultSettings = UserSettings(userId = userId)
        val id = insertUserSettings(defaultSettings)
        return defaultSettings.copy(id = id)
    }
    
    /**
     * Check if settings exist for user
     */
    suspend fun hasSettings(userId: String? = null): Boolean {
        return getUserSettingsSync(userId).id != 0L
    }
    
    /**
     * Export user settings (for backup/sync)
     */
    suspend fun exportSettings(userId: String? = null): UserSettings = 
        getUserSettingsSync(userId)
    
    /**
     * Import user settings (from backup/sync)
     */
    suspend fun importSettings(settings: UserSettings, userId: String? = null): UserSettings {
        val settingsToImport = settings.copy(
            id = 0, // Reset ID to create new entry
            userId = userId
        )
        
        // Delete existing settings first
        resetSettings(userId)
        
        // Insert imported settings
        val newId = insertUserSettings(settingsToImport)
        return settingsToImport.copy(id = newId)
    }
    
    /**
     * Get app-wide preference statistics
     */
    suspend fun getPreferenceStats(): PreferenceStats {
        val allSettings = mutableListOf<UserSettings>()
        
        // This would need to be implemented based on how you store multiple user settings
        // For now, we'll get stats from the current user
        val currentSettings = getUserSettingsSync()
        
        return PreferenceStats(
            totalUsers = if (currentSettings.id != 0L) 1 else 0,
            averageSwipeSensitivity = currentSettings.swipeSensitivity,
            autoCategorizeEnabled = if (currentSettings.autoCategorize) 1 else 0,
            autoDeleteBlurryEnabled = if (currentSettings.autoDeleteBlurry) 1 else 0,
            languageDistribution = getLanguageStats()
        )
    }
    
    data class PreferenceStats(
        val totalUsers: Int,
        val averageSwipeSensitivity: Float,
        val autoCategorizeEnabled: Int,
        val autoDeleteBlurryEnabled: Int,
        val languageDistribution: Map<String, Int>
    )
}
