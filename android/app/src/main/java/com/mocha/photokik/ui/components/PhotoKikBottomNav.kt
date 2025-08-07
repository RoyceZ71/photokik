package com.mocha.photokik.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mocha.photokik.viewmodel.Screen
import com.mocha.photokik.ui.theme.*

@Composable
fun PhotoKikBottomNav(
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        GradientStart.copy(alpha = 0.1f),
                        GradientEnd.copy(alpha = 0.1f)
                    )
                )
            )
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = BackgroundDark.copy(alpha = 0.95f),
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == Screen.SWIPE,
            onClick = { onScreenSelected(Screen.SWIPE) },
            icon = {
                GlowingIcon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Swipe",
                    size = 26.dp,
                    tint = if (currentScreen == Screen.SWIPE) PhotoKikPurple else TextSecondary,
                    glowColor = SwipeIconGlow,
                    isSelected = currentScreen == Screen.SWIPE,
                    animateGlow = currentScreen == Screen.SWIPE
                )
            },
            label = {
                Text(
                    text = "Swipe",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PhotoKikPurple,
                selectedTextColor = PhotoKikPurple,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = PhotoKikPurple.copy(alpha = 0.2f)
            )
        )
        
        NavigationBarItem(
            selected = currentScreen == Screen.GALLERY,
            onClick = { onScreenSelected(Screen.GALLERY) },
            icon = {
                GlowingIcon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Gallery",
                    size = 26.dp,
                    tint = if (currentScreen == Screen.GALLERY) KeepGreen else TextSecondary,
                    glowColor = GalleryIconGlow,
                    isSelected = currentScreen == Screen.GALLERY,
                    animateGlow = currentScreen == Screen.GALLERY
                )
            },
            label = {
                Text(
                    text = "Gallery",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KeepGreen,
                selectedTextColor = KeepGreen,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = KeepGreen.copy(alpha = 0.2f)
            )
        )
        
        NavigationBarItem(
            selected = currentScreen == Screen.TRASH,
            onClick = { onScreenSelected(Screen.TRASH) },
            icon = {
                GlowingIcon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Trash",
                    size = 26.dp,
                    tint = if (currentScreen == Screen.TRASH) KikRed else TextSecondary,
                    glowColor = TrashIconGlow,
                    isSelected = currentScreen == Screen.TRASH,
                    animateGlow = currentScreen == Screen.TRASH
                )
            },
            label = {
                Text(
                    text = "Trash",
                    style = MaterialTheme.typography.labelMedium
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = KikRed,
                selectedTextColor = KikRed,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = KikRed.copy(alpha = 0.2f)
            )
        )
    }
}
