package com.paulos19.atelie.export

import android.content.Context
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.model.Folder
import com.paulos19.atelie.model.WriterSettings
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class BackupSnapshot(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val appName: String = "Ateliê do Escritor",
    val folders: List<Folder>,
    val books: List<Book>,
    val settings: WriterSettings
)

object BackupManager {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun createBackup(
        context: Context,
        folders: List<Folder>,
        books: List<Book>,
        settings: WriterSettings
    ): File {
        val backupDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()

        val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val backupFile = File(backupDir, "atelie_backup_$dateStr.json")

        val snapshot = BackupSnapshot(
            folders = folders,
            books = books,
            settings = settings
        )

        val jsonString = json.encodeToString(snapshot)
        backupFile.writeText(jsonString)

        return backupFile
    }

    fun listBackups(context: Context): List<File> {
        val backupDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "backups")
        if (!backupDir.exists()) return emptyList()
        return backupDir.listFiles { file -> file.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun restoreBackup(file: File): BackupSnapshot {
        val jsonString = file.readText()
        return json.decodeFromString(jsonString)
    }
}
