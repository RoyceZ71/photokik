package com.mocha.photokik.data.model

enum class SwipeDirection(val displayName: String) {
    KEEP("Keep"),
    KIK("Kik")
}

data class SwipeAction(
    val photoId: Long,
    val direction: SwipeDirection,
    val timestamp: Long = System.currentTimeMillis()
)
