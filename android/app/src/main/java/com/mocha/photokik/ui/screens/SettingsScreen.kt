package com.mocha.photokik.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mocha.photokik.data.model.AppLanguage
import com.mocha.photokik.ui.theme.*
import com.mocha.photokik.ui.components.GlowingIcon
import com.mocha.photokik.ui.components.PulsingGlowCard
import com.mocha.photokik.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val galleryStats by viewModel.galleryStats.collectAsStateWithLifecycle()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        BackgroundMedium
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Info Section with enhanced glowing
        SettingsSection(
            title = "PhotoKik",
            icon = Icons.Default.CameraAlt,
            glowColor = PhotoKikPurpleLight
        ) {
            Column {
                Text(
                    text = "Organize your photos with smart swipe gestures",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Storage stats with glowing indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "Total Photos",
                        value = galleryStats.totalPhotos.toString(),
                        glowColor = GalleryIconGlow
                    )
                    
                    StatItem(
                        label = "Storage Used",
                        value = formatFileSize(galleryStats.storageUsed),
                        glowColor = DocumentsGlow
                    )
                    
                    StatItem(
                        label = "Favorites",
                        value = galleryStats.favorites.toString(),
                        glowColor = FavoriteYellowGlow
                    )
                }
            }
        }
        
        // Personalization Section
        SettingsSection(
            title = "Personalization",
            icon = Icons.Default.Tune,
            glowColor = SettingsIconGlow
        ) {
            // Language Selection with glowing flag icon
            SettingsItem(
                title = "Language",
                subtitle = userSettings.language.displayName,
                icon = Icons.Default.Language,
                iconGlowColor = PhotoKikPurpleLight,
                onClick = { /* Language picker */ }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = userSettings.language.flag,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    GlowingIcon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Change language",
                        size = 20.dp,
                        tint = TextSecondary,
                        glowColor = GlowColorSecondary
                    )
                }
            }
            
            HorizontalDivider(
                color = DividerColor,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            // Swipe Sensitivity with enhanced UI
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Swipe Sensitivity",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Text(
                            text = "How far you need to swipe",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    
                    GlowingIcon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swipe sensitivity",
                        size = 24.dp,
                        tint = PhotoKikPurple,
                        glowColor = SwipeIconGlow,
                        animateGlow = true
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Slider(
                    value = userSettings.swipeSensitivity,
                    onValueChange = { newValue ->
                        viewModel.updateSettings(
                            userSettings.copy(swipeSensitivity = newValue)
                        )
                    },
                    valueRange = 0.1f..1.0f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = PhotoKikPurple,
                        activeTrackColor = PhotoKikPurple,
                        inactiveTrackColor = PhotoKikPurple.copy(alpha = 0.3f)
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Low",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    
                    Text(
                        text = "High",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }
        
        // Smart Features Section with glowing toggles
        SettingsSection(
            title = "Smart Features",
            icon = Icons.Default.AutoAwesome,
            glowColor = GlowColorAccent
        ) {
            SettingsToggleItem(
                title = "Auto-categorize Photos",
                subtitle = "Automatically organize photos using AI",
                icon = Icons.Default.Category,
                iconGlowColor = MemoriesGlow,
                isChecked = userSettings.autoCategorize,
                onCheckedChange = { isChecked ->
                    viewModel.updateSettings(
                        userSettings.copy(autoCategorize = isChecked)
                    )
                }
            )
            
            HorizontalDivider(
                color = DividerColor,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            SettingsToggleItem(
                title = "Auto-delete Blurry Photos",
                subtitle = "Automatically remove blurry or poor quality photos",
                icon = Icons.Default.BlurOff,
                iconGlowColor = BlurryGlow,
                isChecked = userSettings.autoDeleteBlurry,
                onCheckedChange = { isChecked ->
                    viewModel.updateSettings(
                        userSettings.copy(autoDeleteBlurry = isChecked)
                    )
                }
            )
        }
        
        // System Section
        SettingsSection(
            title = "System",
            icon = Icons.Default.Settings,
            glowColor = SettingsIconGlow
        ) {
            SettingsItem(
                title = "Privacy Policy",
                subtitle = "Learn how we protect your data",
                icon = Icons.Default.PrivacyTip,
                iconGlowColor = DocumentsGlow,
                onClick = { /* Open privacy policy */ }
            )
            
            HorizontalDivider(
                color = DividerColor,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            SettingsItem(
                title = "About PhotoKik",
                subtitle = "Version 1.0.0",
                icon = Icons.Default.Info,
                iconGlowColor = PhotoKikPurpleLight,
                onClick = { /* Open about dialog */ }
            )
        }
        
        // Footer with enhanced styling
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Made with ❤️ by PhotoKik Team",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "© 2024 PhotoKik. All rights reserved.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    glowColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    PulsingGlowCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = glowColor,
        isActive = true
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    GlowingIcon(
                        imageVector = icon,
                        contentDescription = title,
                        size = 24.dp,
                        tint = PhotoKikPurple,
                        glowColor = glowColor,
                        animateGlow = true
                    )
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconGlowColor: Color,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingIcon(
            imageVector = icon,
            contentDescription = title,
            size = 20.dp,
            tint = PhotoKikPurple,
            glowColor = iconGlowColor,
            animateGlow = true
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        
        if (trailing != null) {
            trailing()
        } else {
            GlowingIcon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                size = 20.dp,
                tint = TextSecondary,
                glowColor = GlowColorSecondary
            )
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconGlowColor: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlowingIcon(
            imageVector = icon,
            contentDescription = title,
            size = 20.dp,
            tint = PhotoKikPurple,
            glowColor = iconGlowColor,
            isSelected = isChecked,
            animateGlow = isChecked
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PhotoKikPurple,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SurfaceLight
            )
        )
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    glowColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PhotoKikPurple
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    
    return when {
        gb >= 1 -> "%.1f GB".format(gb)
        mb >= 1 -> "%.1f MB".format(mb)
        kb >= 1 -> "%.1f KB".format(kb)
        else -> "$bytes B"
    }
}
