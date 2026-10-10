package com.paulos19.atelie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulos19.atelie.model.EditorFontFamily
import com.paulos19.atelie.model.WriterSettings
import com.paulos19.atelie.theme.DimColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarExpandedModal(
    settings: WriterSettings,
    wordCount: Int,
    charCount: Int,
    readingMinutes: Int,
    onUpdateSettings: (WriterSettings) -> Unit,
    onInsertText: (String) -> Unit,
    onWrapSelection: (String, String) -> Unit,
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
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header do Modal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configurações de Escrita & Ferramentas",
                    fontSize = 16.sp,
                    color = DimColors.TextQuiet
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = DimColors.TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seção 1: Tipografia & Ergonomia
            SectionHeader(title = "Tipografia & Espaçamento")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditorFontFamily.entries.forEach { family ->
                    val isSelected = settings.fontFamily == family
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) DimColors.AccentSlate else DimColors.BgSubtle)
                            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(8.dp))
                            .clickable { onUpdateSettings(settings.copy(fontFamily = family)) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = family.name,
                            fontSize = 13.sp,
                            color = if (isSelected) DimColors.TextQuiet else DimColors.TextDimmed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tamanho da Fonte
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tamanho da Fonte", fontSize = 13.sp, color = DimColors.TextDimmed)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (settings.fontSizeSp > 13) {
                                onUpdateSettings(settings.copy(fontSizeSp = settings.fontSizeSp - 1))
                            }
                        }
                    ) {
                        Text(text = "A-", color = DimColors.TextDimmed, fontSize = 14.sp)
                    }
                    Text(
                        text = "${settings.fontSizeSp} pt",
                        fontSize = 13.sp,
                        color = DimColors.TextQuiet,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            if (settings.fontSizeSp < 28) {
                                onUpdateSettings(settings.copy(fontSizeSp = settings.fontSizeSp + 1))
                            }
                        }
                    ) {
                        Text(text = "A+", color = DimColors.TextDimmed, fontSize = 14.sp)
                    }
                }
            }

            // Entrelinha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Espaçamento de Linhas", fontSize = 13.sp, color = DimColors.TextDimmed)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(1.4f, 1.6f, 1.9f).forEach { spacing ->
                        val isSelected = Math.abs(settings.lineHeightMultiplier - spacing) < 0.05f
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) DimColors.AccentSlate else DimColors.BgSubtle)
                                .clickable { onUpdateSettings(settings.copy(lineHeightMultiplier = spacing)) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${spacing}x",
                                fontSize = 12.sp,
                                color = if (isSelected) DimColors.TextQuiet else DimColors.TextDimmed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seção 2: Ferramentas Avançadas de Processador de Texto
            SectionHeader(title = "Inserção Rápida de Caracteres Literários")

            FlowRowLayout {
                ExpandedToolChip(label = "— Travessão de Diálogo") { onInsertText("— ") }
                ExpandedToolChip(label = "« » Aspas Francesas") { onWrapSelection("«", "»") }
                ExpandedToolChip(label = "“ ” Aspas Curvas") { onWrapSelection("“", "”") }
                ExpandedToolChip(label = "… Reticências") { onInsertText("…") }
                ExpandedToolChip(label = "* * * Quebra de Cena") { onInsertText("\n\n* * *\n\n") }
                ExpandedToolChip(label = "§ Seção") { onInsertText("§ ") }
                ExpandedToolChip(label = "Notas de Rodapé [^1]") { onInsertText("[^1]") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seção 3: Estatísticas da Obra
            SectionHeader(title = "Estatísticas em Tempo Real")

            Card(
                colors = CardDefaults.cardColors(containerColor = DimColors.BgElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    StatLine(label = "Palavras no Capítulo:", value = "$wordCount")
                    StatLine(label = "Caracteres Totais:", value = "$charCount")
                    StatLine(label = "Estimativa de Leitura:", value = "~$readingMinutes min")
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        color = DimColors.TextMuted,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun StatLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = DimColors.TextMuted, fontSize = 13.sp)
        Text(text = value, color = DimColors.TextDimmed, fontSize = 13.sp)
    }
}

@Composable
private fun ExpandedToolChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 6.dp, bottom = 6.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(DimColors.BgSubtle)
            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = DimColors.TextDimmed)
    }
}

@Composable
private fun FlowRowLayout(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Implementação simplificada de linha
        content()
    }
}
