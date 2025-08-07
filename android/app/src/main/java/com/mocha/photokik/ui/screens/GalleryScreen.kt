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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mocha.photokik.data.model.Photo
import com.mocha.photokik.data.model.PhotoCategory
import com.mocha.photokik.ui.theme.*
import com.mocha.photokik.ui.components.GlowingIcon
import com.mocha.photokik.ui.components.PulsingGlowCard
import com.mocha.photokik.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allPhotos by viewModel.allPhotos.collectAsStateWithLifecycle()
    val favoritePhotos by viewModel.favoritePhotos.collectAsStateWithLifecycle()
    val galleryStats by viewModel.galleryStats.collectAsStateWithLifecycle()
    val filteredPhotos by viewModel.filteredPhotos.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    
    var selectedCategory by remember { mutableStateOf<PhotoCategory?>(null) }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    
    val photosToDisplay = when {
        showFavoritesOnly -> favoritePhotos
        selectedCategory != null -> allPhotos.filter { it.category == selectedCategory }
        searchQuery.isNotEmpty() -> filteredPhotos
        else -> allPhotos
    }
    
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
        // Gallery stats with glowing effects
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StatsCard(
                    title = "Total",
                    value = galleryStats.totalPhotos.toString(),
                    icon = Icons.Default.PhotoLibrary,
                    color = PhotoKikPurple,
                    glowColor = PhotoKikPurpleLight,
                    isSelected = selectedCategory == null && !showFavoritesOnly,
                    onClick = { 
                        selectedCategory = null
                        showFavoritesOnly = false 
                    }
                )
            }
            
            item {
                StatsCard(
                    title = "Memories",
                    value = galleryStats.memories.toString(),
                    icon = Icons.Default.PhotoAlbum,
                    color = KeepGreen,
                    glowColor = KeepGreenGlow,
                    isSelected = selectedCategory == PhotoCategory.MEMORIES,
                    onClick = { selectedCategory = PhotoCategory.MEMORIES }
                )
            }
            
            item {
                StatsCard(
                    title = "Documents",
                    value = galleryStats.documents.toString(),
                    icon = Icons.Default.Description,
                    color = DocumentsColor,
                    glowColor = DocumentsGlow,
                    isSelected = selectedCategory == PhotoCategory.DOCUMENTS,
                    onClick = { selectedCategory = PhotoCategory.DOCUMENTS }
                )
            }
            
            item {
                StatsCard(
                    title = "Duplicates",
                    value = galleryStats.duplicates.toString(),
                    icon = Icons.Default.ContentCopy,
                    color = DuplicatesColor,
                    glowColor = DuplicatesGlow,
                    isSelected = selectedCategory == PhotoCategory.DUPLICATES,
                    onClick = { selectedCategory = PhotoCategory.DUPLICATES }
                )
            }
            
            item {
                StatsCard(
                    title = "Favorites",
                    value = galleryStats.favorites.toString(),
                    icon = Icons.Default.Star,
                    color = FavoriteYellow,
                    glowColor = FavoriteYellowGlow,
                    isSelected = showFavoritesOnly,
                    onClick = { showFavoritesOnly = !showFavoritesOnly }
                )
            }
        }
        
        // Search bar with glowing search icon
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            placeholder = { Text("Search photos...") },
            leadingIcon = {
                GlowingIcon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = PhotoKikPurple,
                    glowColor = PhotoKikPurpleLight,
                    animateGlow = searchQuery.isNotEmpty()
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearSearch() }
                    ) {
                        GlowingIcon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = TextSecondary,
                            glowColor = KikRedGlow,
                            animateGlow = true
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PhotoKikPurple,
                unfocusedBorderColor = BorderColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextSecondary
            )
        )
        
        // Photo grid or empty state
        if (photosToDisplay.isEmpty()) {
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
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "No photos",
                        size = 64.dp,
                        tint = TextSecondary,
                        glowColor = GlowColorSecondary,
                        animateGlow = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No photos found" else "No photos in this category",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
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
                items(photosToDisplay) { photo ->
                    PhotoGridItem(
                        photo = photo,
                        onToggleFavorite = { 
                            viewModel.toggleFavorite(photo.id, !photo.isFavorite) 
                        },
                        modifier = Modifier.aspectRatio(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    glowColor: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    PulsingGlowCard(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() },
        glowColor = glowColor,
        isActive = isSelected
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected) {
                    color.copy(alpha = 0.2f)
                } else {
                    CardBackground
                }
            ),
            border = if (isSelected) {
                BorderStroke(2.dp, color)
            } else null,
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 8.dp else 4.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlowingIcon(
                    imageVector = icon,
                    contentDescription = title,
                    size = 28.dp,
                    tint = color,
                    glowColor = glowColor,
                    isSelected = isSelected,
                    animateGlow = isSelected
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun PhotoGridItem(
    photo: Photo,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photo.filePath)
                    .crossfade(true)
                    .build(),
                contentDescription = photo.filename,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Favorite overlay with glow
            if (photo.isFavorite) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable { onToggleFavorite() }
                        .padding(6.dp)
                ) {
                    GlowingIcon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorite",
                        size = 16.dp,
                        tint = FavoriteYellow,
                        glowColor = FavoriteYellowGlow,
                        animateGlow = true
                    )
                }
            }
            
            // Category indicator with color-coded glow
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(
                        color = when (photo.category) {
                            PhotoCategory.MEMORIES -> KeepGreen.copy(alpha = 0.8f)
                            PhotoCategory.DOCUMENTS -> DocumentsColor.copy(alpha = 0.8f)
                            PhotoCategory.DUPLICATES -> DuplicatesColor.copy(alpha = 0.8f)
                            PhotoCategory.BLURRY -> BlurryColor.copy(alpha = 0.8f)
                            PhotoCategory.UNCATEGORIZED -> TextSecondary.copy(alpha = 0.8f)
                        },
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = photo.category.displayName.take(3).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}
