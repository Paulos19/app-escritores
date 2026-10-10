package com.paulos19.atelie.model

import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: String,
    val bookId: String,
    val title: String,
    val content: String = "",
    val synopsis: String? = null,
    val order: Int = 1,
    val wordCount: Int = 0,
    val lastScrollPosition: Int = 0,
    val lastCursorPosition: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
