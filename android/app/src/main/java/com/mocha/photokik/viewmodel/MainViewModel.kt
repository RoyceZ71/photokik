package com.mocha.photokik.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.PhotoCategory
import com.mocha.photokik.data.model.UserSettings
import com.mocha.photokik.data.model.SwipeAction
import com.mocha.photokik.data.model.SwipeDirection
import com.mocha.photokik.data.model.GalleryStats
import com.mocha.photokik.repository.PhotoRepository
import com.mocha.photokik.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    // Photos data
    val allPhotos: StateFlow<List<Photo>> = photoRepository.getAllActivePhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val deletedPhotos: StateFlow<List<Photo>> = photoRepository.getDeletedPhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val favoritePhotos: StateFlow<List<Photo>> = photoRepository.getFavoritePhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val galleryStats: StateFlow<GalleryStats> = photoRepository.getGalleryStats()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GalleryStats(0, 0, 0, 0, 0, 0, 0L)
        )
    
    // User settings
    val userSettings: StateFlow<UserSettings> = settingsRepository.getUserSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )
    
    // Current swipe photo index
    private val _currentSwipeIndex = MutableStateFlow(0)
    val currentSwipeIndex: StateFlow<Int> = _currentSwipeIndex.asStateFlow()
    
    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Filtered photos based on search
    val filteredPhotos: StateFlow<List<Photo>> = combine(
        allPhotos,
        searchQuery
    ) { photos, query ->
        if (query.isEmpty()) {
            photos
        } else {
            // This would be replaced with the actual search from repository
            photos.filter { photo ->
                photo.filename.contains(query, ignoreCase = true) ||
                photo.aiTags?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    init {
        // Initialize default settings if they don't exist
        viewModelScope.launch {
            val existingSettings = settingsRepository.getUserSettingsSync()
            if (existingSettings.id == 0L) {
                settingsRepository.insertUserSettings(UserSettings())
            }
        }
    }
    
    // Navigation actions
    fun setCurrentScreen(screen: Screen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }
    
    fun setLoading(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
    }
    
    fun setError(error: String?) {
        _uiState.value = _uiState.value.copy(error = error)
    }
    
    // Photo actions
    fun handleSwipeAction(photoId: Long, direction: SwipeDirection) {
        viewModelScope.launch {
            try {
                when (direction) {
                    SwipeDirection.KEEP -> {
                        // Keep photo - optionally mark as favorite or categorize
                        photoRepository.updatePhotoCategory(photoId, PhotoCategory.MEMORIES)
                    }
                    SwipeDirection.KIK -> {
                        // Move to trash
                        photoRepository.moveToTrash(photoId)
                    }
                }
                
                // Move to next photo
                _currentSwipeIndex.value = _currentSwipeIndex.value + 1
                
            } catch (e: Exception) {
                setError("Error processing swipe action: ${e.message}")
            }
        }
    }
    
    fun addPhoto(photo: Photo) {
        viewModelScope.launch {
            try {
                photoRepository.insertPhoto(photo)
            } catch (e: Exception) {
                setError("Error adding photo: ${e.message}")
            }
        }
    }
    
    fun toggleFavorite(photoId: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                photoRepository.updateFavoriteStatus(photoId, isFavorite)
            } catch (e: Exception) {
                setError("Error updating favorite status: ${e.message}")
            }
        }
    }
    
    fun restorePhoto(photoId: Long) {
        viewModelScope.launch {
            try {
                photoRepository.restoreFromTrash(photoId)
            } catch (e: Exception) {
                setError("Error restoring photo: ${e.message}")
            }
        }
    }
    
    fun permanentlyDeletePhoto(photoId: Long) {
        viewModelScope.launch {
            try {
                val photo = photoRepository.getPhotoById(photoId)
                photo?.let {
                    photoRepository.deletePhoto(it)
                }
            } catch (e: Exception) {
                setError("Error deleting photo: ${e.message}")
            }
        }
    }
    
    fun emptyTrash() {
        viewModelScope.launch {
            try {
                photoRepository.emptyTrash()
            } catch (e: Exception) {
                setError("Error emptying trash: ${e.message}")
            }
        }
    }
    
    // Settings actions
    fun updateSettings(settings: UserSettings) {
        viewModelScope.launch {
            try {
                settingsRepository.updateUserSettings(settings)
            } catch (e: Exception) {
                setError("Error updating settings: ${e.message}")
            }
        }
    }
    
    // Search actions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    // Reset swipe index
    fun resetSwipeIndex() {
        _currentSwipeIndex.value = 0
    }
}

data class MainUiState(
    val currentScreen: Screen = Screen.SWIPE,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class Screen {
    SWIPE,
    GALLERY,
    TRASH,
    SETTINGS
}
