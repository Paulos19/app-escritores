package com.paulos19.atelie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.theme.DimColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportBottomSheet(
    book: Book,
    onExportDocx: () -> Unit,
    onExportPdf: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = DimColors.BgSurface,
        scrimColor = DimColors.BgCanvas.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Exportar Manuscrito",
                fontSize = 17.sp,
                color = DimColors.TextQuiet
            )
            Text(
                text = "\"${book.title}\" (${book.chapters.size} capítulos, ${book.totalWords} palavras)",
                fontSize = 12.sp,
                color = DimColors.TextMuted,
                modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
            )

            ExportOptionItem(
                icon = Icons.Default.Description,
                title = "Microsoft Word (.docx)",
                description = "Formatado para submissão editorial, agentes literários e revisão.",
                onClick = {
                    onExportDocx()
                    onDismissRequest()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExportOptionItem(
                icon = Icons.Default.PictureAsPdf,
                title = "Documento Tipográfico (.pdf)",
                description = "Diagramação em A4 com numeração de páginas, cabeçalhos e capa.",
                onClick = {
                    onExportPdf()
                    onDismissRequest()
                }
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun ExportOptionItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DimColors.BgElevated)
            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DimColors.BgSubtle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DimColors.TextQuiet,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, color = DimColors.TextQuiet)
            Text(text = description, fontSize = 11.sp, color = DimColors.TextMuted)
        }
    }
}
