package com.paulos19.atelie.model

import kotlinx.serialization.Serializable

@Serializable
enum class ScreenType {
    HOME,
    BOOK_DETAIL,
    CHAPTER_EDITOR
}

@Serializable
data class ScreenCacheData(
    val currentScreen: ScreenType = ScreenType.HOME,
    val activeBookId: String? = null,
    val activeChapterId: String? = null,
    val lastVisitedTimestamp: Long = System.currentTimeMillis()
)

sealed class NavigationState {
    data object Home : NavigationState()
    data class BookDetail(val bookId: String) : NavigationState()
    data class ChapterEditor(val bookId: String, val chapterId: String) : NavigationState()
}
