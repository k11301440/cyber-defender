package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.Threat
import com.example.cyberdefender.ui.theme.*

/**
 * プレイ画面の最上部に配置するスリムなステータスダッシュボード (絵文字完全排除)
 */
@Composable
fun BattleStatusBar(
    threat: Threat,
    serverHp: Int,
    maxServerHp: Int,
    shield: Int,
    maxShield: Int,
    isStunned: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(XpWindowBg)
            .border(1.dp, Color(0xFF003C74), RoundedCornerShape(3.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 上段: 敵情報 & 敵HP
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF991B1B), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "[脅威]",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }

                Text(
                    text = "${threat.name} [${threat.layer.code}:L${threat.layer.layerNumber}]",
                    color = Color(0xFF991B1B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isStunned) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFD97706), RoundedCornerShape(2.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "[スタン中]",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PixelFontFamily
                        )
                    }
                }
            }

            Text(
                text = "HP: ${threat.currentHp} / ${threat.maxHp}",
                color = Color(0xFF991B1B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PixelFontFamily
            )
        }

        // 敵HPバー
        CompactProgressBar(
            current = threat.currentHp,
            max = threat.maxHp,
            barColor = Color(0xFFDC2626)
        )

        // 下段: 自社サーバHP & シールド
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "[自社サーバHP]",
                    color = Color(0xFF1E3A8A),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
                Text(
                    text = "$serverHp / $maxServerHp",
                    color = if (serverHp > 30) Color(0xFF15803D) else Color(0xFFDC2626),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "[シールド]",
                    color = Color(0xFF1D4ED8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
                Text(
                    text = "$shield / $maxShield",
                    color = Color(0xFF2563EB),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
            }
        }

        // 自社HPバー
        CompactProgressBar(
            current = serverHp,
            max = maxServerHp,
            barColor = if (serverHp > 30) Color(0xFF16A34A) else Color(0xFFDC2626)
        )
    }
}

@Composable
fun CompactProgressBar(
    current: Int,
    max: Int,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val fraction = (current.toFloat() / max.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFFCBD5E1), RoundedCornerShape(2.dp))
            .border(1.dp, Color(0xFF64748B), RoundedCornerShape(2.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction)
                .background(barColor, RoundedCornerShape(2.dp))
        )
    }
}
