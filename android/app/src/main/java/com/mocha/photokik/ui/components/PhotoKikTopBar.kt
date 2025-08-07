package com.mocha.photokik.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mocha.photokik.viewmodel.Screen
import com.mocha.photokik.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoKikTopBar(
    currentScreen: Screen,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentScreen) {
                        Screen.SWIPE -> "PhotoKik"
                        Screen.GALLERY -> "Gallery"
                        Screen.TRASH -> "Trash"
                        Screen.SETTINGS -> "Settings"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            if (currentScreen != Screen.SETTINGS) {
                IconButton(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SettingsIconGlow.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                radius = 50f
                            )
                        )
                ) {
                    GlowingIcon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        size = 28.dp,
                        tint = MaterialTheme.colorScheme.onBackground,
                        glowColor = SettingsIconGlow,
                        animateGlow = true
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
    )
}
