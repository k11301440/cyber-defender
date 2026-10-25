package com.example.cyberdefender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.Stage
import com.example.cyberdefender.model.StageCatalog
import com.example.cyberdefender.ui.components.*
import com.example.cyberdefender.ui.theme.*

@Composable
fun StageSelectScreen(
    onSelectStage: (Int) -> Unit,
    onOpenKnowledge: () -> Unit,
    modifier: Modifier = Modifier
) {
    CrtMonitorFrame(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF004E98)) // Windows XPのデスクトップ背景色
                .padding(4.dp)
        ) {
            XpWindow(
                title = "帝都物産 情報システム部 - アプセトネデブ防衛作戦一覧",
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 情シス新人への訓示バルーン
                    XpBalloonNotification(
                        speaker = "情シス課長 (内線: 101)",
                        message = "「おい新人！我が社のサーバを狙って、アプセトネデブの各層からクラッカーどもが攻めてきてるぞ！過剰防衛して全社回線を引っこ抜くと営業から怒鳴られるからな！適切な層を見極めてピンポイントで防護しろ！」",
                        isAngry = false
                    )

                    // アプセトネデブ解説ボタン
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📁 社内インシデント案件一覧 (全7話):",
                            color = XpTextDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )

                        XpButton(
                            text = "📖 アプセトネデブ早見表を開く",
                            onClick = onOpenKnowledge
                        )
                    }

                    // ステージリスト
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(StageCatalog.stages) { stage ->
                            StageCard(
                                stage = stage,
                                onClick = { onSelectStage(stage.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StageCard(
    stage: Stage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White)
            .border(1.dp, XpBorderShadow, RoundedCornerShape(3.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stage.title,
                        color = XpTextDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    OsiLayerBadge(layer = stage.layer)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stage.subtitle,
                    color = XpTextMuted,
                    fontSize = 10.sp
                )
            }

            XpButton(
                text = "出動 📞",
                onClick = onClick,
                isPrimary = true
            )
        }
    }
}
