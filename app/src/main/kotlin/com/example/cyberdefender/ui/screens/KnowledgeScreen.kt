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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cyberdefender.model.*
import com.example.cyberdefender.ui.components.*
import com.example.cyberdefender.ui.theme.*
import com.example.cyberdefender.viewmodel.BattleViewModel

enum class KnowledgeTab {
    OSI_GUIDE,
    INCIDENT_CATALOG_100
}

@Composable
fun KnowledgeScreen(
    viewModel: BattleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(KnowledgeTab.OSI_GUIDE) }
    var selectedLayerFilter by remember { mutableStateOf<OsiLayer?>(null) }
    val progress by viewModel.gameProgress.collectAsStateWithLifecycle()

    CrtMonitorFrame(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(XpDesktopBlue)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            XpWindow(
                title = "ヘルプとサポートセンター - OSI参照モデル & サイバー攻撃100種暗記図鑑",
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
                    // タブ切り替え（早見表 vs 100種図鑑）
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD6D3C4), RoundedCornerShape(3.dp))
                            .border(1.dp, Color(0xFF807A6D), RoundedCornerShape(3.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TabButton(
                            text = "アプセトネデブ早見表",
                            isSelected = selectedTab == KnowledgeTab.OSI_GUIDE,
                            onClick = { selectedTab = KnowledgeTab.OSI_GUIDE },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "サイバー攻撃100種図鑑 (全100件)",
                            isSelected = selectedTab == KnowledgeTab.INCIDENT_CATALOG_100,
                            onClick = { selectedTab = KnowledgeTab.INCIDENT_CATALOG_100 },
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
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "[伝統の合言葉: アプセトネデブとは？]",
                                        color = Color(0xFFB45309),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = PixelFontFamily
                                    )
                                    Text(
                                        text = "OSI基本参照モデルの第7層〜第1層の頭文字を取った伝統の暗記語呂合わせ。\n" +
                                                "「ア」プリケーション(L7) -> 「プ」レゼンテーション(L6) -> 「セ」ッション(L5) -> " +
                                                "「ト」ランスポート(L4) -> 「ネ」ットワーク(L3) -> 「デ」ータリンク(L2) -> 「ブ」ツリ(L1)",
                                        color = XpTextDark,
                                        fontSize = 11.sp,
                                        fontFamily = PixelFontFamily,
                                        lineHeight = 16.sp
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

                        KnowledgeTab.INCIDENT_CATALOG_100 -> {
                            // 層別フィルタボタン
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilterChip(
                                    label = "全層",
                                    isSelected = selectedLayerFilter == null,
                                    onClick = { selectedLayerFilter = null },
                                    modifier = Modifier.weight(1f)
                                )
                                OsiLayer.values().forEach { layer ->
                                    FilterChip(
                                        label = layer.code,
                                        isSelected = selectedLayerFilter == layer,
                                        onClick = { selectedLayerFilter = layer },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            val filteredIncidents = if (selectedLayerFilter == null) {
                                IncidentCatalog.allIncidents
                            } else {
                                IncidentCatalog.allIncidents.filter { it.layer == selectedLayerFilter }
                            }

                            Text(
                                text = "表示中: ${filteredIncidents.size} 件 / 被弾記録済: ${progress.incidentLogBook.size} 件",
                                color = Color(0xFF1E3A8A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PixelFontFamily
                            )

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(filteredIncidents) { incident ->
                                    val isHitExperienced = progress.incidentLogBook.any { it.incidentId == incident.id }
                                    IncidentMasterCard(
                                        incident = incident,
                                        isExperienced = isHitExperienced
                                    )
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
fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isSelected) Color(0xFF003C74) else Color(0xFFEDE9D8)
    val text = if (isSelected) Color.White else Color(0xFF1E293B)

    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(bg)
            .border(1.dp, Color(0xFF003C74), RoundedCornerShape(2.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily
        )
    }
}

@Composable
fun IncidentMasterCard(
    incident: IncidentMaster,
    isExperienced: Boolean,
    modifier: Modifier = Modifier
) {
    val effectiveAction = DefenseActionCatalog.allActions.find { it.id == incident.effectiveActionId }
    val tabooAction = DefenseActionCatalog.allActions.find { it.id == incident.tabooActionId }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFF003C74), RoundedCornerShape(3.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // ヘッダー: No. & 攻撃名 & 層バッジ & 被弾マーク
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
                        .background(Color(0xFF003C74), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "No.${incident.id} [${incident.layer.code}:L${incident.layer.layerNumber}]",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }

                Text(
                    text = incident.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF991B1B),
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isExperienced) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF22C55E), RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "[被弾ログあり]",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
            }
        }

        Text(
            text = "検知器材: ${incident.deviceSource} / 威力: ${incident.damage}",
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontFamily = PixelFontFamily
        )

        // 【有効な対策】
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0FDF4), RoundedCornerShape(2.dp))
                .border(1.dp, Color(0xFF16A34A), RoundedCornerShape(2.dp))
                .padding(6.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "【有効な対策】: ${effectiveAction?.name ?: "適正防護コマンド"}",
                    color = Color(0xFF15803D),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
                Text(
                    text = "理由: ${incident.effectiveReason}",
                    color = Color(0xFF1E293B),
                    fontSize = 10.sp,
                    fontFamily = PixelFontFamily,
                    lineHeight = 14.sp
                )
            }
        }

        // 【禁忌肢（絶対禁止）】 (ある場合のみ表示)
        if (tabooAction != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEF2F2), RoundedCornerShape(2.dp))
                    .border(1.dp, Color(0xFFDC2626), RoundedCornerShape(2.dp))
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "※【禁忌肢 (選んだら即負け)】: ${tabooAction.name}",
                        color = Color(0xFFB91C1C),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                    Text(
                        text = "即死理由: ${tabooAction.tabooGameOverMessage}",
                        color = Color(0xFF7F1D1D),
                        fontSize = 10.sp,
                        fontFamily = PixelFontFamily,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // 【試験・実務暗記Tips】
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEFF6FF))
                .padding(6.dp)
        ) {
            Text(
                text = "試験Tips: ${incident.examTip}",
                fontSize = 10.sp,
                color = Color(0xFF1E40AF),
                fontFamily = PixelFontFamily,
                lineHeight = 14.sp
            )
        }
    }
}
