package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.Threat
import com.example.cyberdefender.model.ThreatAttack
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP風 侵入脅威モニタ & 通報パネル
 */
@Composable
fun ThreatCard(
    threat: Threat,
    currentAttack: ThreatAttack,
    isStunned: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XpWindowBg)
            .border(1.dp, XpBorderShadow)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // ヘッダー: 敵の名前 & 所属するOSI 7層バッジ
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
                    text = "🚨 " + threat.name,
                    color = XpAlertRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                OsiLayerBadge(layer = threat.layer)
            }

            if (isStunned) {
                Text(
                    text = "【通信遮断中】",
                    color = XpStartGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 敵のアスキーアート & 敵HPバー
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF001122))
                    .border(1.dp, XpBorderDark)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = threat.avatarAscii,
                    color = if (isStunned) Color(0xFF66FF66) else Color(0xFFFF5555),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "脅威活動規模 (HP):",
                        fontSize = 10.sp,
                        color = XpTextDark
                    )
                    Text(
                        text = "${threat.currentHp} / ${threat.maxHp}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = XpAlertRed,
                        fontFamily = FontFamily.Monospace
                    )
                }
                XpBlockProgressBar(current = threat.currentHp, max = threat.maxHp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = threat.description,
                    color = XpTextMuted,
                    fontSize = 9.sp,
                    lineHeight = 12.sp,
                    maxLines = 2
                )
            }
        }

        // 次回攻撃予告パネル
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isStunned) Color(0xFFE6F4EA) else Color(0xFFFCE8E6))
                .border(1.dp, if (isStunned) XpStartGreen else XpAlertRed)
                .padding(4.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isStunned) "【次回行動: 攻撃スキップ】" else "【攻撃予兆: ${currentAttack.name}】",
                        color = if (isStunned) XpStartGreen else XpAlertRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    if (!isStunned) {
                        Text(
                            text = if (currentAttack.piercesShield) "直接 ${currentAttack.damage} 被害" else "威力 ${currentAttack.damage}",
                            color = XpAlertRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Text(
                    text = if (isStunned) "適正層の防護により、敵の攻撃シーケンスは無効化されています。" else currentAttack.warningMessage,
                    color = XpTextDark,
                    fontSize = 9.sp,
                    lineHeight = 12.sp
                )
            }
        }
    }
}
