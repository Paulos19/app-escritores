package com.paulos19.atelie.security

import android.content.Context
import android.content.SharedPreferences

/**
 * Gerenciador de Segurança e Cache de Autenticação de 3 Horas.
 *
 * Conforme especificado:
 * A autorização de acesso ao livro protegido fica em cache de 3 horas (10.800.000 ms),
 * sem solicitar a senha ou biometria mesmo se o aplicativo for fechado.
 * Se passadas as 3 horas, a senha/biometria é exigida novamente ao retornar para a tela.
 */
class SecurityManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("atelie_security_cache", Context.MODE_PRIVATE)

    companion object {
        const val THREE_HOURS_MS: Long = 3 * 60 * 60 * 1000L // 10.800.000 ms
        private const val KEY_PREFIX_UNLOCK = "unlocked_timestamp_"
    }

    /**
     * Verifica se o livro está autorizado pelo cache de 3 horas.
     */
    fun isBookAuthorized(bookId: String, isProtected: Boolean): Boolean {
        if (!isProtected) return true

        val lastUnlocked = prefs.getLong(KEY_PREFIX_UNLOCK + bookId, 0L)
        if (lastUnlocked <= 0L) return false

        val elapsed = System.currentTimeMillis() - lastUnlocked
        return elapsed in 0 until THREE_HOURS_MS
    }

    /**
     * Registra o desbloqueio com sucesso, iniciando o contador de 3 horas.
     */
    fun recordUnlock(bookId: String) {
        prefs.edit()
            .putLong(KEY_PREFIX_UNLOCK + bookId, System.currentTimeMillis())
            .apply()
    }

    /**
     * Força o bloqueio imediato do livro.
     */
    fun revokeUnlock(bookId: String) {
        prefs.edit()
            .remove(KEY_PREFIX_UNLOCK + bookId)
            .apply()
    }

    /**
     * Retorna o tempo restante de autorização em minutos.
     */
    fun getRemainingMinutes(bookId: String): Long {
        val lastUnlocked = prefs.getLong(KEY_PREFIX_UNLOCK + bookId, 0L)
        if (lastUnlocked <= 0L) return 0L
        val remainingMs = THREE_HOURS_MS - (System.currentTimeMillis() - lastUnlocked)
        return if (remainingMs > 0) remainingMs / (60 * 1000) else 0L
    }
}
