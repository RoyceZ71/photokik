package com.mocha.photokik.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlowingIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
    glowColor: Color = tint,
    size: Dp = 24.dp,
    glowRadius: Dp = 8.dp,
    isSelected: Boolean = false,
    animateGlow: Boolean = true
) {
    // Animation for pulsing glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "glow_animation")
    
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    val baseGlowAlpha = if (isSelected) 0.6f else 0.3f
    val finalGlowAlpha = if (animateGlow) glowAlpha else baseGlowAlpha
    val finalGlowScale = if (animateGlow && isSelected) glowScale else 1f
    
    Box(
        modifier = modifier.size(size + glowRadius * 2),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect background
        Box(
            modifier = Modifier
                .size((size + glowRadius * 2) * finalGlowScale)
                .scale(finalGlowScale)
                .background(
                    color = glowColor.copy(alpha = finalGlowAlpha),
                    shape = CircleShape
                )
        )
        
        // Icon
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(size),
            tint = tint
        )
    }
}

@Composable
fun GlowingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    animateGlow: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "button_glow")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_glow_alpha"
    )
    
    val finalGlowAlpha = if (animateGlow) glowAlpha else 0.3f
    
    Button(
        onClick = onClick,
        modifier = modifier
            .drawBehind {
                if (enabled) {
                    drawGlowEffect(
                        color = glowColor,
                        alpha = finalGlowAlpha,
                        radius = size.width * 0.6f
                    )
                }
            },
        enabled = enabled,
        content = content
    )
}

private fun DrawScope.drawGlowEffect(
    color: Color,
    alpha: Float,
    radius: Float
) {
    val center = this.center
    drawCircle(
        color = color.copy(alpha = alpha),
        radius = radius,
        center = center
    )
}

@Composable
fun PulsingGlowCard(
    modifier: Modifier = Modifier,
    glowColor: Color = MaterialTheme.colorScheme.primary,
    isActive: Boolean = false,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_glow")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "card_glow_alpha"
    )
    
    val finalGlowAlpha = if (isActive) glowAlpha else 0.1f
    
    Card(
        modifier = modifier
            .drawBehind {
                drawRect(
                    color = glowColor.copy(alpha = finalGlowAlpha),
                    size = size
                )
            },
        content = { content() }
    )
}
