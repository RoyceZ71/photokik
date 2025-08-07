package com.mocha.photokik.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mocha.photokik.data.model.SwipeDirection
import com.mocha.photokik.ui.components.SwipeablePhotoCard
import com.mocha.photokik.ui.components.GlowingIcon
import com.mocha.photokik.ui.components.GlowingButton
import com.mocha.photokik.ui.theme.*
import com.mocha.photokik.viewmodel.MainViewModel

@Composable
fun SwipeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allPhotos by viewModel.allPhotos.collectAsStateWithLifecycle()
    val currentSwipeIndex by viewModel.currentSwipeIndex.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDark,
                        BackgroundMedium,
                        PhotoKikPurpleDark
                    )
                )
            )
    ) {
        if (allPhotos.isEmpty()) {
            // Empty state with enhanced glowing
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GlowingIcon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "No photos",
                    size = 80.dp,
                    tint = TextSecondary,
                    glowColor = GalleryIconGlow,
                    animateGlow = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "No Photos Yet",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Add some photos to start organizing them with PhotoKik!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                GlowingButton(
                    onClick = { /* Open camera or gallery */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    glowColor = PhotoKikPurpleLight,
                    animateGlow = true
                ) {
                    GlowingIcon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        size = 20.dp,
                        tint = Color.White,
                        glowColor = Color.White.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Photos",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        } else if (currentSwipeIndex >= allPhotos.size) {
            // Completed state with enhanced glowing
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GlowingIcon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    size = 80.dp,
                    tint = KeepGreen,
                    glowColor = KeepGreenGlow,
                    animateGlow = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "All Done!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "You've organized all your photos. Great job!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetSwipeIndex() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, PhotoKikPurple)
                    ) {
                        GlowingIcon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Start over",
                            size = 18.dp,
                            tint = PhotoKikPurple,
                            glowColor = PhotoKikPurpleLight
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Start Over",
                            style = MaterialTheme.typography.labelLarge,
                            color = PhotoKikPurple
                        )
                    }
                    
                    GlowingButton(
                        onClick = { /* Add more photos */ },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        glowColor = PhotoKikPurpleLight,
                        animateGlow = true
                    ) {
                        GlowingIcon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add more",
                            size = 18.dp,
                            tint = Color.White,
                            glowColor = Color.White.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add More",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Swipe interface with enhanced mobile optimization
            val currentPhoto = allPhotos[currentSwipeIndex]
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Enhanced mobile-first progress indicator with better visibility
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp), // Reduced padding for mobile
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground.copy(alpha = 0.9f) // Better contrast
                    ),
                    shape = RoundedCornerShape(16.dp), // Larger radius for mobile
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp), // More generous internal padding
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress text first for better mobile hierarchy
                        Text(
                            text = "${currentSwipeIndex + 1} of ${allPhotos.size}",
                            style = MaterialTheme.typography.titleMedium, // Larger text for mobile
                            color = TextPrimary, // Higher contrast
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Enhanced progress bar with glow effect
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp) // Slightly taller for mobile
                        ) {
                            LinearProgressIndicator(
                                progress = { (currentSwipeIndex + 1).toFloat() / allPhotos.size.toFloat() },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(4.dp)),
                                color = PhotoKikPurple,
                                trackColor = PhotoKikPurple.copy(alpha = 0.2f),
                            )
                            
                            // Glowing effect overlay for progress
                            LinearProgressIndicator(
                                progress = { (currentSwipeIndex + 1).toFloat() / allPhotos.size.toFloat() },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(4.dp)),
                                color = PhotoKikPurpleLight.copy(alpha = 0.6f),
                                trackColor = Color.Transparent,
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Motivational progress text for mobile engagement
                        val progressPercentage = ((currentSwipeIndex + 1).toFloat() / allPhotos.size.toFloat() * 100).toInt()
                        Text(
                            text = when {
                                progressPercentage <= 25 -> "Just getting started! 🚀"
                                progressPercentage <= 50 -> "You're halfway there! 💪"
                                progressPercentage <= 75 -> "Almost done! 🎯"
                                progressPercentage < 100 -> "Final stretch! 🏁"
                                else -> "Completed! 🎉"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Photo card - Enhanced for mobile
                SwipeablePhotoCard(
                    photo = currentPhoto,
                    onSwipe = { direction ->
                        viewModel.handleSwipeAction(currentPhoto.id, direction)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                        .heightIn(min = 400.dp, max = 700.dp)
                )
                
                // Enhanced mobile action buttons with better spacing and feedback
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp), // Optimized for mobile
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(24.dp), // More pronounced rounding for mobile
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp), // Better mobile padding
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Action instruction text
                        Text(
                            text = "Choose your action",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                        // Enhanced Kik button with mobile-first design
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Card(
                                    modifier = Modifier.size(88.dp), // Slightly smaller for better balance
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Button(
                                        onClick = { 
                                            viewModel.handleSwipeAction(currentPhoto.id, SwipeDirection.KIK)
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = KikRed,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(44.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 16.dp,
                                            pressedElevation = 8.dp
                                        )
                                    ) {
                                        GlowingIcon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Kik",
                                            size = 36.dp,
                                            tint = Color.White,
                                            glowColor = KikRedGlow,
                                            animateGlow = true
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = "Kik",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = KikRed,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = "Delete photo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        
                        // Enhanced Keep button with mobile-first design
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Card(
                                    modifier = Modifier.size(88.dp), // Consistent sizing
                                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Button(
                                        onClick = { 
                                            viewModel.handleSwipeAction(currentPhoto.id, SwipeDirection.KEEP)
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = KeepGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(44.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 16.dp,
                                            pressedElevation = 8.dp
                                        )
                                    ) {
                                        GlowingIcon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Keep",
                                            size = 36.dp,
                                            tint = Color.White,
                                            glowColor = KeepGreenGlow,
                                            animateGlow = true
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Text(
                                    text = "Keep",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = KeepGreen,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = "Save photo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                    }
                        
                        // Optional middle action (favorite toggle)
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { 
                                    viewModel.toggleFavorite(currentPhoto.id, !currentPhoto.isFavorite)
                                },
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        color = if (currentPhoto.isFavorite) FavoriteYellow.copy(alpha = 0.2f) 
                                               else CardBackground.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(28.dp)
                                    )
                            ) {
                                GlowingIcon(
                                    imageVector = if (currentPhoto.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Toggle favorite",
                                    size = 28.dp,
                                    tint = if (currentPhoto.isFavorite) FavoriteYellow else TextSecondary,
                                    glowColor = FavoriteYellowGlow,
                                    isSelected = currentPhoto.isFavorite,
                                    animateGlow = currentPhoto.isFavorite
                                )
                            }
                        }
                    }
                }
                
                // Enhanced instructions with better mobile visibility
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 20.dp), // More space from bottom for mobile
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground.copy(alpha = 0.7f) // Better contrast
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp), // More generous padding
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Swipe left instruction
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    GlowingIcon(
                                        imageVector = Icons.Default.SwipeLeft,
                                        contentDescription = "Swipe left",
                                        size = 20.dp, // Larger for mobile
                                        tint = KikRed,
                                        glowColor = KikRedGlow,
                                        animateGlow = true
                                    )
                                    Text(
                                        text = "Swipe Left",
                                        style = MaterialTheme.typography.bodyMedium, // Larger text
                                        color = KikRed,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = "to kik",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(
                                        color = DividerColor,
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                            
                            // Swipe right instruction
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Swipe Right",
                                        style = MaterialTheme.typography.bodyMedium, // Larger text
                                        color = KeepGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                    GlowingIcon(
                                        imageVector = Icons.Default.SwipeRight,
                                        contentDescription = "Swipe right",
                                        size = 20.dp, // Larger for mobile
                                        tint = KeepGreen,
                                        glowColor = KeepGreenGlow,
                                        animateGlow = true
                                    )
                                }
                                Text(
                                    text = "to keep",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
