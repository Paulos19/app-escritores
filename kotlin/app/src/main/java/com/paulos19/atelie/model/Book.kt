package com.paulos19.atelie.model

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val folderId: String? = null,
    val title: String,
    val subtitle: String? = null,
    val author: String = "Autor",
    val genre: String? = null,
    val coverColorHex: String = "#1E2024",
    val synopsis: String? = null,
    val chapters: List<Chapter> = emptyList(),
    val isProtected: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalWords: Int
        get() = chapters.sumOf { it.wordCount }

    val estimatedReadingMinutes: Int
        get() = kotlin.math.max(1, (totalWords / 200.0).toInt())
}
