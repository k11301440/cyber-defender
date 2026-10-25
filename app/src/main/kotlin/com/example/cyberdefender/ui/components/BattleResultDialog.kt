package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.BattlePhase
import com.example.cyberdefender.model.BattleState
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP クラシックメッセージボックス形式の作戦終了・査定結果ダイアログ
 * (Android Material3を完全排除、XPウィンドウによるダイアログ。絵文字完全排除)
 */
@Composable
fun BattleResultDialog(
    state: BattleState,
    onRestart: () -> Unit,
    onNextStage: () -> Unit,
    onBackToStages: () -> Unit,
    onOpenUpgrades: () -> Unit,
    hasNextStage: Boolean
) {
    if (state.phase == BattlePhase.IN_BATTLE) return

    val isVictory = state.phase == BattlePhase.VICTORY
    val isTaboo = state.phase == BattlePhase.TABOO_GAME_OVER
    val scrollState = rememberScrollState()

    val dialogTitle = when {
        isTaboo -> "重大インシデント警報 - [禁忌肢実行による懲戒処分]"
        isVictory -> "防衛作戦成功 - [情シス人事査定通知]"
        else -> "サーバダウン - [重大障害発生・始末書提出]"
    }

    XpClassicDialog(
        title = dialogTitle,
        onDismissRequest = onBackToStages
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 査定結果ランク & 予算支給 (XP白地テーブル)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.dp, XpBorderDark)
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("情シス評価ランク:", fontSize = 12.sp, color = XpTextDark, fontFamily = PixelFontFamily)
                        Text(
                            text = state.resultRank,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVictory) Color(0xFF15803D) else Color(0xFFDC2626),
                            fontFamily = PixelFontFamily
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("獲得部署IT予算:", fontSize = 12.sp, color = XpTextDark, fontFamily = PixelFontFamily)
                        Text(
                            text = "+ ${state.rewardBudgetAmount.toString().reversed().chunked(3).joinToString(",").reversed()} 円",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF003399),
                            fontFamily = PixelFontFamily
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("想定賞与査定額:", fontSize = 12.sp, color = XpTextDark, fontFamily = PixelFontFamily)
                        Text(
                            text = state.bonusAmountText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF003399),
                            fontFamily = PixelFontFamily
                        )
                    }
                }
            }

            // 社員からのクレームまたは賞賛
            if (isTaboo) {
                XpBalloonNotification(
                    speaker = "人事部長 & 顧問弁護士",
                    message = state.tabooGameOverMessage ?: "「なんということをしてくれたんだ！全社データが吹き飛んだぞ！即刻懲戒解雇だ！」",
                    isAngry = true
                )
            } else if (state.isOverkillUsed) {
                XpBalloonNotification(
                    speaker = "営業部長 & 経理部長",
                    message = "「おい情シス！回線引っこ抜く奴があるか！契約も振込もパーだぞ！始末書書け！」",
                    isAngry = true
                )
            } else if (isVictory) {
                XpBalloonNotification(
                    speaker = "社長 & 総務一同",
                    message = "「業務を一切止めずに見事なピンポイント防衛だったな！獲得した予算で部署のセキュリティ設備を増強してくれ！」",
                    isAngry = false
                )
            } else {
                XpBalloonNotification(
                    speaker = "情シス課長",
                    message = "「サーバが落ちてしまったな…敵の攻撃手口に対する適正な層のコマンドを見極めないと効果が半減するぞ。もう一度作戦を練り直せ！」",
                    isAngry = true
                )
            }

            // アプセトネデブ 防衛ノウハウノート
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7F6EE))
                    .border(1.dp, XpBorderShadow)
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "[アプセトネデブ防衛ノウハウ]",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003399),
                        fontFamily = PixelFontFamily
                    )
                    Text(
                        text = state.threat.educationalDetail,
                        fontSize = 11.sp,
                        color = XpTextDark,
                        lineHeight = 16.sp,
                        fontFamily = PixelFontFamily
                    )
                }
            }

            // 操作ボタン一覧 (操作域拡大・XP立体ボタン)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isVictory && hasNextStage) {
                    XpButton(
                        text = "次の話へ進む [>>]",
                        onClick = onNextStage,
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        minHeight = 44
                    )
                } else {
                    XpButton(
                        text = "もう一度防衛 [<-]",
                        onClick = onRestart,
                        isPrimary = true,
                        modifier = Modifier.weight(1f),
                        minHeight = 44
                    )
                }

                if (isVictory) {
                    XpButton(
                        text = "設備投資へ",
                        onClick = onOpenUpgrades,
                        modifier = Modifier.weight(1f),
                        minHeight = 44
                    )
                }

                XpButton(
                    text = "作戦一覧へ",
                    onClick = onBackToStages,
                    modifier = Modifier.weight(1f),
                    minHeight = 44
                )
            }
        }
    }
}
