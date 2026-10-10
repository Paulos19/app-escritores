package com.paulos19.atelie.repository

import android.content.Context
import android.content.SharedPreferences
import com.paulos19.atelie.model.ScreenCacheData
import com.paulos19.atelie.model.ScreenType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Persiste a última tela e o ponto exato onde o autor parou.
 * Permite restaurar o livro e capítulo ativos na reinicialização do app.
 */
class ScreenCacheManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("atelie_screen_cache", Context.MODE_PRIVATE)

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val KEY_SCREEN_CACHE = "cached_screen_state"
    }

    fun saveScreen(
        screenType: ScreenType,
        bookId: String? = null,
        chapterId: String? = null
    ) {
        val cacheData = ScreenCacheData(
            currentScreen = screenType,
            activeBookId = bookId,
            activeChapterId = chapterId,
            lastVisitedTimestamp = System.currentTimeMillis()
        )
        prefs.edit()
            .putString(KEY_SCREEN_CACHE, json.encodeToString(cacheData))
            .apply()
    }

    fun getCachedScreen(): ScreenCacheData {
        val raw = prefs.getString(KEY_SCREEN_CACHE, null) ?: return ScreenCacheData()
        return try {
            json.decodeFromString<ScreenCacheData>(raw)
        } catch (_: Exception) {
            ScreenCacheData()
        }
    }

    fun clearCache() {
        prefs.edit().remove(KEY_SCREEN_CACHE).apply()
    }
}
