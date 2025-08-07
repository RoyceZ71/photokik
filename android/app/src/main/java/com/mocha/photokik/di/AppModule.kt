package com.mocha.photokik.di

import android.content.Context
import androidx.room.Room
import com.mocha.photokik.data.database.PhotoKikDatabase
import com.mocha.photokik.data.database.PhotoDao
import com.mocha.photokik.data.database.UserSettingsDao
import com.mocha.photokik.repository.PhotoRepository
import com.mocha.photokik.repository.SettingsRepository
import com.mocha.photokik.utils.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun providePhotoKikDatabase(
        @ApplicationContext context: Context
    ): PhotoKikDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = PhotoKikDatabase::class.java,
            name = "photokik_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun providePhotoDao(database: PhotoKikDatabase): PhotoDao {
        return database.photoDao()
    }
    
    @Provides
    fun provideUserSettingsDao(database: PhotoKikDatabase): UserSettingsDao {
        return database.userSettingsDao()
    }
    
    @Provides
    @Singleton
    fun providePhotoRepository(photoDao: PhotoDao): PhotoRepository {
        return PhotoRepository(photoDao)
    }
    
    @Provides
    @Singleton
    fun provideSettingsRepository(userSettingsDao: UserSettingsDao): SettingsRepository {
        return SettingsRepository(userSettingsDao)
    }
    
    @Provides
    @Singleton
    fun provideCameraManager(): CameraManager {
        return CameraManager()
    }
    
    @Provides
    @Singleton
    fun providePhotoProcessor(): PhotoProcessor {
        return PhotoProcessor()
    }
    
    @Provides
    @Singleton
    fun provideGalleryManager(photoProcessor: PhotoProcessor): GalleryManager {
        return GalleryManager(photoProcessor)
    }
    
    @Provides
    @Singleton
    fun providePermissionManager(): PermissionManager {
        return PermissionManager()
    }
    
    @Provides
    @Singleton
    fun provideDuplicateDetector(): DuplicateDetector {
        return DuplicateDetector()
    }
    
    @Provides
    @Singleton
    fun provideAIPhotoAnalyzer(): AIPhotoAnalyzer {
        return AIPhotoAnalyzer()
    }
}
