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
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP タスクマネージャ風 サーバー健全度 & リソース監視パネル
 */
@Composable
fun ServerStatusPanel(
    serverHp: Int,
    maxHp: Int,
    shield: Int,
    maxShield: Int,
    ap: Int,
    maxAp: Int,
    businessImpact: Int,
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
        // パネルヘッダー
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📊 自社基幹サーバ・リソース管理",
                color = XpTextDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            // AP (メモリリソース)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "使用可能メモリ(AP):",
                    color = XpTextDark,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "$ap / $maxAp",
                    color = if (ap > 1) XpStartGreen else XpAlertRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // サーバーHP (緑のXPブロックプログレスバー)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "サーバ健全性 (Integrity HP):",
                    fontSize = 10.sp,
                    color = XpTextDark,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "$serverHp / $maxHp",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (serverHp > 40) XpStartGreen else XpAlertRed,
                    fontFamily = FontFamily.Monospace
                )
            }
            XpBlockProgressBar(current = serverHp, max = maxHp)
        }

        // 業務影響度メーター（過剰防衛すると急上昇！）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "社内業務影響度 (クレーム指数):",
                fontSize = 10.sp,
                color = if (businessImpact > 30) XpAlertRed else XpTextMuted,
                fontWeight = if (businessImpact > 30) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "$businessImpact% ${if (businessImpact >= 80) "【大炎上中】" else if (businessImpact > 0) "【小言あり】" else "【平穏無事】"}",
                fontSize = 10.sp,
                color = if (businessImpact > 30) XpAlertRed else XpStartGreen,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
