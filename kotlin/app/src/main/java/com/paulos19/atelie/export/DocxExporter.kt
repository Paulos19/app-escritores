package com.paulos19.atelie.export

import android.content.Context
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.model.Chapter
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Exportador nativo de documentos Microsoft Word (.docx).
 *
 * Constrói o pacote Office Open XML padrão (ZIP com word/document.xml, [Content_Types].xml, etc.)
 * sem depender de bibliotecas externas pesadas, garantindo compatibilidade total com
 * MS Word, Google Docs e LibreOffice.
 */
object DocxExporter {

    fun exportBook(context: Context, book: Book): File {
        val exportDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "export")
        if (!exportDir.exists()) exportDir.mkdirs()

        val sanitizedTitle = book.title.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val file = File(exportDir, "${sanitizedTitle}_manuscrito.docx")

        ZipOutputStream(FileOutputStream(file)).use { zip ->
            // 1. [Content_Types].xml
            addZipEntry(zip, "[Content_Types].xml", contentTypesXml())

            // 2. _rels/.rels
            addZipEntry(zip, "_rels/.rels", rootRelsXml())

            // 3. word/_rels/document.xml.rels
            addZipEntry(zip, "word/_rels/document.xml.rels", documentRelsXml())

            // 4. word/document.xml
            addZipEntry(zip, "word/document.xml", generateDocumentXml(book))
        }

        return file
    }

    fun exportChapter(context: Context, book: Book, chapter: Chapter): File {
        val exportDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "export")
        if (!exportDir.exists()) exportDir.mkdirs()

        val sanitizedTitle = chapter.title.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val file = File(exportDir, "${sanitizedTitle}.docx")

        val singleChapterBook = book.copy(chapters = listOf(chapter))

        ZipOutputStream(FileOutputStream(file)).use { zip ->
            addZipEntry(zip, "[Content_Types].xml", contentTypesXml())
            addZipEntry(zip, "_rels/.rels", rootRelsXml())
            addZipEntry(zip, "word/_rels/document.xml.rels", documentRelsXml())
            addZipEntry(zip, "word/document.xml", generateDocumentXml(singleChapterBook))
        }

        return file
    }

    private fun addZipEntry(zip: ZipOutputStream, entryName: String, content: String) {
        val entry = ZipEntry(entryName)
        zip.putNextEntry(entry)
        val bytes = content.toByteArray(StandardCharsets.UTF_8)
        zip.write(bytes, 0, bytes.size)
        zip.closeEntry()
    }

    private fun escapeXml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun generateDocumentXml(book: Book): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n")
        sb.append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n")
        sb.append("<w:body>\n")

        // Capa / Título do Livro
        sb.append("<w:p>\n")
        sb.append("  <w:pPr><w:jc w:val=\"center\"/><w:spacing w:before=\"720\" w:after=\"240\"/></w:pPr>\n")
        sb.append("  <w:r><w:rPr><w:b/><w:sz w:val=\"56\"/><w:rFonts w:ascii=\"Georgia\" w:hAnsi=\"Georgia\"/></w:rPr>\n")
        sb.append("  <w:t>").append(escapeXml(book.title)).append("</w:t></w:r>\n")
        sb.append("</w:p>\n")

        // Autor / Subtítulo
        if (!book.subtitle.isNullOrBlank()) {
            sb.append("<w:p>\n")
            sb.append("  <w:pPr><w:jc w:val=\"center\"/><w:spacing w:after=\"120\"/></w:pPr>\n")
            sb.append("  <w:r><w:rPr><w:i/><w:sz w:val=\"32\"/><w:rFonts w:ascii=\"Georgia\" w:hAnsi=\"Georgia\"/></w:rPr>\n")
            sb.append("  <w:t>").append(escapeXml(book.subtitle)).append("</w:t></w:r>\n")
            sb.append("</w:p>\n")
        }

        sb.append("<w:p>\n")
        sb.append("  <w:pPr><w:jc w:val=\"center\"/><w:spacing w:after=\"1440\"/></w:pPr>\n")
        sb.append("  <w:r><w:rPr><w:sz w:val=\"28\"/><w:rFonts w:ascii=\"Georgia\" w:hAnsi=\"Georgia\"/></w:rPr>\n")
        sb.append("  <w:t>").append(escapeXml(book.author)).append("</w:t></w:r>\n")
        sb.append("</w:p>\n")

        // Quebra de página após capa se houver mais de um capítulo
        if (book.chapters.isNotEmpty()) {
            sb.append("<w:p><w:r><w:br w:type=\"page\"/></w:r></w:p>\n")
        }

        // Capítulos
        book.chapters.forEachIndexed { index, chapter ->
            // Título do Capítulo
            sb.append("<w:p>\n")
            sb.append("  <w:pPr><w:jc w:val=\"center\"/><w:spacing w:before=\"720\" w:after=\"480\"/></w:pPr>\n")
            sb.append("  <w:r><w:rPr><w:b/><w:sz w:val=\"40\"/><w:rFonts w:ascii=\"Georgia\" w:hAnsi=\"Georgia\"/></w:rPr>\n")
            sb.append("  <w:t>").append(escapeXml(chapter.title)).append("</w:t></w:r>\n")
            sb.append("</w:p>\n")

            // Parágrafos do Capítulo
            val paragraphs = chapter.content.split("\n")
            for (para in paragraphs) {
                val trimmed = para.trim()
                if (trimmed.isEmpty()) {
                    sb.append("<w:p><w:pPr><w:spacing w:after=\"120\"/></w:pPr></w:p>\n")
                    continue
                }

                sb.append("<w:p>\n")
                sb.append("  <w:pPr>\n")
                sb.append("    <w:ind w:firstLine=\"720\"/>\n") // Recuo da primeira linha (ABNT / Literário padrão)
                sb.append("    <w:spacing w:line=\"360\" w:lineRule=\"auto\" w:after=\"80\"/>\n") // Espaçamento 1.5x
                sb.append("    <w:jc w:val=\"both\"/>\n") // Justificado
                sb.append("  </w:pPr>\n")
                sb.append("  <w:r><w:rPr><w:sz w:val=\"24\"/><w:rFonts w:ascii=\"Georgia\" w:hAnsi=\"Georgia\"/></w:rPr>\n")
                sb.append("  <w:t xml:space=\"preserve\">").append(escapeXml(trimmed)).append("</w:t></w:r>\n")
                sb.append("</w:p>\n")
            }

            // Quebra de página entre capítulos
            if (index < book.chapters.size - 1) {
                sb.append("<w:p><w:r><w:br w:type=\"page\"/></w:r></w:p>\n")
            }
        }

        sb.append("</w:body>\n")
        sb.append("</w:document>")
        return sb.toString()
    }

    private fun contentTypesXml(): String =
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
</Types>""".trimIndent()

    private fun rootRelsXml(): String =
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>""".trimIndent()

    private fun documentRelsXml(): String =
        """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
</Relationships>""".trimIndent()
}
