package com.paulos19.atelie.export

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.model.Chapter
import java.io.File
import java.io.FileOutputStream

/**
 * Exportador nativo de documentos PDF utilizando android.graphics.pdf.PdfDocument.
 * Realiza paginação automática, cabeçalhos, rodapés com numeração e tipografia editorial.
 */
object PdfExporter {

    private const val PAGE_WIDTH = 595  // Formato A4 padrão em pontos (72 dpi)
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_HORIZONTAL = 54
    private const val MARGIN_TOP = 54
    private const val MARGIN_BOTTOM = 54
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN_HORIZONTAL * 2)

    fun exportBook(context: Context, book: Book): File {
        val exportDir = File(context.getExternalFilesDir(null) ?: context.filesDir, "export")
        if (!exportDir.exists()) exportDir.mkdirs()

        val sanitizedTitle = book.title.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val file = File(exportDir, "${sanitizedTitle}_livro.pdf")

        val pdfDoc = PdfDocument()
        var pageNumber = 1

        val titlePaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val authorPaint = TextPaint().apply {
            color = Color.DKGRAY
            textSize = 14f
            isAntiAlias = true
        }

        val chapterTitlePaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val bodyPaint = TextPaint().apply {
            color = Color.rgb(40, 40, 40)
            textSize = 12f
            isAntiAlias = true
        }

        val footerPaint = TextPaint().apply {
            color = Color.GRAY
            textSize = 10f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Página de Capa / Rosto
        val coverPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        val coverPage = pdfDoc.startPage(coverPageInfo)
        val coverCanvas = coverPage.canvas

        coverCanvas.drawText(book.title, MARGIN_HORIZONTAL.toFloat(), 300f, titlePaint)
        if (!book.subtitle.isNullOrBlank()) {
            coverCanvas.drawText(book.subtitle, MARGIN_HORIZONTAL.toFloat(), 340f, authorPaint)
        }
        coverCanvas.drawText("Por: ${book.author}", MARGIN_HORIZONTAL.toFloat(), 400f, authorPaint)

        pdfDoc.finishPage(coverPage)
        pageNumber++

        // Renderiza cada capítulo com paginação contínua
        for (chapter in book.chapters) {
            val fullText = buildString {
                append("\n\n")
                append(chapter.content)
            }

            val layout = StaticLayout.Builder.obtain(
                fullText,
                0,
                fullText.length,
                bodyPaint,
                CONTENT_WIDTH
            )
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(4f, 1.3f)
                .build()

            var lineIndex = 0
            val totalLines = layout.lineCount

            while (lineIndex < totalLines) {
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                val page = pdfDoc.startPage(pageInfo)
                val canvas = page.canvas

                var currentY = MARGIN_TOP.toFloat()

                // Cabeçalho da página se for a primeira do capítulo
                if (lineIndex == 0) {
                    canvas.drawText(chapter.title, MARGIN_HORIZONTAL.toFloat(), currentY + 20f, chapterTitlePaint)
                    currentY += 50f
                } else {
                    // Header de cabeçalho com nome do livro
                    canvas.drawText(book.title, MARGIN_HORIZONTAL.toFloat(), currentY, footerPaint)
                    currentY += 24f
                }

                val availableHeight = PAGE_HEIGHT - MARGIN_BOTTOM - currentY

                // Desenha as linhas que cabem na página atual
                val startLine = lineIndex
                var drawnHeight = 0f

                while (lineIndex < totalLines) {
                    val lineHeight = layout.getLineBottom(lineIndex) - layout.getLineTop(lineIndex)
                    if (drawnHeight + lineHeight > availableHeight) {
                        break
                    }
                    drawnHeight += lineHeight
                    lineIndex++
                }

                canvas.save()
                canvas.translate(MARGIN_HORIZONTAL.toFloat(), currentY - layout.getLineTop(startLine))
                // Limita o canvas para a fatia da página
                canvas.clipRect(0, layout.getLineTop(startLine), CONTENT_WIDTH, layout.getLineBottom(lineIndex - 1))
                layout.draw(canvas)
                canvas.restore()

                // Rodapé com número da página
                canvas.drawText(
                    "$pageNumber",
                    (PAGE_WIDTH / 2).toFloat(),
                    (PAGE_HEIGHT - 30).toFloat(),
                    footerPaint
                )

                pdfDoc.finishPage(page)
                pageNumber++
            }
        }

        FileOutputStream(file).use { out ->
            pdfDoc.writeTo(out)
        }
        pdfDoc.close()

        return file
    }

    fun exportChapter(context: Context, book: Book, chapter: Chapter): File {
        return exportBook(context, book.copy(chapters = listOf(chapter)))
    }
}
