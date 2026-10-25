package com.example.cyberdefender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cyberdefender.model.IncidentLogEntry
import com.example.cyberdefender.model.OsiKnowledge
import com.example.cyberdefender.model.StageCatalog
import com.example.cyberdefender.ui.components.*
import com.example.cyberdefender.ui.theme.*
import com.example.cyberdefender.viewmodel.BattleViewModel

enum class KnowledgeTab {
    OSI_GUIDE,
    INCIDENT_LOGS
}

@Composable
fun KnowledgeScreen(
    viewModel: BattleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(KnowledgeTab.OSI_GUIDE) }
    val progress by viewModel.gameProgress.collectAsStateWithLifecycle()

    CrtMonitorFrame(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF004E98))
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            XpWindow(
                title = "ヘルプとサポートセンター - OSI知識 & インシデントログ図鑑",
                onCloseClick = onNavigateBack,
                menuItems = listOf(
                    "作戦へ戻る(B)" to onNavigateBack
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // タブ切り替え（早見表 vs インシデント図鑑ログ）
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD6D3C4), RoundedCornerShape(3.dp))
                            .border(1.dp, Color(0xFF807A6D), RoundedCornerShape(3.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TabButton(
                            text = "📖 アプセトネデブ早見表",
                            isSelected = selectedTab == KnowledgeTab.OSI_GUIDE,
                            onClick = { selectedTab = KnowledgeTab.OSI_GUIDE },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "📋 被弾インシデント図鑑 (${progress.incidentLogBook.size}件)",
                            isSelected = selectedTab == KnowledgeTab.INCIDENT_LOGS,
                            onClick = { selectedTab = KnowledgeTab.INCIDENT_LOGS },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    when (selectedTab) {
                        KnowledgeTab.OSI_GUIDE -> {
                            // 由来解説
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFFFFE1))
                                    .border(1.dp, XpBalloonBorder)
                                    .padding(6.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "💡【伝統の合言葉: アプセトネデブとは？】",
                                        color = Color(0xFFB45309),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = PixelFontFamily
                                    )
                                    Text(
                                        text = "OSI基本参照モデルの第7層〜第1層の頭文字を取った伝統の暗記語呂合わせ！\n" +
                                                "「ア」プリ(L7) → 「プ」レゼン(L6) → 「セ」ッション(L5) → " +
                                                "「ト」ランス(L4) → 「ネ」ット(L3) → 「デ」ータリンク(L2) → 「ブ」ツリ(L1)",
                                        color = XpTextDark,
                                        fontSize = 10.sp,
                                        fontFamily = PixelFontFamily,
                                        lineHeight = 14.sp
                                    )
                                }
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

                        KnowledgeTab.INCIDENT_LOGS -> {
                            if (progress.incidentLogBook.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .background(Color.White)
                                        .border(1.dp, XpBorderDark)
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = "📭", fontSize = 28.sp)
                                        Text(
                                            text = "登録されたインシデントログはありません",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = PixelFontFamily,
                                            color = XpTextDark
                                        )
                                        Text(
                                            text = "バトル中に敵の攻撃を食らうと、攻撃の手口や防御の原則が自動でこの図鑑に記録されます！",
                                            fontSize = 10.sp,
                                            fontFamily = PixelFontFamily,
                                            color = Color(0xFF64748B),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    items(progress.incidentLogBook) { entry ->
                                        IncidentLogCard(entry = entry)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

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
            .clip(RoundedCornerShape(2.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(2.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF003C74) else Color(0xFF475569),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily
        )
    }
}

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
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFF003399), RoundedCornerShape(2.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.osiInitial,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
                Text(
                    text = "第${item.layerNumber}層: ${item.layerName}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF003399),
                    fontFamily = PixelFontFamily
                )
            }
        }

        Text(
            text = "📡 代表プロトコル: ${item.representativeProtocols}",
            fontSize = 10.sp,
            color = Color(0xFF1E3A8A),
            fontFamily = PixelFontFamily
        )
        Text(
            text = "⚡ 代表的な脅威: ${item.typicalThreats}",
            fontSize = 10.sp,
            color = Color(0xFF991B1B),
            fontFamily = PixelFontFamily
        )
        Text(
            text = "🛡️ 現実の防護策: ${item.realWorldDefense}",
            fontSize = 10.sp,
            color = Color(0xFF166534),
            fontFamily = PixelFontFamily,
            lineHeight = 14.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFEF3C7))
                .padding(4.dp)
        ) {
            Text(
                text = "📝 試験Tips: ${item.itExamTip}",
                fontSize = 9.sp,
                color = Color(0xFF92400E),
                fontFamily = PixelFontFamily,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun IncidentLogCard(
    entry: IncidentLogEntry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFDC2626), RoundedCornerShape(3.dp))
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFDC2626), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "【${entry.layer.code}: L${entry.layer.layerNumber}】",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
                Text(
                    text = entry.attackName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B),
                    fontFamily = PixelFontFamily
                )
            }

            Text(
                text = "威力: ${entry.damage}",
                color = Color(0xFFB45309),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PixelFontFamily
            )
        }

        Text(
            text = "侵入元: ${entry.threatName}",
            color = Color(0xFF64748B),
            fontSize = 9.sp,
            fontFamily = PixelFontFamily
        )

        Text(
            text = "🔍 攻撃の仕組み:\n${entry.mechanism}",
            color = Color(0xFF1E293B),
            fontSize = 10.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 14.sp
        )

        Text(
            text = "🛡️ 防御の原則:\n${entry.defensePrinciple}",
            color = Color(0xFF15803D),
            fontSize = 10.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 14.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF6FF))
                .padding(4.dp)
        ) {
            Text(
                text = "🎓 試験・実務ポイント:\n${entry.examTip}",
                fontSize = 9.sp,
                color = Color(0xFF1E40AF),
                fontFamily = PixelFontFamily,
                lineHeight = 13.sp
            )
        }
    }
}
