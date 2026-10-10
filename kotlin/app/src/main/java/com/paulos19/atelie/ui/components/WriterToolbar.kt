package com.paulos19.atelie.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulos19.atelie.theme.DimColors

enum class ToolbarTab(val label: String) {
    DIALOGUE("Diálogo"),
    STYLE("Estilo"),
    STRUCTURE("Estrutura")
}

@Composable
fun WriterToolbar(
    wordCount: Int,
    onInsertText: (String) -> Unit,
    onWrapSelection: (String, String) -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ToolbarTab.DIALOGUE) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .imePadding(),
        color = DimColors.BgElevated,
        tonalElevation = 4.dp,
        border = BorderStroke(1.dp, DimColors.BorderSubtle)
    ) {
        Column {
            // Linha Superior: Abas e Botão de Expansão
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ToolbarTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            color = if (isSelected) DimColors.TextQuiet else DimColors.TextMuted,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) DimColors.BgSubtle else DimColors.BgElevated)
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$wordCount pal.",
                        fontSize = 11.sp,
                        color = DimColors.TextMuted
                    )

                    // Botão Expandir Toolbar
                    IconButton(
                        onClick = onExpandClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Expandir Ferramentas",
                            tint = DimColors.TextDimmed,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = DimColors.BorderSubtle, thickness = 0.5.dp)

            // Linha Inferior: Botões Rápidos por Categoria
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (selectedTab) {
                    ToolbarTab.DIALOGUE -> {
                        ToolbarActionButton(label = "— Travessão") { onInsertText("— ") }
                        ToolbarActionButton(label = "« »") { onWrapSelection("«", "»") }
                        ToolbarActionButton(label = "“ ”") { onWrapSelection("“", "”") }
                        ToolbarActionButton(label = "…") { onInsertText("…") }
                        ToolbarActionButton(label = "* * *") { onInsertText("\n\n* * *\n\n") }
                        ToolbarActionButton(label = "Novo Diálogo") { onInsertText("\n— ") }
                    }

                    ToolbarTab.STYLE -> {
                        ToolbarIconActionButton(
                            icon = Icons.Default.FormatBold,
                            description = "Negrito"
                        ) { onWrapSelection("**", "**") }

                        ToolbarIconActionButton(
                            icon = Icons.Default.FormatItalic,
                            description = "Itálico"
                        ) { onWrapSelection("*", "*") }

                        ToolbarActionButton(label = "~Tachado~") { onWrapSelection("~~", "~~") }

                        ToolbarIconActionButton(
                            icon = Icons.Default.FormatQuote,
                            description = "Citação"
                        ) { onInsertText("\n> ") }
                    }

                    ToolbarTab.STRUCTURE -> {
                        ToolbarIconActionButton(
                            icon = Icons.Default.Title,
                            description = "Título H1"
                        ) { onInsertText("\n# ") }

                        ToolbarActionButton(label = "H2") { onInsertText("\n## ") }
                        ToolbarActionButton(label = "H3") { onInsertText("\n### ") }
                        ToolbarActionButton(label = "• Lista") { onInsertText("\n- ") }
                        ToolbarActionButton(label = "1. Lista") { onInsertText("\n1. ") }
                    }
                }
            }
        }
    }
}

@Composable
fun ToolbarActionButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(DimColors.BgSubtle)
            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = DimColors.TextDimmed
        )
    }
}

@Composable
fun ToolbarIconActionButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(DimColors.BgSubtle)
            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = DimColors.TextDimmed,
            modifier = Modifier.size(16.dp)
        )
    }
}
