package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.BattlePhase
import com.example.cyberdefender.model.BattleState
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP風 バトル終了（評価・クレーム・ボーナス算定）ダイアログ
 */
@Composable
fun BattleResultDialog(
    state: BattleState,
    onRestart: () -> Unit,
    onNextStage: () -> Unit,
    onBackToStages: () -> Unit,
    hasNextStage: Boolean
) {
    if (state.phase == BattlePhase.IN_BATTLE) return

    val isVictory = state.phase == BattlePhase.VICTORY
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = {},
        containerColor = XpWindowBg,
        title = {
            Text(
                text = if (isVictory) "🏁 防衛作戦終了 - 業務査定結果" else "❌ サーバダウン - 重大インシデント発生",
                color = if (isVictory) Color(0xFF003399) else XpAlertRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 査定結果ランク & ボーナス表示
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .border(1.dp, XpBorderDark)
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("情シス評価ランク:", fontSize = 11.sp, color = XpTextDark)
                            Text(
                                text = state.resultRank,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.resultRank.startsWith("S") || state.resultRank.startsWith("A")) XpStartGreen else XpAlertRed
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("今期の想定賞与:", fontSize = 11.sp, color = XpTextDark)
                            Text(
                                text = state.bonusAmountText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF003399),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // 社員からのクレームまたは賞賛
                if (state.isOverkillUsed) {
                    XpBalloonNotification(
                        speaker = "営業部長 & 経理部長",
                        message = state.latestComplaint ?: "おい情シス！ネット切る奴があるか！契約も振込もパーだぞ！始末書書け！",
                        isAngry = true
                    )
                } else if (isVictory) {
                    XpBalloonNotification(
                        speaker = "社長 & 総務一同",
                        message = "業務を一切止めずに見事なピンポイント防衛だったな！さすが我が社の情シスだ、頼りにしてるよ！",
                        isAngry = false
                    )
                }

                // アプセトネデブ セキュリティ解説
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF7F6EE))
                        .border(1.dp, XpBorderShadow)
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "【アプセトネデブ解説ノート】",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF003399)
                        )
                        Text(
                            text = state.lastEducationalMessage ?: state.threat.educationalDetail,
                            fontSize = 10.sp,
                            color = XpTextDark,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isVictory && hasNextStage) {
                XpButton(text = "次の話へ進む ⏩", onClick = onNextStage, isPrimary = true)
            } else {
                XpButton(text = "もう一度防衛する 🔄", onClick = onRestart, isPrimary = true)
            }
        },
        dismissButton = {
            XpButton(text = "作戦一覧へ 📁", onClick = onBackToStages)
        }
    )
}
