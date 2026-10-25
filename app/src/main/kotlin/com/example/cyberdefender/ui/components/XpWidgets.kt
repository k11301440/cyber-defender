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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.OsiLayer
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP Lunaスタイルのクラシックウィンドウコンテナ
 */
@Composable
fun XpWindow(
    title: String,
    modifier: Modifier = Modifier,
    onCloseClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(XpWindowBg)
            .border(2.dp, XpBlueBorder, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
    ) {
        // Windows XP 青色Lunaグラデーションタイトルバー
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3882F6),
                            XpBlueTitleStart,
                            XpBlueTitleEnd,
                            Color(0xFF1E4AB0)
                        )
                    )
                )
                .padding(horizontal = 6.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🪟",
                        fontSize = 12.sp
                    )
                    Text(
                        text = title,
                        color = XpTextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                // [_] [□] [×] ボタン
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    XpWindowControlBtn(label = "_")
                    XpWindowControlBtn(label = "□")
                    XpWindowCloseBtn(onClick = onCloseClick)
                }
            }
        }

        // クラシックメニューバー風ヘッダー
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(XpWindowBg)
                .border(width = 1.dp, color = XpBorderShadow)
                .padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("ファイル(F)", fontSize = 10.sp, color = XpTextDark)
            Text("防護(S)", fontSize = 10.sp, color = XpTextDark)
            Text("社内連絡(M)", fontSize = 10.sp, color = XpTextDark)
            Text("ヘルプ(H)", fontSize = 10.sp, color = XpTextDark)
        }

        // ウィンドウ内部コンテンツ
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            content = content
        )
    }
}

@Composable
private fun XpWindowControlBtn(label: String) {
    Box(
        modifier = Modifier
            .size(18.dp, 16.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF6EA6FF), Color(0xFF285FCC))
                )
            )
            .border(1.dp, Color(0xFF103680), RoundedCornerShape(2.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 9.sp
        )
    }
}

@Composable
private fun XpWindowCloseBtn(onClick: (() -> Unit)?) {
    Box(
        modifier = Modifier
            .size(18.dp, 16.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFF87171), XpCloseRed)
                )
            )
            .border(1.dp, Color(0xFF8B1A1A), RoundedCornerShape(2.dp))
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "✕",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 9.sp
        )
    }
}

/**
 * Windows XP Luna風の立体押しボタン
 */
@Composable
fun XpButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false
) {
    val bgBrush = if (enabled) {
        if (isPrimary) {
            Brush.verticalGradient(listOf(Color(0xFF8FD86E), XpStartGreen, Color(0xFF246620)))
        } else {
            Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFECE9D8), Color(0xFFDBD6C4)))
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
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
    }
}

/**
 * XP名物：緑のブロックが並ぶプログレスバー
 */
@Composable
fun XpBlockProgressBar(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val ratio = (current.toFloat() / max.coerceAtLeast(1)).coerceIn(0f, 1f)
    val totalBlocks = 16
    val activeBlocks = (totalBlocks * ratio).toInt()

    Box(
        modifier = modifier
            .height(18.dp)
            .background(XpCardBg)
            .border(1.dp, XpBorderDark)
            .padding(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (i in 0 until totalBlocks) {
                val isActive = i < activeBlocks
                val blockModifier = if (isActive) {
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF76E24E), XpProgressGreen, Color(0xFF15803D))
                            )
                        )
                } else {
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                }
                Box(modifier = blockModifier)
            }
        }
    }
}

/**
 * XP名物：黄色いバルーン吹き出し（社員クレーム・小言）
 */
@Composable
fun XpBalloonNotification(
    speaker: String,
    message: String,
    isAngry: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(6.dp))
            .background(if (isAngry) Color(0xFFFFEEEE) else XpBalloonBg)
            .border(1.dp, if (isAngry) XpAlertRed else XpBalloonBorder, RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isAngry) "📞😡" else "📞💬",
                fontSize = 16.sp
            )
            Column {
                Text(
                    text = "内線着信: $speaker",
                    color = if (isAngry) XpAlertRed else Color(0xFF444444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "「$message」",
                    color = XpTextDark,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

/**
 * アプセトネデブ (OSI 7層) バッジ
 */
@Composable
fun OsiLayerBadge(
    layer: OsiLayer,
    modifier: Modifier = Modifier
) {
    val (shortName, color) = when (layer) {
        OsiLayer.L7_APPLICATION -> Pair("ア: L7 アプリケーション層", LayerAppColor)
        OsiLayer.L6_PRESENTATION -> Pair("プ: L6 プレゼンテーション層", LayerPresColor)
        OsiLayer.L5_SESSION -> Pair("セ: L5 セッション層", LayerSessColor)
        OsiLayer.L4_TRANSPORT -> Pair("ト: L4 トランスポート層", LayerTransColor)
        OsiLayer.L3_NETWORK -> Pair("ネ: L3 ネットワーク層", LayerNetColor)
        OsiLayer.L2_DATALINK -> Pair("デ: L2 データリンク層", LayerDataColor)
        OsiLayer.L1_PHYSICAL -> Pair("ブ: L1 物理層", LayerPhysColor)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(color)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = shortName,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
