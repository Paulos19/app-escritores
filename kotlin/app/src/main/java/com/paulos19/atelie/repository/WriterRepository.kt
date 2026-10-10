package com.paulos19.atelie.repository

import android.content.Context
import android.content.SharedPreferences
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.model.Chapter
import com.paulos19.atelie.model.Folder
import com.paulos19.atelie.model.WriterSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WriterRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("atelie_writer_storage", Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
    }

    companion object {
        private const val KEY_FOLDERS = "writer_folders_data"
        private const val KEY_BOOKS = "writer_books_data"
        private const val KEY_SETTINGS = "writer_settings_data"
    }

    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        val raw = prefs.getString(KEY_FOLDERS, null) ?: return@withContext emptyList()
        try {
            json.decodeFromString<List<Folder>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveFolders(folders: List<Folder>) = withContext(Dispatchers.IO) {
        prefs.edit().putString(KEY_FOLDERS, json.encodeToString(folders)).apply()
    }

    suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        val raw = prefs.getString(KEY_BOOKS, null) ?: return@withContext emptyList()
        try {
            json.decodeFromString<List<Book>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        prefs.edit().putString(KEY_BOOKS, json.encodeToString(books)).apply()
    }

    suspend fun getSettings(): WriterSettings = withContext(Dispatchers.IO) {
        val raw = prefs.getString(KEY_SETTINGS, null) ?: return@withContext WriterSettings()
        try {
            json.decodeFromString<WriterSettings>(raw)
        } catch (_: Exception) {
            WriterSettings()
        }
    }

    suspend fun saveSettings(settings: WriterSettings) = withContext(Dispatchers.IO) {
        prefs.edit().putString(KEY_SETTINGS, json.encodeToString(settings)).apply()
    }

    suspend fun seedInitialDataIfEmpty(): Pair<List<Folder>, List<Book>> = withContext(Dispatchers.IO) {
        val existingFolders = getFolders()
        val existingBooks = getBooks()

        if (existingFolders.isEmpty() && existingBooks.isEmpty()) {
            val initialFolder = Folder(
                id = "folder_sample_1",
                name = "Trilogia das Sombras",
                description = "Primeiro rascunho de ficção especulativa",
                colorHex = "#2E3440"
            )

            val initialBook = Book(
                id = "book_sample_1",
                folderId = initialFolder.id,
                title = "O Silêncio dos Vagalumes",
                subtitle = "Livro I - O Chamado Noturno",
                author = "Autor",
                genre = "Fantasia Sombria",
                coverColorHex = "#1F232A",
                synopsis = "Nas colinas cinzentas, onde a luz do sol nunca toca as raízes antigas, segredos há muito enterrados começam a sussurrar.",
                chapters = listOf(
                    Chapter(
                        id = "chap_1",
                        bookId = "book_sample_1",
                        title = "Capítulo 1: A Névoa Espessa",
                        content = "A cidade dormia sob uma cortina de cinzas. Nenhuma lanterna brilhava nos postes de ferro retorcido.\n\nElias ajustou o casaco grosso e observou o relógio da torre. Os ponteiros pareciam hesitar a cada segundo, como se o próprio tempo temesse avançar naquela madrugada fria.\n\n— Você não deveria estar aqui — sussurrou a voz familiar atrás dele.",
                        order = 1,
                        wordCount = 52
                    ),
                    Chapter(
                        id = "chap_2",
                        bookId = "book_sample_1",
                        title = "Capítulo 2: Pegadas no Asfalto",
                        content = "Os rastros conduziam até a margem do canal esquecido. Pegadas irregulares marcavam o solo orvalhado.\n\nNenhum som vinha dos galpões abandonados, apenas o murmúrio incessante da água escura fluindo para o mar.",
                        order = 2,
                        wordCount = 29
                    )
                )
            )

            val initialStandaloneBook = Book(
                id = "book_sample_2",
                folderId = null,
                title = "Caderno de Crônicas",
                subtitle = "Pensamentos soltos e aforismos",
                author = "Notas Pessoais",
                genre = "Ensaios",
                coverColorHex = "#23242B",
                synopsis = "Fragmentos de ideias e ensaios coletados em caminhadas noturnas.",
                chapters = listOf(
                    Chapter(
                        id = "chap_standalone_1",
                        bookId = "book_sample_2",
                        title = "Fragmento I: O Peso das Palavras",
                        content = "Escrever no escuro é como esculpir sombras. As palavras não precisam de brilho, apenas de precisão e quietude.",
                        order = 1,
                        wordCount = 19
                    )
                )
            )

            val newFolders = listOf(initialFolder)
            val newBooks = listOf(initialBook, initialStandaloneBook)

            saveFolders(newFolders)
            saveBooks(newBooks)

            return@withContext Pair(newFolders, newBooks)
        }

        return@withContext Pair(existingFolders, existingBooks)
    }
}
