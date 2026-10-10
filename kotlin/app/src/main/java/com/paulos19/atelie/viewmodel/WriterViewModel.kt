package com.paulos19.atelie.viewmodel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.paulos19.atelie.export.BackupManager
import com.paulos19.atelie.export.DocxExporter
import com.paulos19.atelie.export.PdfExporter
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.model.Chapter
import com.paulos19.atelie.model.Folder
import com.paulos19.atelie.model.NavigationState
import com.paulos19.atelie.model.ScreenType
import com.paulos19.atelie.model.WriterSettings
import com.paulos19.atelie.repository.ScreenCacheManager
import com.paulos19.atelie.repository.WriterRepository
import com.paulos19.atelie.security.BiometricHelper
import com.paulos19.atelie.security.SecurityManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class WriterViewModel(
    private val repository: WriterRepository,
    private val securityManager: SecurityManager,
    private val screenCacheManager: ScreenCacheManager
) : ViewModel() {

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders.asStateFlow()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _settings = MutableStateFlow(WriterSettings())
    val settings: StateFlow<WriterSettings> = _settings.asStateFlow()

    private val _selectedFolderId = MutableStateFlow<String?>(null)
    val selectedFolderId: StateFlow<String?> = _selectedFolderId.asStateFlow()

    private val _navigationState = MutableStateFlow<NavigationState>(NavigationState.Home)
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    // Controle de bloqueio biométrico pendente
    private val _pendingAuthBook = MutableStateFlow<Book?>(null)
    val pendingAuthBook: StateFlow<Book?> = _pendingAuthBook.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        loadDataAndRestoreScreenCache()
    }

    private fun loadDataAndRestoreScreenCache() {
        viewModelScope.launch {
            val (loadedFolders, loadedBooks) = repository.seedInitialDataIfEmpty()
            _folders.value = loadedFolders
            _books.value = loadedBooks
            _settings.value = repository.getSettings()

            // Restaura Screen Cache
            val cache = screenCacheManager.getCachedScreen()
            when (cache.currentScreen) {
                ScreenType.HOME -> {
                    _navigationState.value = NavigationState.Home
                }
                ScreenType.BOOK_DETAIL -> {
                    val book = loadedBooks.find { it.id == cache.activeBookId }
                    if (book != null) {
                        if (book.isProtected && !securityManager.isBookAuthorized(book.id, book.isProtected)) {
                            // Requer autorização de 3 horas
                            _pendingAuthBook.value = book
                            _navigationState.value = NavigationState.Home
                        } else {
                            _navigationState.value = NavigationState.BookDetail(book.id)
                        }
                    }
                }
                ScreenType.CHAPTER_EDITOR -> {
                    val book = loadedBooks.find { it.id == cache.activeBookId }
                    val chapter = book?.chapters?.find { it.id == cache.activeChapterId }
                    if (book != null && chapter != null) {
                        if (book.isProtected && !securityManager.isBookAuthorized(book.id, book.isProtected)) {
                            _pendingAuthBook.value = book
                            _navigationState.value = NavigationState.Home
                        } else {
                            _navigationState.value = NavigationState.ChapterEditor(book.id, chapter.id)
                        }
                    }
                }
            }
        }
    }

    fun selectFolder(folderId: String?) {
        _selectedFolderId.value = folderId
    }

    fun openBook(book: Book, activity: FragmentActivity? = null) {
        if (book.isProtected && !securityManager.isBookAuthorized(book.id, book.isProtected)) {
            // Requer desbloqueio biométrico ou PIN
            if (activity != null && BiometricHelper.canAuthenticate(activity)) {
                BiometricHelper.showPrompt(
                    activity = activity,
                    title = "Desbloquear \"${book.title}\"",
                    subtitle = "Autorização válida por 3 horas",
                    onSuccess = {
                        securityManager.recordUnlock(book.id)
                        _pendingAuthBook.value = null
                        _navigationState.value = NavigationState.BookDetail(book.id)
                        screenCacheManager.saveScreen(ScreenType.BOOK_DETAIL, book.id, null)
                    },
                    onError = { err ->
                        _statusMessage.value = "Falha ao autenticar: $err"
                    },
                    onFailed = {
                        _statusMessage.value = "Biometria ou PIN não reconhecido"
                    }
                )
            } else {
                _pendingAuthBook.value = book
            }
        } else {
            _navigationState.value = NavigationState.BookDetail(book.id)
            screenCacheManager.saveScreen(ScreenType.BOOK_DETAIL, book.id, null)
        }
    }

    fun authenticatePendingBook(activity: FragmentActivity) {
        val book = _pendingAuthBook.value ?: return
        BiometricHelper.showPrompt(
            activity = activity,
            title = "Desbloquear \"${book.title}\"",
            subtitle = "Autorização válida por 3 horas",
            onSuccess = {
                securityManager.recordUnlock(book.id)
                _pendingAuthBook.value = null
                _navigationState.value = NavigationState.BookDetail(book.id)
                screenCacheManager.saveScreen(ScreenType.BOOK_DETAIL, book.id, null)
            },
            onError = { err ->
                _statusMessage.value = "Erro: $err"
            },
            onFailed = {
                _statusMessage.value = "Biometria ou PIN inválido"
            }
        )
    }

    fun dismissPendingAuth() {
        _pendingAuthBook.value = null
    }

    fun openChapter(bookId: String, chapterId: String) {
        _navigationState.value = NavigationState.ChapterEditor(bookId, chapterId)
        screenCacheManager.saveScreen(ScreenType.CHAPTER_EDITOR, bookId, chapterId)
    }

    fun navigateBack() {
        when (val current = _navigationState.value) {
            is NavigationState.ChapterEditor -> {
                _navigationState.value = NavigationState.BookDetail(current.bookId)
                screenCacheManager.saveScreen(ScreenType.BOOK_DETAIL, current.bookId, null)
            }
            is NavigationState.BookDetail -> {
                _navigationState.value = NavigationState.Home
                screenCacheManager.saveScreen(ScreenType.HOME, null, null)
            }
            NavigationState.Home -> {
                // Já na home
            }
        }
    }

    // --- CRUD Livros ---
    fun createBook(
        title: String,
        synopsis: String? = null,
        genre: String? = null,
        isProtected: Boolean = false,
        coverColorHex: String = "#1E2024"
    ) {
        val newBook = Book(
            id = "book_" + System.currentTimeMillis(),
            folderId = _selectedFolderId.value,
            title = title,
            synopsis = synopsis,
            genre = genre,
            isProtected = isProtected,
            coverColorHex = coverColorHex,
            chapters = emptyList()
        )
        val updated = listOf(newBook) + _books.value
        _books.value = updated
        viewModelScope.launch {
            repository.saveBooks(updated)
        }
        _navigationState.value = NavigationState.BookDetail(newBook.id)
        screenCacheManager.saveScreen(ScreenType.BOOK_DETAIL, newBook.id, null)
    }

    fun updateBook(
        bookId: String,
        title: String,
        synopsis: String?,
        genre: String?,
        isProtected: Boolean,
        coverColorHex: String
    ) {
        val updated = _books.value.map { b ->
            if (b.id == bookId) {
                b.copy(
                    title = title,
                    synopsis = synopsis,
                    genre = genre,
                    isProtected = isProtected,
                    coverColorHex = coverColorHex,
                    updatedAt = System.currentTimeMillis()
                )
            } else b
        }
        _books.value = updated
        viewModelScope.launch {
            repository.saveBooks(updated)
        }
    }

    fun deleteBook(bookId: String) {
        val updated = _books.value.filter { it.id != bookId }
        _books.value = updated
        viewModelScope.launch {
            repository.saveBooks(updated)
        }
        if (_navigationState.value is NavigationState.BookDetail || _navigationState.value is NavigationState.ChapterEditor) {
            _navigationState.value = NavigationState.Home
            screenCacheManager.saveScreen(ScreenType.HOME, null, null)
        }
    }

    // --- CRUD Capítulos ---
    fun createChapter(bookId: String, title: String, synopsis: String? = null) {
        val book = _books.value.find { it.id == bookId } ?: return
        val newChapter = Chapter(
            id = "chap_" + System.currentTimeMillis(),
            bookId = bookId,
            title = title,
            synopsis = synopsis,
            order = book.chapters.size + 1
        )
        val updatedChapters = book.chapters + newChapter
        val updatedBooks = _books.value.map { b ->
            if (b.id == bookId) b.copy(chapters = updatedChapters, updatedAt = System.currentTimeMillis()) else b
        }
        _books.value = updatedBooks
        viewModelScope.launch {
            repository.saveBooks(updatedBooks)
        }
        // Abre direto o novo capítulo
        _navigationState.value = NavigationState.ChapterEditor(bookId, newChapter.id)
        screenCacheManager.saveScreen(ScreenType.CHAPTER_EDITOR, bookId, newChapter.id)
    }

    fun updateChapterContent(bookId: String, chapterId: String, content: String, wordCount: Int) {
        val updatedBooks = _books.value.map { b ->
            if (b.id == bookId) {
                val updatedChapters = b.chapters.map { c ->
                    if (c.id == chapterId) {
                        c.copy(
                            content = content,
                            wordCount = wordCount,
                            updatedAt = System.currentTimeMillis()
                        )
                    } else c
                }
                b.copy(chapters = updatedChapters, updatedAt = System.currentTimeMillis())
            } else b
        }
        _books.value = updatedBooks
        viewModelScope.launch {
            repository.saveBooks(updatedBooks)
        }
    }

    fun deleteChapter(bookId: String, chapterId: String) {
        val book = _books.value.find { it.id == bookId } ?: return
        val updatedChapters = book.chapters.filter { it.id != chapterId }
        val updatedBooks = _books.value.map { b ->
            if (b.id == bookId) b.copy(chapters = updatedChapters, updatedAt = System.currentTimeMillis()) else b
        }
        _books.value = updatedBooks
        viewModelScope.launch {
            repository.saveBooks(updatedBooks)
        }
        if (_navigationState.value is NavigationState.ChapterEditor) {
            _navigationState.value = NavigationState.BookDetail(bookId)
            screenCacheManager.saveScreen(ScreenType.BOOK_DETAIL, bookId, null)
        }
    }

    // --- CRUD Pastas ---
    fun createFolder(name: String, description: String? = null, colorHex: String = "#2E3440") {
        val newFolder = Folder(
            id = "folder_" + System.currentTimeMillis(),
            name = name,
            description = description,
            colorHex = colorHex
        )
        val updated = _folders.value + newFolder
        _folders.value = updated
        viewModelScope.launch {
            repository.saveFolders(updated)
        }
    }

    fun deleteFolder(folderId: String) {
        val updatedFolders = _folders.value.filter { it.id != folderId }
        val updatedBooks = _books.value.map { b ->
            if (b.folderId == folderId) b.copy(folderId = null) else b
        }
        _folders.value = updatedFolders
        _books.value = updatedBooks
        if (_selectedFolderId.value == folderId) {
            _selectedFolderId.value = null
        }
        viewModelScope.launch {
            repository.saveFolders(updatedFolders)
            repository.saveBooks(updatedBooks)
        }
    }

    // --- Configurações & Exportações ---
    fun updateSettings(newSettings: WriterSettings) {
        _settings.value = newSettings
        viewModelScope.launch {
            repository.saveSettings(newSettings)
        }
    }

    fun exportBookDocx(context: Context, book: Book): File? {
        return try {
            val file = DocxExporter.exportBook(context, book)
            _statusMessage.value = "DOCX gerado: ${file.name}"
            file
        } catch (e: Exception) {
            _statusMessage.value = "Erro ao exportar DOCX: ${e.message}"
            null
        }
    }

    fun exportBookPdf(context: Context, book: Book): File? {
        return try {
            val file = PdfExporter.exportBook(context, book)
            _statusMessage.value = "PDF gerado: ${file.name}"
            file
        } catch (e: Exception) {
            _statusMessage.value = "Erro ao exportar PDF: ${e.message}"
            null
        }
    }

    fun createBackup(context: Context): File? {
        return try {
            val file = BackupManager.createBackup(
                context,
                _folders.value,
                _books.value,
                _settings.value
            )
            _statusMessage.value = "Backup salvo com sucesso: ${file.name}"
            file
        } catch (e: Exception) {
            _statusMessage.value = "Erro ao criar backup: ${e.message}"
            null
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}

class WriterViewModelFactory(
    private val repository: WriterRepository,
    private val securityManager: SecurityManager,
    private val screenCacheManager: ScreenCacheManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WriterViewModel(repository, securityManager, screenCacheManager) as T
    }
}
