package com.paulos19.atelie

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.paulos19.atelie.model.NavigationState
import com.paulos19.atelie.repository.ScreenCacheManager
import com.paulos19.atelie.repository.WriterRepository
import com.paulos19.atelie.security.SecurityManager
import com.paulos19.atelie.theme.AtelieTheme
import com.paulos19.atelie.ui.components.SecurityUnlockDialog
import com.paulos19.atelie.ui.screens.BookDetailScreen
import com.paulos19.atelie.ui.screens.ChapterEditorScreen
import com.paulos19.atelie.ui.screens.HomeScreen
import com.paulos19.atelie.viewmodel.WriterViewModel
import com.paulos19.atelie.viewmodel.WriterViewModelFactory

class MainActivity : FragmentActivity() {

    private val viewModel: WriterViewModel by viewModels {
        val repository = WriterRepository(applicationContext)
        val securityManager = SecurityManager(applicationContext)
        val screenCacheManager = ScreenCacheManager(applicationContext)
        WriterViewModelFactory(repository, securityManager, screenCacheManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AtelieTheme {
                val folders by viewModel.folders.collectAsState()
                val books by viewModel.books.collectAsState()
                val settings by viewModel.settings.collectAsState()
                val selectedFolderId by viewModel.selectedFolderId.collectAsState()
                val navigationState by viewModel.navigationState.collectAsState()
                val pendingAuthBook by viewModel.pendingAuthBook.collectAsState()
                val statusMessage by viewModel.statusMessage.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }

                // Exibe mensagens de feedback e exportação
                LaunchedEffect(statusMessage) {
                    statusMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearStatusMessage()
                    }
                }

                // Controle do Botão Físico/Gesto Voltar do Android
                BackHandler(enabled = navigationState !is NavigationState.Home) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    when (val currentNav = navigationState) {
                        is NavigationState.Home -> {
                            HomeScreen(
                                folders = folders,
                                books = books,
                                selectedFolderId = selectedFolderId,
                                onSelectFolder = { viewModel.selectFolder(it) },
                                onOpenBook = { book -> viewModel.openBook(book, this@MainActivity) },
                                onCreateFolder = { name, desc -> viewModel.createFolder(name, desc) },
                                onCreateBook = { title, syn, gen, isProt, color ->
                                    viewModel.createBook(title, syn, gen, isProt, color)
                                },
                                onBackupClick = { viewModel.createBackup(applicationContext) },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is NavigationState.BookDetail -> {
                            val activeBook = books.find { it.id == currentNav.bookId }
                            if (activeBook != null) {
                                BookDetailScreen(
                                    book = activeBook,
                                    onBack = { viewModel.navigateBack() },
                                    onOpenChapter = { chapId -> viewModel.openChapter(activeBook.id, chapId) },
                                    onCreateChapter = { title, syn -> viewModel.createChapter(activeBook.id, title, syn) },
                                    onDeleteChapter = { chapId -> viewModel.deleteChapter(activeBook.id, chapId) },
                                    onUpdateBook = { title, syn, gen, isProt, color ->
                                        viewModel.updateBook(activeBook.id, title, syn, gen, isProt, color)
                                    },
                                    onDeleteBook = { viewModel.deleteBook(activeBook.id) },
                                    onExportDocx = { viewModel.exportBookDocx(applicationContext, activeBook) },
                                    onExportPdf = { viewModel.exportBookPdf(applicationContext, activeBook) },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                viewModel.navigateBack()
                            }
                        }

                        is NavigationState.ChapterEditor -> {
                            val activeBook = books.find { it.id == currentNav.bookId }
                            val activeChapter = activeBook?.chapters?.find { it.id == currentNav.chapterId }
                            if (activeBook != null && activeChapter != null) {
                                ChapterEditorScreen(
                                    book = activeBook,
                                    chapter = activeChapter,
                                    initialSettings = settings,
                                    onBack = { viewModel.navigateBack() },
                                    onSaveContent = { content, count ->
                                        viewModel.updateChapterContent(activeBook.id, activeChapter.id, content, count)
                                    },
                                    onUpdateSettings = { newS -> viewModel.updateSettings(newS) },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                viewModel.navigateBack()
                            }
                        }
                    }

                    // Modal de Desbloqueio se o cache de 3 horas expirou
                    pendingAuthBook?.let { book ->
                        SecurityUnlockDialog(
                            book = book,
                            onAuthenticateClick = {
                                viewModel.authenticatePendingBook(this@MainActivity)
                            },
                            onDismissRequest = {
                                viewModel.dismissPendingAuth()
                            }
                        )
                    }
                }
            }
        }
    }
}
