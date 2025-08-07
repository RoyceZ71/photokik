package com.mocha.photokik.data.model

import androidx.compose.runtime.Stable
import kotlin.math.abs

@Stable
data class SwipeGesture(
    val startX: Float = 0f,
    val startY: Float = 0f,
    val currentX: Float = 0f,
    val currentY: Float = 0f,
    val velocity: Float = 0f,
    val threshold: Float = 300f
) {
    val deltaX: Float get() = currentX - startX
    val deltaY: Float get() = currentY - startY
    val distance: Float get() = kotlin.math.sqrt((deltaX * deltaX) + (deltaY * deltaY))
    val isHorizontalSwipe: Boolean get() = abs(deltaX) > abs(deltaY)
    val isVerticalSwipe: Boolean get() = abs(deltaY) > abs(deltaX)
    
    val direction: SwipeDirection? get() {
        return when {
            isHorizontalSwipe && abs(deltaX) > threshold -> {
                if (deltaX > 0) SwipeDirection.KEEP else SwipeDirection.KIK
            }
            else -> null
        }
    }
    
    val swipeProgress: Float get() {
        return if (isHorizontalSwipe) {
            (abs(deltaX) / threshold).coerceIn(0f, 1f)
        } else {
            0f
        }
    }
}

enum class SwipeState {
    IDLE,
    DRAGGING,
    ANIMATING
}
