package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.cyberdefender.model.OsiKnowledge
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP Lunaスタイルのクラシックウィンドウコンテナ (絵文字完全排除)
 */
@Composable
fun XpWindow(
    title: String,
    modifier: Modifier = Modifier,
    onCloseClick: (() -> Unit)? = null,
    menuItems: List<Pair<String, () -> Unit>> = emptyList(),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
            .background(XpWindowBg)
            .border(2.dp, XpBlueBorder, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
            .shadow(6.dp, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
    ) {
        // Windows XP 青色Lunaグラデーションタイトルバー
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A90E2),
                            XpBlueTitleStart,
                            XpBlueTitleEnd,
                            Color(0xFF163E99)
                        )
                    )
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color(0xFF1E3A8A), RoundedCornerShape(2.dp))
                            .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "XP",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PixelFontFamily
                        )
                    }

                    Text(
                        text = title,
                        color = XpTextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    XpWindowControlBtn(label = "_")
                    XpWindowControlBtn(label = "[ ]")
                    XpWindowCloseBtn(onClick = onCloseClick)
                }
            }
        }

        // スマホ操作に最適化したXP風メニューバー
        if (menuItems.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEDE9D8))
                    .border(width = 1.dp, color = XpBorderShadow)
                    .padding(horizontal = 4.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                menuItems.forEach { (label, onClick) ->
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .clickable(onClick = onClick)
                            .background(Color(0xFFEDE9D8))
                            .border(1.dp, Color(0xFFC0BCAC), RoundedCornerShape(2.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = XpTextDark,
                            fontFamily = PixelFontFamily,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // ウィンドウ内部コンテンツ
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            content = content
        )
    }
}

@Composable
private fun XpWindowControlBtn(label: String) {
    Box(
        modifier = Modifier
            .size(24.dp, 22.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF6EA6FF), Color(0xFF285FCC))
                )
            )
            .border(1.dp, Color(0xFF103680), RoundedCornerShape(3.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily
        )
    }
}

@Composable
private fun XpWindowCloseBtn(onClick: (() -> Unit)?) {
    Box(
        modifier = Modifier
            .size(24.dp, 22.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF87171), XpCloseRed)
                )
            )
            .border(1.dp, Color(0xFF8B1A1A), RoundedCornerShape(3.dp))
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "X",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily
        )
    }
}

/**
 * Windows XP Luna風の立体押しボタン (操作域拡大・絵文字完全排除)
 */
@Composable
fun XpButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false,
    fontSize: Int = 12,
    minHeight: Int = 44
) {
    val bgBrush = if (enabled) {
        if (isPrimary) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF99E67B),
                    XpStartGreen,
                    Color(0xFF2E7D28)
                )
            )
        } else {
            Brush.verticalGradient(
                listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF2EFE4),
                    Color(0xFFDFDAC9),
                    Color(0xFFCEC8B2)
                )
            )
        }
    } else {
        Brush.verticalGradient(listOf(Color(0xFFE5E5E5), Color(0xFFCCCCCC)))
    }

    val textColor = if (enabled) {
        if (isPrimary) Color.White else XpTextDark
    } else {
        XpTextMuted
    }

    Box(
        modifier = modifier
            .heightIn(min = minHeight.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(bgBrush)
            .border(
                1.dp,
                if (isPrimary) Color(0xFF1E521B) else XpButtonBorderDark,
                RoundedCornerShape(3.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Windows XP クラシックダイアログ
 */
@Composable
fun XpClassicDialog(
    title: String,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(4.dp))
                .background(XpWindowBg)
                .border(2.dp, XpBlueBorder, RoundedCornerShape(4.dp))
                .shadow(12.dp, RoundedCornerShape(4.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // タイトルバー
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF4A90E2), XpBlueTitleStart, XpBlueTitleEnd)
                            )
                        )
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PixelFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        XpWindowCloseBtn(onClick = onDismissRequest)
                    }
                }

                // ダイアログ本文
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    content = content
                )
            }
        }
    }
}

/**
 * Windows XP風 タスクバー吹き出し通知
 */
@Composable
fun XpBalloonNotification(
    speaker: String,
    message: String,
    modifier: Modifier = Modifier,
    isAngry: Boolean = false
) {
    val bgColor = if (isAngry) Color(0xFFFFECEC) else XpBalloonBg
    val borderColor = if (isAngry) Color(0xFFE53935) else XpBalloonBorder

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(3.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (isAngry) Color(0xFFDC2626) else Color(0xFFF59E0B), RoundedCornerShape(2.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAngry) "!" else "i",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = speaker,
                    color = if (isAngry) Color(0xFFC62828) else Color(0xFF7A4B00),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message,
                    color = XpTextDark,
                    fontSize = 11.sp,
                    fontFamily = PixelFontFamily,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

/**
 * 共通タブ切り替えボタン
 */
@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Color.White else Color(0xFFD6D3C4)
    val border = if (isSelected) Color(0xFF003C74) else Color.Transparent

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(2.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF003C74) else Color(0xFF475569),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily
        )
    }
}

/**
 * OSI早見表カード
 */
@Composable
fun OsiKnowledgeCard(
    item: OsiKnowledge,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White)
            .border(1.dp, XpBorderShadow, RoundedCornerShape(3.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(0xFF003399), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.osiInitial,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
                Text(
                    text = "第${item.layerNumber}層: ${item.layerName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003399),
                    fontFamily = PixelFontFamily
                )
            }
        }

        Text(
            text = "代表プロトコル: ${item.representativeProtocols}",
            fontSize = 11.sp,
            color = Color(0xFF1E3A8A),
            fontFamily = PixelFontFamily
        )
        Text(
            text = "代表的な脅威: ${item.typicalThreats}",
            fontSize = 11.sp,
            color = Color(0xFF991B1B),
            fontFamily = PixelFontFamily
        )
        Text(
            text = "現実の防護策: ${item.realWorldDefense}",
            fontSize = 11.sp,
            color = Color(0xFF166534),
            fontFamily = PixelFontFamily,
            lineHeight = 15.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF3C7))
                .padding(6.dp)
        ) {
            Text(
                text = "試験Tips: ${item.itExamTip}",
                fontSize = 10.sp,
                color = Color(0xFF92400E),
                fontFamily = PixelFontFamily,
                lineHeight = 14.sp
            )
        }
    }
}
