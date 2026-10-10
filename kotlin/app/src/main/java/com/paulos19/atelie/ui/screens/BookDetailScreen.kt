package com.paulos19.atelie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.model.Chapter
import com.paulos19.atelie.theme.DimColors
import com.paulos19.atelie.ui.components.ExportBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    book: Book,
    onBack: () -> Unit,
    onOpenChapter: (String) -> Unit,
    onCreateChapter: (String, String?) -> Unit,
    onDeleteChapter: (String) -> Unit,
    onUpdateBook: (String, String?, String?, Boolean, String) -> Unit,
    onDeleteBook: () -> Unit,
    onExportDocx: () -> Unit,
    onExportPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isNewChapterDialogVisible by remember { mutableStateOf(false) }
    var isEditBookDialogVisible by remember { mutableStateOf(false) }
    var isDeleteBookDialogVisible by remember { mutableStateOf(false) }
    var isExportSheetVisible by remember { mutableStateOf(false) }

    // Campos do capítulo
    var chapterTitleInput by remember { mutableStateOf("") }
    var chapterSynopsisInput by remember { mutableStateOf("") }

    // Campos de edição do livro
    var editTitleInput by remember { mutableStateOf(book.title) }
    var editSynopsisInput by remember { mutableStateOf(book.synopsis ?: "") }
    var editGenreInput by remember { mutableStateOf(book.genre ?: "") }
    var editIsProtected by remember { mutableStateOf(book.isProtected) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DimColors.BgCanvas,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DimColors.BgCanvas)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = DimColors.TextDimmed
                        )
                    }
                    Text(
                        text = "Estante",
                        color = DimColors.TextMuted,
                        fontSize = 14.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Exportar Manuscrito DOCX / PDF
                    IconButton(onClick = { isExportSheetVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Exportar DOCX / PDF",
                            tint = DimColors.TextDimmed
                        )
                    }

                    // Editar Livro
                    IconButton(onClick = {
                        editTitleInput = book.title
                        editSynopsisInput = book.synopsis ?: ""
                        editGenreInput = book.genre ?: ""
                        editIsProtected = book.isProtected
                        isEditBookDialogVisible = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Livro",
                            tint = DimColors.TextDimmed
                        )
                    }

                    // Excluir Livro
                    IconButton(onClick = { isDeleteBookDialogVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir Livro",
                            tint = DimColors.DangerMuted
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
        ) {
            // Cartão de Apresentação da Obra
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DimColors.BgSurface)
                        .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(12.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = book.title,
                                color = DimColors.TextQuiet,
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            if (book.isProtected) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Protegido",
                                    tint = DimColors.TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        if (!book.synopsis.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = book.synopsis,
                                color = DimColors.TextDimmed,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "${book.chapters.size} capítulos",
                                color = DimColors.TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${book.totalWords} palavras",
                                color = DimColors.TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "~${book.estimatedReadingMinutes} min leitura",
                                color = DimColors.TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Cabeçalho dos Capítulos + Botão Adicionar Capítulo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Capítulos do Manuscrito",
                        color = DimColors.TextQuiet,
                        fontSize = 16.sp
                    )

                    Button(
                        onClick = { isNewChapterDialogVisible = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DimColors.AccentSlate,
                            contentColor = DimColors.TextQuiet
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Novo Capítulo", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Lista de Capítulos
            if (book.chapters.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum capítulo criado ainda.\nToque em \"Novo Capítulo\" para começar a escrever.",
                            color = DimColors.TextMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                items(book.chapters, key = { it.id }) { chapter ->
                    ChapterItemRow(
                        chapter = chapter,
                        onClick = { onOpenChapter(chapter.id) },
                        onDelete = { onDeleteChapter(chapter.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // Modal Novo Capítulo
    if (isNewChapterDialogVisible) {
        AlertDialog(
            onDismissRequest = { isNewChapterDialogVisible = false },
            containerColor = DimColors.BgSurface,
            title = { Text(text = "Novo Capítulo", color = DimColors.TextQuiet, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = chapterTitleInput,
                        onValueChange = { chapterTitleInput = it },
                        label = { Text("Título do Capítulo", color = DimColors.TextMuted) },
                        placeholder = { Text("ex: Capítulo ${book.chapters.size + 1}: O Despertar", color = DimColors.TextGhost) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DimColors.TextQuiet,
                            unfocusedTextColor = DimColors.TextDimmed,
                            focusedBorderColor = DimColors.BorderFocus,
                            unfocusedBorderColor = DimColors.BorderSubtle
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = chapterSynopsisInput,
                        onValueChange = { chapterSynopsisInput = it },
                        label = { Text("Sinopse ou Notas (Opcional)", color = DimColors.TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DimColors.TextQuiet,
                            unfocusedTextColor = DimColors.TextDimmed,
                            focusedBorderColor = DimColors.BorderFocus,
                            unfocusedBorderColor = DimColors.BorderSubtle
                        ),
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val title = chapterTitleInput.trim().ifEmpty { "Capítulo ${book.chapters.size + 1}" }
                        onCreateChapter(title, chapterSynopsisInput.trim().ifEmpty { null })
                        chapterTitleInput = ""
                        chapterSynopsisInput = ""
                        isNewChapterDialogVisible = false
                    }
                ) {
                    Text("Criar e Abrir", color = DimColors.TextQuiet)
                }
            },
            dismissButton = {
                TextButton(onClick = { isNewChapterDialogVisible = false }) {
                    Text("Cancelar", color = DimColors.TextMuted)
                }
            }
        )
    }

    // Modal Editar Livro
    if (isEditBookDialogVisible) {
        AlertDialog(
            onDismissRequest = { isEditBookDialogVisible = false },
            containerColor = DimColors.BgSurface,
            title = { Text(text = "Editar Obra", color = DimColors.TextQuiet, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitleInput,
                        onValueChange = { editTitleInput = it },
                        label = { Text("Título", color = DimColors.TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DimColors.TextQuiet,
                            unfocusedTextColor = DimColors.TextDimmed,
                            focusedBorderColor = DimColors.BorderFocus,
                            unfocusedBorderColor = DimColors.BorderSubtle
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editGenreInput,
                        onValueChange = { editGenreInput = it },
                        label = { Text("Gênero", color = DimColors.TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DimColors.TextQuiet,
                            unfocusedTextColor = DimColors.TextDimmed,
                            focusedBorderColor = DimColors.BorderFocus,
                            unfocusedBorderColor = DimColors.BorderSubtle
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editSynopsisInput,
                        onValueChange = { editSynopsisInput = it },
                        label = { Text("Sinopse", color = DimColors.TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DimColors.TextQuiet,
                            unfocusedTextColor = DimColors.TextDimmed,
                            focusedBorderColor = DimColors.BorderFocus,
                            unfocusedBorderColor = DimColors.BorderSubtle
                        ),
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Proteger com Biometria", color = DimColors.TextQuiet, fontSize = 13.sp)
                        Switch(
                            checked = editIsProtected,
                            onCheckedChange = { editIsProtected = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DimColors.TextQuiet,
                                checkedTrackColor = DimColors.AccentSlate,
                                uncheckedThumbColor = DimColors.TextMuted,
                                uncheckedTrackColor = DimColors.BgSubtle
                            )
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editTitleInput.isNotBlank()) {
                            onUpdateBook(
                                editTitleInput.trim(),
                                editSynopsisInput.trim().ifEmpty { null },
                                editGenreInput.trim().ifEmpty { null },
                                editIsProtected,
                                book.coverColorHex
                            )
                            isEditBookDialogVisible = false
                        }
                    }
                ) {
                    Text("Salvar", color = DimColors.TextQuiet)
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditBookDialogVisible = false }) {
                    Text("Cancelar", color = DimColors.TextMuted)
                }
            }
        )
    }

    // Confirmação Exclusão do Livro
    if (isDeleteBookDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDeleteBookDialogVisible = false },
            containerColor = DimColors.BgSurface,
            title = { Text(text = "Excluir Obra?", color = DimColors.DangerMuted, fontSize = 16.sp) },
            text = {
                Text(
                    text = "Tem certeza que deseja apagar o livro \"${book.title}\" e todos os seus capítulos? Esta ação não pode ser desfeita.",
                    color = DimColors.TextDimmed,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isDeleteBookDialogVisible = false
                        onDeleteBook()
                    }
                ) {
                    Text("Excluir Definitivamente", color = DimColors.DangerMuted)
                }
            },
            dismissButton = {
                TextButton(onClick = { isDeleteBookDialogVisible = false }) {
                    Text("Cancelar", color = DimColors.TextMuted)
                }
            }
        )
    }

    // Bottom Sheet de Exportação DOCX / PDF
    if (isExportSheetVisible) {
        ExportBottomSheet(
            book = book,
            onExportDocx = onExportDocx,
            onExportPdf = onExportPdf,
            onDismissRequest = { isExportSheetVisible = false }
        )
    }
}

@Composable
private fun ChapterItemRow(
    chapter: Chapter,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DimColors.BgElevated)
            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chapter.title,
                color = DimColors.TextQuiet,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${chapter.wordCount} palavras • ~${Math.max(1, chapter.wordCount / 200)} min leitura",
                color = DimColors.TextMuted,
                fontSize = 11.sp
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Excluir Capítulo",
                tint = DimColors.TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
