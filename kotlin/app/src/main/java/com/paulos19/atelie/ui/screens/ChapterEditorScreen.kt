package com.paulos19.atelie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.model.Chapter
import com.paulos19.atelie.model.EditorFontFamily
import com.paulos19.atelie.model.WriterSettings
import com.paulos19.atelie.theme.DimColors
import com.paulos19.atelie.ui.components.ToolbarExpandedModal
import com.paulos19.atelie.ui.components.WriterToolbar
import kotlinx.coroutines.delay

@Composable
fun ChapterEditorScreen(
    book: Book,
    chapter: Chapter,
    initialSettings: WriterSettings,
    onBack: () -> Unit,
    onSaveContent: (String, Int) -> Unit,
    onUpdateSettings: (WriterSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = chapter.content,
                selection = TextRange(chapter.content.length)
            )
        )
    }

    var settings by remember { mutableStateOf(initialSettings) }
    var isReadingMode by remember { mutableStateOf(false) }
    var isHeaderDropdownExpanded by remember { mutableStateOf(false) }
    var isExpandedToolbarModalVisible by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Contagem de palavras e métricas em tempo real
    val wordCount = remember(textFieldValue.text) {
        val trimmed = textFieldValue.text.trim()
        if (trimmed.isEmpty()) 0 else trimmed.split(Regex("\\s+")).size
    }
    val charCount = textFieldValue.text.length
    val readingMinutes = Math.max(1, (wordCount / 200.0).toInt())

    // Auto-save debounced em segundo plano
    LaunchedEffect(textFieldValue.text) {
        delay(settings.autoSaveDelayMs)
        onSaveContent(textFieldValue.text, wordCount)
    }

    val selectedFontFamily = when (settings.fontFamily) {
        EditorFontFamily.SERIF -> FontFamily.Serif
        EditorFontFamily.SANS -> FontFamily.SansSerif
        EditorFontFamily.MONO -> FontFamily.Monospace
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DimColors.BgCanvas,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DimColors.BgCanvas)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = DimColors.TextDimmed
                        )
                    }

                    Column {
                        Text(
                            text = chapter.title,
                            color = DimColors.TextQuiet,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                        Text(
                            text = if (isReadingMode) "Modo Leitura (Congelado)" else "$wordCount palavras • Salvo",
                            color = if (isReadingMode) DimColors.AccentActive else DimColors.TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Dropdown no Header: Métricas da Sessão & Ativar Modo Leitura
                Box {
                    IconButton(onClick = { isHeaderDropdownExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opções do Editor",
                            tint = DimColors.TextDimmed
                        )
                    }

                    DropdownMenu(
                        expanded = isHeaderDropdownExpanded,
                        onDismissRequest = { isHeaderDropdownExpanded = false },
                        modifier = Modifier
                            .background(DimColors.BgElevated)
                            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(8.dp))
                            .width(240.dp)
                            .padding(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Métricas do Capítulo",
                                fontSize = 13.sp,
                                color = DimColors.TextQuiet
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Palavras: $wordCount", fontSize = 12.sp, color = DimColors.TextDimmed)
                            Text(text = "Caracteres: $charCount", fontSize = 12.sp, color = DimColors.TextDimmed)
                            Text(text = "Leitura estimada: ~$readingMinutes min", fontSize = 12.sp, color = DimColors.TextDimmed)

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DimColors.BorderSubtle)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Alternar Modo Leitura
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DimColors.BgSubtle)
                                    .clickable {
                                        isReadingMode = !isReadingMode
                                        isHeaderDropdownExpanded = false
                                        if (isReadingMode) {
                                            keyboardController?.hide()
                                        }
                                    }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isReadingMode) Icons.Default.Edit else Icons.Default.AutoStories,
                                        contentDescription = null,
                                        tint = DimColors.TextQuiet,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isReadingMode) "Modo Edição" else "Modo Leitura",
                                        fontSize = 12.sp,
                                        color = DimColors.TextQuiet
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Se o Modo Leitura estiver ativo, a toolbar fica completamente congelada/oculta!
            if (!isReadingMode) {
                WriterToolbar(
                    wordCount = wordCount,
                    onInsertText = { inserted ->
                        val currentText = textFieldValue.text
                        val selection = textFieldValue.selection
                        val newText = currentText.substring(0, selection.min) + inserted + currentText.substring(selection.max)
                        val newCursor = selection.min + inserted.length
                        textFieldValue = TextFieldValue(
                            text = newText,
                            selection = TextRange(newCursor)
                        )
                    },
                    onWrapSelection = { prefix, suffix ->
                        val currentText = textFieldValue.text
                        val selection = textFieldValue.selection
                        val selectedPortion = currentText.substring(selection.min, selection.max)
                        val wrapped = "$prefix${selectedPortion.ifEmpty { "texto" }}$suffix"
                        val newText = currentText.substring(0, selection.min) + wrapped + currentText.substring(selection.max)
                        val newCursor = selection.min + wrapped.length
                        textFieldValue = TextFieldValue(
                            text = newText,
                            selection = TextRange(newCursor)
                        )
                    },
                    onExpandClick = {
                        // Teclado desce e modal da toolbar expande
                        keyboardController?.hide()
                        isExpandedToolbarModalVisible = true
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = if (isReadingMode) 28.dp else 20.dp,
                        vertical = 16.dp
                    )
            ) {
                // Banner sutil ao ativar Modo Leitura
                if (isReadingMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(DimColors.BgElevated)
                            .clickable { isReadingMode = false }
                            .padding(vertical = 6.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Modo Leitura Ativo • Toque para retornar ao modo escrita",
                            color = DimColors.TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Área do Texto com Proteção Visual e Ergonomia
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = {
                        if (!isReadingMode) {
                            textFieldValue = it
                        }
                    },
                    readOnly = isReadingMode,
                    textStyle = TextStyle(
                        color = DimColors.TextDimmed,
                        fontSize = settings.fontSizeSp.sp,
                        lineHeight = (settings.fontSizeSp * settings.lineHeightMultiplier).sp,
                        fontFamily = selectedFontFamily
                    ),
                    cursorBrush = SolidColor(if (isReadingMode) DimColors.BgCanvas else DimColors.TextQuiet),
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 400.dp),
                    decorationBox = { innerTextField ->
                        if (textFieldValue.text.isEmpty() && !isReadingMode) {
                            Text(
                                text = "Comece sua história aqui... Sem pressa, sem ruído, apenas palavras.",
                                color = DimColors.TextGhost,
                                fontSize = settings.fontSizeSp.sp,
                                fontFamily = selectedFontFamily
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(180.dp))
            }
        }
    }

    // Modal da Toolbar Expandida (todas as configurações e ferramentas nível Word)
    if (isExpandedToolbarModalVisible) {
        ToolbarExpandedModal(
            settings = settings,
            wordCount = wordCount,
            charCount = charCount,
            readingMinutes = readingMinutes,
            onUpdateSettings = { newS ->
                settings = newS
                onUpdateSettings(newS)
            },
            onInsertText = { inserted ->
                val currentText = textFieldValue.text
                val selection = textFieldValue.selection
                val newText = currentText.substring(0, selection.min) + inserted + currentText.substring(selection.max)
                textFieldValue = TextFieldValue(text = newText, selection = TextRange(selection.min + inserted.length))
            },
            onWrapSelection = { prefix, suffix ->
                val currentText = textFieldValue.text
                val selection = textFieldValue.selection
                val selectedPortion = currentText.substring(selection.min, selection.max)
                val wrapped = "$prefix${selectedPortion.ifEmpty { "texto" }}$suffix"
                val newText = currentText.substring(0, selection.min) + wrapped + currentText.substring(selection.max)
                textFieldValue = TextFieldValue(text = newText, selection = TextRange(selection.min + wrapped.length))
            },
            onDismissRequest = { isExpandedToolbarModalVisible = false }
        )
    }
}
