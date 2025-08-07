package com.mocha.photokik.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.SwipeDirection
import com.mocha.photokik.ui.theme.*
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeablePhotoCard(
    photo: Photo,
    onSwipe: (SwipeDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var hasTriggeredHaptic by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current
    
    // Enhanced mobile-first swipe thresholds
    val swipeThreshold = with(density) { 120.dp.toPx() } // Reduced for easier mobile swiping
    val hapticThreshold = swipeThreshold * 0.4f // Earlier haptic feedback
    val rotation = (offsetX / swipeThreshold * 12f).coerceIn(-12f, 12f) // Reduced rotation for mobile
    val scale = (1f - abs(offsetX) / (swipeThreshold * 4)).coerceIn(0.85f, 1f) // Gentler scaling
    
    // Enhanced mobile visual feedback
    val elevationMultiplier = if (isDragging) 2f else 1f
    val cardElevation = (16 + abs(offsetX) / 8) * elevationMultiplier
    
    // Enhanced mobile swipe direction detection
    val swipeDirection = when {
        offsetX > swipeThreshold * 0.3f -> SwipeDirection.KEEP
        offsetX < -swipeThreshold * 0.3f -> SwipeDirection.KIK
        else -> null
    }
    
    // Dynamic overlay opacity based on swipe distance
    val swipeProgress = abs(offsetX) / swipeThreshold
    val overlayAlpha = (swipeProgress * 0.5f).coerceIn(0f, 0.4f)
    
    val overlayColor = when (swipeDirection) {
        SwipeDirection.KEEP -> KeepGreen.copy(alpha = overlayAlpha)
        SwipeDirection.KIK -> KikRed.copy(alpha = overlayAlpha)
        null -> Color.Transparent
    }
    
    // Enhanced haptic feedback system for mobile
    LaunchedEffect(offsetX) {
        val currentAbsOffset = abs(offsetX)
        
        // Progressive haptic feedback
        when {
            currentAbsOffset > hapticThreshold && !hasTriggeredHaptic -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                hasTriggeredHaptic = true
            }
            currentAbsOffset > swipeThreshold * 0.8f && swipeDirection != null -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            currentAbsOffset < hapticThreshold -> {
                hasTriggeredHaptic = false
            }
        }
    }
    
    Card(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX
                translationY = offsetY * 0.2f
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
                shadowElevation = (16 + abs(offsetX) / 10).dp.toPx()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { _ ->
                        isDragging = true
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    },
                    onDragEnd = {
                        isDragging = false
                        hasTriggeredHaptic = false
                        
                        // Enhanced mobile swipe threshold with momentum consideration
                        val swipeVelocityThreshold = swipeThreshold * 0.6f
                        val shouldSwipe = abs(offsetX) > swipeVelocityThreshold
                        
                        if (shouldSwipe) {
                            // Strong haptic for successful swipe
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            
                            // Trigger swipe action
                            if (offsetX > 0) {
                                onSwipe(SwipeDirection.KEEP)
                            } else {
                                onSwipe(SwipeDirection.KIK)
                            }
                        } else {
                            // Gentle haptic for bounce back
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        
                        // Reset position (will be animated in real implementation)
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { _, dragAmount ->
                    // Enhanced mobile drag with constraints
                    offsetX += dragAmount.x * 1.2f // Slight amplification for easier swiping
                    offsetY += dragAmount.y * 0.3f // Reduced Y movement for focus on horizontal swipe
                    
                    // Constrain Y movement for better mobile UX
                    offsetY = offsetY.coerceIn(-100f, 100f)
                }
            },
        shape = RoundedCornerShape(if (isDragging) 28.dp else 24.dp), // Dynamic corner radius
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation.dp,
            pressedElevation = (cardElevation * 1.5f).dp,
            draggedElevation = (cardElevation * 2f).dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
        ) {
            // Photo with enhanced loading
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photo.filePath)
                    .crossfade(true)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .build(),
                contentDescription = photo.filename,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Overlay for swipe feedback with enhanced glowing
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = when (swipeDirection) {
                            SwipeDirection.KEEP -> Brush.radialGradient(
                                colors = listOf(
                                    KeepGreen.copy(alpha = 0.4f),
                                    KeepGreen.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                            SwipeDirection.KIK -> Brush.radialGradient(
                                colors = listOf(
                                    KikRed.copy(alpha = 0.4f),
                                    KikRed.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                            null -> Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                        }
                    )
            )
            
            // Swipe direction indicator with enhanced glowing
            swipeDirection?.let { direction ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = when (direction) {
                        SwipeDirection.KEEP -> Alignment.CenterEnd
                        SwipeDirection.KIK -> Alignment.CenterStart
                    }
                ) {
                    PulsingGlowCard(
                        modifier = Modifier
                            .size(100.dp)
                            .graphicsLayer {
                                scaleX = (abs(offsetX) / swipeThreshold).coerceIn(0.6f, 1.3f)
                                scaleY = (abs(offsetX) / swipeThreshold).coerceIn(0.6f, 1.3f)
                            },
                        glowColor = when (direction) {
                            SwipeDirection.KEEP -> KeepGreenGlow
                            SwipeDirection.KIK -> KikRedGlow
                        },
                        isActive = true
                    ) {
                        Card(
                            shape = RoundedCornerShape(50.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (direction) {
                                    SwipeDirection.KEEP -> KeepGreen
                                    SwipeDirection.KIK -> KikRed
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                GlowingIcon(
                                    imageVector = when (direction) {
                                        SwipeDirection.KEEP -> Icons.Default.Check
                                        SwipeDirection.KIK -> Icons.Default.Close
                                    },
                                    contentDescription = direction.name,
                                    size = 40.dp,
                                    tint = Color.White,
                                    glowColor = Color.White.copy(alpha = 0.6f),
                                    animateGlow = true
                                )
                            }
                        }
                    }
                }
            }
            
            // Photo info overlay with enhanced mobile design
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(
                        horizontal = if (isDragging) 32.dp else 24.dp,
                        vertical = if (isDragging) 28.dp else 24.dp
                    ) // Dynamic padding during drag
            ) {
                Text(
                    text = photo.filename,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category with glowing indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = when (photo.category) {
                                        com.mocha.photokik.data.model.PhotoCategory.MEMORIES -> KeepGreen
                                        com.mocha.photokik.data.model.PhotoCategory.DOCUMENTS -> DocumentsColor
                                        com.mocha.photokik.data.model.PhotoCategory.DUPLICATES -> DuplicatesColor
                                        com.mocha.photokik.data.model.PhotoCategory.BLURRY -> BlurryColor
                                        com.mocha.photokik.data.model.PhotoCategory.UNCATEGORIZED -> TextSecondary
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        
                        Text(
                            text = photo.category.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    
                    // Photo indicators with glowing effects
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (photo.isFavorite) {
                            GlowingIcon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favorite",
                                size = 18.dp,
                                tint = FavoriteYellow,
                                glowColor = FavoriteYellowGlow,
                                animateGlow = true
                            )
                        }
                        
                        photo.blurScore?.let { score ->
                            if (score > 0.7f) {
                                GlowingIcon(
                                    imageVector = Icons.Default.BlurOn,
                                    contentDescription = "Blurry",
                                    size = 18.dp,
                                    tint = BlurryColor,
                                    glowColor = BlurryGlow,
                                    animateGlow = true
                                )
                            }
                        }
                        
                        // File size indicator
                        photo.fileSize?.let { size ->
                            Text(
                                text = formatFileSize(size),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                // Dimensions if available
                if (photo.width != null && photo.height != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${photo.width} × ${photo.height}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Enhanced swipe hint indicators with better mobile visibility
            if (offsetX == 0f && !isDragging) {
                // Enhanced mobile-optimized hint indicators
                // Left side hint (Kik) - More prominent for mobile
                Card(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(20.dp)
                        .size(48.dp), // Larger touch target for mobile
                    colors = CardDefaults.cardColors(
                        containerColor = KikRed.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        GlowingIcon(
                            imageVector = Icons.Default.SwipeLeft,
                            contentDescription = "Swipe left to kik",
                            size = 24.dp, // Larger for mobile
                            tint = KikRed.copy(alpha = 0.8f),
                            glowColor = KikRedGlow,
                            animateGlow = true
                        )
                    }
                }
                
                // Right side hint (Keep) - More prominent for mobile
                Card(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(20.dp)
                        .size(48.dp), // Larger touch target for mobile
                    colors = CardDefaults.cardColors(
                        containerColor = KeepGreen.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        GlowingIcon(
                            imageVector = Icons.Default.SwipeRight,
                            contentDescription = "Swipe right to keep",
                            size = 24.dp, // Larger for mobile
                            tint = KeepGreen.copy(alpha = 0.8f),
                            glowColor = KeepGreenGlow,
                            animateGlow = true
                        )
                    }
                }
            }
        }
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
