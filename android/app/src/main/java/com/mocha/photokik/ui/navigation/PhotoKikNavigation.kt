package com.mocha.photokik.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mocha.photokik.ui.screens.SwipeScreen
import com.mocha.photokik.ui.screens.GalleryScreen
import com.mocha.photokik.ui.screens.TrashScreen
import com.mocha.photokik.ui.screens.SettingsScreen
import com.mocha.photokik.ui.components.PhotoKikTopBar
import com.mocha.photokik.ui.components.PhotoKikBottomNav
import com.mocha.photokik.viewmodel.MainViewModel
import com.mocha.photokik.viewmodel.Screen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoKikNavigation(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val currentScreen = uiState.currentScreen
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            PhotoKikTopBar(
                currentScreen = currentScreen,
                onNavigateToSettings = { mainViewModel.setCurrentScreen(Screen.SETTINGS) }
            )
        },
        bottomBar = {
            PhotoKikBottomNav(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    mainViewModel.setCurrentScreen(screen)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Screen content with transitions
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    slideInHorizontally { width -> width } togetherWith 
                    slideOutHorizontally { width -> -width }
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.SWIPE -> {
                        SwipeScreen(
                            viewModel = mainViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Screen.GALLERY -> {
                        GalleryScreen(
                            viewModel = mainViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Screen.TRASH -> {
                        TrashScreen(
                            viewModel = mainViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Screen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = mainViewModel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            
            // Loading overlay
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Error handling
            uiState.error?.let { error ->
                LaunchedEffect(error) {
                    // Show error snackbar or dialog
                    // This would be implemented with SnackbarHostState
                }
            }
        }
    }
}

// Navigation items data class
data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val navigationItems = listOf(
    NavItem(Screen.SWIPE, Icons.Filled.SwapHoriz, "Swipe"),
    NavItem(Screen.GALLERY, Icons.Filled.PhotoLibrary, "Gallery"),
    NavItem(Screen.TRASH, Icons.Filled.Delete, "Trash"),
    NavItem(Screen.SETTINGS, Icons.Filled.Settings, "Settings")
)
