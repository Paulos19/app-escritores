package com.paulos19.atelie.model

import kotlinx.serialization.Serializable

@Serializable
enum class EditorFontFamily {
    SERIF, SANS, MONO
}

@Serializable
data class WriterSettings(
    val fontSizeSp: Int = 17,
    val lineHeightMultiplier: Float = 1.6f,
    val fontFamily: EditorFontFamily = EditorFontFamily.SERIF,
    val showWordCounter: Boolean = true,
    val typewriterMode: Boolean = false,
    val paragraphIndent: Boolean = true,
    val autoSaveDelayMs: Long = 700L
)
