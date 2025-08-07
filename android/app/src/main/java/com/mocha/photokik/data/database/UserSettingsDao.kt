package com.mocha.photokik.data.database

import androidx.room.*
import com.mocha.photokik.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    
    @Query("SELECT * FROM user_settings WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL) LIMIT 1")
    fun getUserSettings(userId: String? = null): Flow<UserSettings?>
    
    @Query("SELECT * FROM user_settings WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL) LIMIT 1")
    suspend fun getUserSettingsSync(userId: String? = null): UserSettings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(settings: UserSettings): Long
    
    @Update
    suspend fun updateUserSettings(settings: UserSettings)
    
    @Delete
    suspend fun deleteUserSettings(settings: UserSettings)
    
    @Query("UPDATE user_settings SET language = :language, updated_at = datetime('now') WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL)")
    suspend fun updateLanguage(language: String, userId: String? = null)
    
    @Query("UPDATE user_settings SET swipe_sensitivity = :sensitivity, updated_at = datetime('now') WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL)")
    suspend fun updateSwipeSensitivity(sensitivity: Float, userId: String? = null)
    
    @Query("UPDATE user_settings SET auto_delete_blurry = :autoDelete, updated_at = datetime('now') WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL)")
    suspend fun updateAutoDeleteBlurry(autoDelete: Boolean, userId: String? = null)
    
    @Query("UPDATE user_settings SET auto_categorize = :autoCategorize, updated_at = datetime('now') WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL)")
    suspend fun updateAutoCategorize(autoCategorize: Boolean, userId: String? = null)
    
    @Query("UPDATE user_settings SET optimize_storage = :optimizeStorage, updated_at = datetime('now') WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL)")
    suspend fun updateOptimizeStorage(optimizeStorage: Boolean, userId: String? = null)
    
    @Query("UPDATE user_settings SET system_optimization = :systemOptimization, updated_at = datetime('now') WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL)")
    suspend fun updateSystemOptimization(systemOptimization: Boolean, userId: String? = null)
    
    @Query("DELETE FROM user_settings WHERE user_id = :userId OR (:userId IS NULL AND user_id IS NULL)")
    suspend fun resetSettings(userId: String? = null)
    
    // Analytics queries
    @Query("SELECT COUNT(*) FROM user_settings")
    suspend fun getTotalUsersCount(): Int
    
    @Query("SELECT language, COUNT(*) as count FROM user_settings GROUP BY language ORDER BY count DESC")
    suspend fun getLanguageStats(): Map<String, Int>
}
