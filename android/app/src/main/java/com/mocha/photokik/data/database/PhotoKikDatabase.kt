package com.mocha.photokik.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.UserSettings

@Database(
    entities = [
        Photo::class,
        UserSettings::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class PhotoKikDatabase : RoomDatabase() {
    
    abstract fun photoDao(): PhotoDao
    abstract fun userSettingsDao(): UserSettingsDao
    
    companion object {
        @Volatile
        private var INSTANCE: PhotoKikDatabase? = null
        
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to user_settings table
                database.execSQL("ALTER TABLE user_settings ADD COLUMN optimize_storage INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE user_settings ADD COLUMN system_optimization INTEGER NOT NULL DEFAULT 1")
            }
        }
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration from version 1 to 2
                // Recreate photos table with proper column names
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS photos_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        filename TEXT NOT NULL,
                        file_path TEXT NOT NULL,
                        file_size INTEGER,
                        width INTEGER,
                        height INTEGER,
                        category TEXT NOT NULL DEFAULT 'UNCATEGORIZED',
                        is_favorite INTEGER NOT NULL DEFAULT 0,
                        is_deleted INTEGER NOT NULL DEFAULT 0,
                        blur_score REAL,
                        ai_tags TEXT,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                """.trimIndent())
                
                // Copy data from old table if it exists
                database.execSQL("""
                    INSERT INTO photos_new (id, filename, file_path, file_size, width, height, 
                                          category, is_favorite, is_deleted, blur_score, ai_tags, 
                                          created_at, updated_at)
                    SELECT id, filename, file_path, file_size, width, height,
                           category, is_favorite, is_deleted, blur_score, ai_tags,
                           created_at, updated_at
                    FROM photos WHERE EXISTS (SELECT 1 FROM sqlite_master WHERE type='table' AND name='photos')
                """.trimIndent())
                
                // Drop old table and rename new one
                database.execSQL("DROP TABLE IF EXISTS photos")
                database.execSQL("ALTER TABLE photos_new RENAME TO photos")
                
                // Recreate user_settings table with proper column names
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS user_settings_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        user_id TEXT,
                        language TEXT NOT NULL DEFAULT 'en',
                        swipe_sensitivity REAL NOT NULL DEFAULT 0.5,
                        auto_delete_blurry INTEGER NOT NULL DEFAULT 0,
                        auto_categorize INTEGER NOT NULL DEFAULT 1,
                        created_at INTEGER NOT NULL,
                        updated_at INTEGER NOT NULL
                    )
                """.trimIndent())
                
                // Copy data from old table if it exists
                database.execSQL("""
                    INSERT INTO user_settings_new (id, user_id, language, swipe_sensitivity, 
                                                  auto_delete_blurry, auto_categorize, 
                                                  created_at, updated_at)
                    SELECT id, user_id, language, swipe_sensitivity,
                           auto_delete_blurry, auto_categorize, created_at, updated_at
                    FROM user_settings WHERE EXISTS (SELECT 1 FROM sqlite_master WHERE type='table' AND name='user_settings')
                """.trimIndent())
                
                // Drop old table and rename new one
                database.execSQL("DROP TABLE IF EXISTS user_settings")
                database.execSQL("ALTER TABLE user_settings_new RENAME TO user_settings")
            }
        }
        
        fun getDatabase(context: Context): PhotoKikDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PhotoKikDatabase::class.java,
                    "photokik_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}
