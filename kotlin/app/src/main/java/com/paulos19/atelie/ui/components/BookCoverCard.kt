package com.paulos19.atelie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paulos19.atelie.model.Book
import com.paulos19.atelie.theme.DimColors

@Composable
fun BookCoverCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coverBg = try {
        Color(android.graphics.Color.parseColor(book.coverColorHex))
    } catch (_: Exception) {
        DimColors.CoverCharcoal
    }

    Box(
        modifier = modifier
            .width(150.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(coverBg)
            .border(1.dp, DimColors.BorderSubtle, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        // Lombada do Livro (Linha vertical simulando encadernação)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(8.dp)
                .background(DimColors.BgCanvas.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = DimColors.TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    if (book.isProtected) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Protegido por senha/biometria",
                            tint = DimColors.TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = book.title,
                    color = DimColors.TextQuiet,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                if (!book.subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = book.subtitle,
                        color = DimColors.TextMuted,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Serif,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column {
                if (!book.genre.isNullOrBlank()) {
                    Text(
                        text = book.genre,
                        color = DimColors.TextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = "${book.chapters.size} caps • ${book.totalWords} pal.",
                    color = DimColors.TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}
