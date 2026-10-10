package com.paulos19.atelie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.model.Folder
import com.paulos19.atelie.theme.DimColors
import com.paulos19.atelie.ui.components.BookCoverCard
import com.paulos19.atelie.ui.components.MetricsDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    folders: List<Folder>,
    books: List<Book>,
    selectedFolderId: String?,
    onSelectFolder: (String?) -> Unit,
    onOpenBook: (Book) -> Unit,
    onCreateFolder: (String, String?) -> Unit,
    onCreateBook: (String, String?, String?, Boolean, String) -> Unit,
    onBackupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMetricsDropdownExpanded by remember { mutableStateOf(false) }
    var isNewFolderDialogVisible by remember { mutableStateOf(false) }
    var isNewBookDialogVisible by remember { mutableStateOf(false) }

    // Campos do modal de criação de pasta
    var folderNameInput by remember { mutableStateOf("") }

    // Campos do modal de criação de livro
    var bookTitleInput by remember { mutableStateOf("") }
    var bookSynopsisInput by remember { mutableStateOf("") }
    var bookGenreInput by remember { mutableStateOf("") }
    var bookIsProtected by remember { mutableStateOf(false) }
    var selectedColorHex by remember { mutableStateOf("#1E2024") }

    val displayedBooks = if (selectedFolderId != null) {
        books.filter { it.folderId == selectedFolderId }
    } else {
        books
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DimColors.BgCanvas,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DimColors.BgCanvas)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ateliê do Escritor",
                            color = DimColors.TextQuiet,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Baixa luminância • Offline • Seguro",
                            color = DimColors.TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dropdown de Métricas e Backup
                        Box {
                            IconButton(
                                onClick = { isMetricsDropdownExpanded = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DimColors.BgElevated)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = "Métricas Recentes",
                                    tint = DimColors.TextDimmed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            MetricsDropdown(
                                expanded = isMetricsDropdownExpanded,
                                onDismissRequest = { isMetricsDropdownExpanded = false },
                                books = books,
                                onBackupClick = onBackupClick
                            )
                        }

                        // Botão Nova Pasta
                        IconButton(
                            onClick = { isNewFolderDialogVisible = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DimColors.BgElevated)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = "Nova Pasta",
                                tint = DimColors.TextDimmed,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Botão Novo Livro
                        Button(
                            onClick = { isNewBookDialogVisible = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DimColors.AccentSlate,
                                contentColor = DimColors.TextQuiet
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Livro", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Seletor Horizontal de Pastas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isAllSelected = selectedFolderId == null
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isAllSelected) DimColors.AccentSlate else DimColors.BgElevated)
                            .clickable { onSelectFolder(null) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Todos (${books.size})",
                            fontSize = 12.sp,
                            color = if (isAllSelected) DimColors.TextQuiet else DimColors.TextMuted
                        )
                    }

                    folders.forEach { folder ->
                        val isFolderSelected = selectedFolderId == folder.id
                        val count = books.count { it.folderId == folder.id }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isFolderSelected) DimColors.AccentSlate else DimColors.BgElevated)
                                .clickable { onSelectFolder(folder.id) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${folder.name} ($count)",
                                fontSize = 12.sp,
                                color = if (isFolderSelected) DimColors.TextQuiet else DimColors.TextMuted
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (displayedBooks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Nenhuma obra nesta pasta.",
                        color = DimColors.TextMuted,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { isNewBookDialogVisible = true }) {
                        Text(text = "Começar um novo livro", color = DimColors.TextQuiet)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 145.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(displayedBooks, key = { it.id }) { book ->
                    BookCoverCard(
                        book = book,
                        onClick = { onOpenBook(book) }
                    )
                }
            }
        }
    }

    // Modal Nova Pasta
    if (isNewFolderDialogVisible) {
        AlertDialog(
            onDismissRequest = { isNewFolderDialogVisible = false },
            containerColor = DimColors.BgSurface,
            title = { Text(text = "Nova Pasta", color = DimColors.TextQuiet, fontSize = 16.sp) },
            text = {
                OutlinedTextField(
                    value = folderNameInput,
                    onValueChange = { folderNameInput = it },
                    label = { Text("Nome da Pasta", color = DimColors.TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DimColors.TextQuiet,
                        unfocusedTextColor = DimColors.TextDimmed,
                        focusedBorderColor = DimColors.BorderFocus,
                        unfocusedBorderColor = DimColors.BorderSubtle
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (folderNameInput.isNotBlank()) {
                            onCreateFolder(folderNameInput.trim(), null)
                            folderNameInput = ""
                            isNewFolderDialogVisible = false
                        }
                    }
                ) {
                    Text("Criar", color = DimColors.TextQuiet)
                }
            },
            dismissButton = {
                TextButton(onClick = { isNewFolderDialogVisible = false }) {
                    Text("Cancelar", color = DimColors.TextMuted)
                }
            }
        )
    }

    // Modal Novo Livro
    if (isNewBookDialogVisible) {
        AlertDialog(
            onDismissRequest = { isNewBookDialogVisible = false },
            containerColor = DimColors.BgSurface,
            title = { Text(text = "Nova Obra", color = DimColors.TextQuiet, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bookTitleInput,
                        onValueChange = { bookTitleInput = it },
                        label = { Text("Título da Obra", color = DimColors.TextMuted) },
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
                        value = bookGenreInput,
                        onValueChange = { bookGenreInput = it },
                        label = { Text("Gênero (ex: Ficção, Poesia)", color = DimColors.TextMuted) },
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
                        value = bookSynopsisInput,
                        onValueChange = { bookSynopsisInput = it },
                        label = { Text("Sinopse ou Premissa", color = DimColors.TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DimColors.TextQuiet,
                            unfocusedTextColor = DimColors.TextDimmed,
                            focusedBorderColor = DimColors.BorderFocus,
                            unfocusedBorderColor = DimColors.BorderSubtle
                        ),
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Proteção por Senha / Biometria
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Proteger com Senha/Biometria", color = DimColors.TextQuiet, fontSize = 13.sp)
                            Text(text = "Cache de 3h sem repedir senha", color = DimColors.TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = bookIsProtected,
                            onCheckedChange = { bookIsProtected = it },
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
                        if (bookTitleInput.isNotBlank()) {
                            onCreateBook(
                                bookTitleInput.trim(),
                                bookSynopsisInput.trim().ifEmpty { null },
                                bookGenreInput.trim().ifEmpty { null },
                                bookIsProtected,
                                selectedColorHex
                            )
                            bookTitleInput = ""
                            bookSynopsisInput = ""
                            bookGenreInput = ""
                            bookIsProtected = false
                            isNewBookDialogVisible = false
                        }
                    }
                ) {
                    Text("Iniciar Livro", color = DimColors.TextQuiet)
                }
            },
            dismissButton = {
                TextButton(onClick = { isNewBookDialogVisible = false }) {
                    Text("Cancelar", color = DimColors.TextMuted)
                }
            }
        )
    }
}
