package com.paulos19.atelie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.theme.DimColors

@Composable
fun MetricsDropdown(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    books: List<Book>,
    onBackupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalWords = books.sumOf { it.totalWords }
    val totalChapters = books.sumOf { it.chapters.size }
    val totalBooks = books.size
    val estimatedMinutes = Math.max(1, (totalWords / 200.0).toInt())

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .background(DimColors.BgElevated)
            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(8.dp))
            .width(260.dp)
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = DimColors.TextQuiet,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Métricas do Ateliê",
                    color = DimColors.TextQuiet,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            MetricRow(label = "Total de Palavras", value = "$totalWords")
            MetricRow(label = "Total de Livros", value = "$totalBooks")
            MetricRow(label = "Total de Capítulos", value = "$totalChapters")
            MetricRow(label = "Tempo de Leitura Total", value = "~$estimatedMinutes min")

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DimColors.BorderSubtle)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onDismissRequest()
                    onBackupClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DimColors.BgSubtle,
                    contentColor = DimColors.TextDimmed
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Gerar Backup Local", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = DimColors.TextMuted, fontSize = 12.sp)
        Text(text = value, color = DimColors.TextDimmed, fontSize = 12.sp)
    }
}
