package com.mocha.photokik.data.model

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val flag: String
) {
    ENGLISH("en", "English", "🇺🇸"),
    SPANISH("es", "Español", "🇪🇸"),
    PORTUGUESE("pt", "Português", "🇵🇹");
    
    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().find { it.code == code } ?: ENGLISH
        }
        
        fun getAllLanguages(): List<AppLanguage> {
            return values().toList()
        }
    }
    
    fun getLocalizedDisplayName(): String {
        return when (this) {
            ENGLISH -> "English"
            SPANISH -> "Español" 
            PORTUGUESE -> "Português"
        }
    }
}
