package com.paulos19.atelie.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta especializada em baixo contraste e atenuação de luz branca/azul
 * para autores com fotofobia, sensibilidade luminosa e fadiga ocular.
 *
 * Elimina o corte estridente do preto absoluto (000000) e do branco puro (FFFFFF).
 */
object DimColors {
    // Fundos e Superfícies
    val BgCanvas = Color(0xFF131416)       // Fundo principal suave e aveludado
    val BgSurface = Color(0xFF1B1C1F)      // Cartões, modais e containers
    val BgElevated = Color(0xFF232428)     // Elementos elevados e barras de ferramentas
    val BgSubtle = Color(0xFF2B2C32)       // Campos de entrada e divisores suaves

    // Bordas imperceptíveis para definir silhuetas sem estresse visual
    val BorderSubtle = Color(0xFF2E2F36)
    val BorderFocus = Color(0xFF42444E)

    // Tipografia esmaecida e confortável
    val TextMuted = Color(0xFF686873)      // Rótulos secundários, tags e datas
    val TextDimmed = Color(0xFF9898A4)     // Texto principal de leitura e escrita
    val TextQuiet = Color(0xFFB4B4BF)      // Títulos e destaques leves
    val TextGhost = Color(0xFF45454E)      // Placeholders discretos

    // Acentos atenuados (sem neon, sem saturação agressiva)
    val AccentSlate = Color(0xFF4A5260)    // Botões primários comedidos
    val AccentActive = Color(0xFF5E6778)   // Abas e estados ativos
    val AccentBorder = Color(0xFF383E49)

    // Indicadores funcionais atenuados
    val DangerMuted = Color(0xFF7A4545)
    val SuccessMuted = Color(0xFF426550)
    val WarningMuted = Color(0xFF735C3A)

    // Paleta de capas discretas em tons de carvão e pedras naturais
    val CoverDarkGray = Color(0xFF1E2024)
    val CoverCharcoal = Color(0xFF25262B)
    val CoverSlate = Color(0xFF22262E)
    val CoverForest = Color(0xFF1E2622)
    val CoverWine = Color(0xFF2B1F22)
}
