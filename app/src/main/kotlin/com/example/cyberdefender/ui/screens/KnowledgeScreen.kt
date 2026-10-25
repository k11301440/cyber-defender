package com.example.cyberdefender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.cyberdefender.model.OsiKnowledge
import com.example.cyberdefender.model.StageCatalog
import com.example.cyberdefender.ui.components.*
import com.example.cyberdefender.ui.theme.*

@Composable
fun KnowledgeScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CrtMonitorFrame(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF004E98))
                .padding(4.dp)
        ) {
            XpWindow(
                title = "ヘルプとサポートセンター - アプセトネデブ早見表 & 試験対策",
                onCloseClick = onNavigateBack,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // アプセトネデブの由来解説
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFFFE1))
                            .border(1.dp, XpBalloonBorder)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "💡【おじさんの合言葉: アプセトネデブとは？】",
                                color = Color(0xFFB45309),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "OSI基本参照モデルの第7層〜第1層の頭文字を取った伝統の暗記語呂合わせ！\n" +
                                        "「ア」プリケーション層 (L7) → 「プ」レゼンテーション層 (L6) → 「セ」ッション層 (L5) → " +
                                        "「ト」ランスポート層 (L4) → 「ネ」ットワーク層 (L3) → 「デ」ータリンク層 (L2) → 「ブ」ツリ層 (L1)",
                                color = XpTextDark,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "各層のプロトコルと防護ハンドブック:",
                            color = XpTextDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        XpButton(
                            text = "作戦一覧へ戻る ↩️",
                            onClick = onNavigateBack
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(StageCatalog.osiKnowledges) { item ->
                            OsiKnowledgeCard(item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OsiKnowledgeCard(
    item: OsiKnowledge,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White)
            .border(1.dp, XpBorderShadow, RoundedCornerShape(3.dp))
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF003399))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "【${item.osiInitial}】 L${item.layerNumber}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = item.layerName,
                    color = XpTextDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "代表プロトコル: ${item.representativeProtocols}",
                color = Color(0xFF003399),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "典型的な脅威: ${item.typicalThreats}",
                color = XpAlertRed,
                fontSize = 10.sp
            )
            Text(
                text = "適切な防護策: ${item.realWorldDefense}",
                color = XpStartGreen,
                fontSize = 10.sp
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F4F8))
                    .padding(4.dp)
            ) {
                Text(
                    text = "試験・実務の要点: ${item.itExamTip}",
                    color = XpTextDark,
                    fontSize = 9.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
