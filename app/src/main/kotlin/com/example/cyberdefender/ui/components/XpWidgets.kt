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
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP Lunaスタイルのクラシックウィンドウコンテナ
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
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(XpWindowBg)
            .border(2.dp, XpBlueBorder, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .shadow(4.dp, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
    ) {
        // Windows XP 青色Lunaグラデーションタイトルバー
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
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
                // タイトル & アイコン
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "🪟",
                        fontSize = 13.sp
                    )
                    Text(
                        text = title,
                        color = XpTextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // [_] [□] [✕] コントロールボタン
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    XpWindowControlBtn(label = "_")
                    XpWindowControlBtn(label = "□")
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
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                menuItems.forEach { (label, onClick) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .clickable(onClick = onClick)
                            .background(Color(0xFFEDE9D8))
                            .border(1.dp, Color(0xFFC0BCAC), RoundedCornerShape(2.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
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
                .padding(4.dp),
            content = content
        )
    }
}

@Composable
private fun XpWindowControlBtn(label: String) {
    Box(
        modifier = Modifier
            .size(20.dp, 18.dp)
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
            fontFamily = PixelFontFamily,
            lineHeight = 10.sp
        )
    }
}

@Composable
private fun XpWindowCloseBtn(onClick: (() -> Unit)?) {
    Box(
        modifier = Modifier
            .size(20.dp, 18.dp)
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
            text = "✕",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily,
            lineHeight = 10.sp
        )
    }
}

/**
 * Windows XP Luna風の立体押しボタン（改行防止・ドット感フォント対応）
 */
@Composable
fun XpButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false,
    fontSize: Int = 11
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
            .clip(RoundedCornerShape(3.dp))
            .background(bgBrush)
            .border(
                1.dp,
                if (isPrimary) Color(0xFF1E521B) else Color(0xFF003C74),
                RoundedCornerShape(3.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
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
 * Windows XP風 タスクバー吹き出し通知（バルーンチップ）
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
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .padding(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (isAngry) "💢" else "📞",
                fontSize = 14.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = speaker,
                    color = if (isAngry) Color(0xFFC62828) else Color(0xFF7A4B00),
                    fontSize = 11.sp,
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
                    lineHeight = 15.sp
                )
            }
        }
    }
}

/**
 * Windows XP風ステータスバー
 */
@Composable
fun XpStatusBar(
    text: String,
    modifier: Modifier = Modifier,
    secondaryText: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(22.dp)
            .background(Color(0xFFEDE9D8))
            .border(1.dp, Color(0xFFB0AA9B))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = XpTextDark,
            fontSize = 10.sp,
            fontFamily = PixelFontFamily,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (secondaryText != null) {
            Box(
                modifier = Modifier
                    .border(1.dp, Color(0xFF807A6D))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = secondaryText,
                    color = XpTextDark,
                    fontSize = 9.sp,
                    fontFamily = PixelFontFamily,
                    maxLines = 1
                )
            }
        }
    }
}
