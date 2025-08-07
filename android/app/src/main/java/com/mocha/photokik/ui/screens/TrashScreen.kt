package com.mocha.photokik.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.ui.theme.*
import com.mocha.photokik.ui.components.GlowingIcon
import com.mocha.photokik.ui.components.GlowingButton
import com.mocha.photokik.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val deletedPhotos by viewModel.deletedPhotos.collectAsStateWithLifecycle()
    var selectedPhotos by remember { mutableStateOf(setOf<Long>()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEmptyTrashDialog by remember { mutableStateOf(false) }
    
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
    ) {
        // Header with actions and glowing icons
        if (deletedPhotos.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${deletedPhotos.size} photos in trash",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Text(
                            text = "Photos will be deleted after 30 days",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    
                    GlowingButton(
                        onClick = { showEmptyTrashDialog = true },
                        glowColor = KikRedGlow,
                        animateGlow = true
                    ) {
                        GlowingIcon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Empty trash",
                            size = 18.dp,
                            tint = KikRed,
                            glowColor = KikRedGlow
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Empty Trash", color = KikRed)
                    }
                }
            }
        }
        
        // Selection toolbar with glowing actions
        AnimatedVisibility(
            visible = selectedPhotos.isNotEmpty(),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PhotoKikPurple.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedPhotos.size} selected",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Restore button with green glow
                        IconButton(
                            onClick = {
                                selectedPhotos.forEach { photoId ->
                                    viewModel.restorePhoto(photoId)
                                }
                                selectedPhotos = emptySet()
                            },
                            modifier = Modifier
                                .background(
                                    color = KeepGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            GlowingIcon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = "Restore",
                                tint = KeepGreen,
                                glowColor = KeepGreenGlow,
                                animateGlow = true
                            )
                        }
                        
                        // Delete permanently button with red glow
                        IconButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .background(
                                    color = KikRed.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            GlowingIcon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = "Delete permanently",
                                tint = KikRed,
                                glowColor = KikRedGlow,
                                animateGlow = true
                            )
                        }
                        
                        // Clear selection
                        IconButton(
                            onClick = { selectedPhotos = emptySet() }
                        ) {
                            GlowingIcon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear selection",
                                tint = TextSecondary,
                                glowColor = GlowColorSecondary
                            )
                        }
                    }
                }
            }
        }
        
        // Photo grid or empty state with enhanced glow
        if (deletedPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GlowingIcon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Empty trash",
                        size = 96.dp,
                        tint = TextSecondary,
                        glowColor = TrashIconGlow,
                        animateGlow = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Trash is Empty",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Photos you delete will appear here",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(deletedPhotos) { photo ->
                    TrashPhotoItem(
                        photo = photo,
                        isSelected = selectedPhotos.contains(photo.id),
                        onSelectionChange = { isSelected ->
                            selectedPhotos = if (isSelected) {
                                selectedPhotos + photo.id
                            } else {
                                selectedPhotos - photo.id
                            }
                        },
                        onRestore = { viewModel.restorePhoto(photo.id) },
                        modifier = Modifier.aspectRatio(1f)
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Permanently",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete ${selectedPhotos.size} photo(s)? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedPhotos.forEach { photoId ->
                            viewModel.permanentlyDeletePhoto(photoId)
                        }
                        selectedPhotos = emptySet()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KikRed
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        text = "Cancel",
                        color = TextSecondary
                    )
                }
            },
            containerColor = SurfaceDark
        )
    }
    
    // Empty trash confirmation dialog
    if (showEmptyTrashDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyTrashDialog = false },
            title = {
                Text(
                    text = "Empty Trash",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete all ${deletedPhotos.size} photo(s) in trash? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.emptyTrash()
                        showEmptyTrashDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KikRed
                    )
                ) {
                    Text("Empty Trash")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEmptyTrashDialog = false }
                ) {
                    Text(
                        text = "Cancel",
                        color = TextSecondary
                    )
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun TrashPhotoItem(
    photo: Photo,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = {
                    onSelectionChange(!isSelected)
                },
                onLongClick = {
                    onSelectionChange(true)
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        ),
        border = if (isSelected) {
            BorderStroke(3.dp, PhotoKikPurple)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Photo with overlay
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photo.filePath)
                        .crossfade(true)
                        .build(),
                    contentDescription = photo.filename,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Dark overlay to indicate deleted state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            }
            
            // Selection indicator with glow
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(
                            color = PhotoKikPurple,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .padding(6.dp)
                ) {
                    GlowingIcon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        size = 18.dp,
                        tint = Color.White,
                        glowColor = PhotoKikPurpleLight,
                        animateGlow = true
                    )
                }
            }
            
            // Quick restore button with green glow
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .clickable { onRestore() }
                    .padding(12.dp)
            ) {
                GlowingIcon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = "Restore",
                    size = 24.dp,
                    tint = KeepGreen,
                    glowColor = KeepGreenGlow,
                    animateGlow = true
                )
            }
        }
    }
}
