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

enum class UpgradeTab {
    FACILITY,
    COMMANDS
}

@Composable
fun UpgradeScreen(
    viewModel: BattleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.gameProgress.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(UpgradeTab.FACILITY) }

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
                title = "情報システム部 - 設備投資 & 防護コマンド調達コンソール",
                onCloseClick = onNavigateBack,
                menuItems = listOf(
                    "戻る(B)" to onNavigateBack
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 所持予算バルーン
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFFFFFBEB))
                            .border(1.dp, Color(0xFFD97706), RoundedCornerShape(3.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "[現在の部署IT予算]",
                                    color = Color(0xFF92400E),
                                    fontSize = 11.sp,
                                    fontFamily = PixelFontFamily
                                )
                                Text(
                                    text = "${progress.departmentBudget.toString().reversed().chunked(3).joinToString(",").reversed()} 円",
                                    color = Color(0xFFB45309),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PixelFontFamily
                                )
                            }

                            XpButton(
                                text = "作戦へ戻る [<-]",
                                onClick = onNavigateBack,
                                fontSize = 11,
                                minHeight = 38
                            )
                        }
                    }

                    // タブ切り替え（設備投資7段階 vs 追加コマンド調達）
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFD6D3C4), RoundedCornerShape(3.dp))
                            .border(1.dp, Color(0xFF807A6D), RoundedCornerShape(3.dp))
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TabButton(
                            text = "情シス設備投資 (7段階強化)",
                            isSelected = selectedTab == UpgradeTab.FACILITY,
                            onClick = { selectedTab = UpgradeTab.FACILITY },
                            modifier = Modifier.weight(1f)
                        )
                        TabButton(
                            text = "追加防護コマンド調達",
                            isSelected = selectedTab == UpgradeTab.COMMANDS,
                            onClick = { selectedTab = UpgradeTab.COMMANDS },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    when (selectedTab) {
                        UpgradeTab.FACILITY -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(FacilityType.values()) { facility ->
                                    val currentLvl = progress.getLevel(facility)
                                    val cost = progress.getUpgradeCost(facility)
                                    val isMax = currentLvl >= facility.maxLevel
                                    val canAfford = progress.departmentBudget >= cost && !isMax

                                    FacilityUpgradeCard(
                                        facility = facility,
                                        currentLevel = currentLvl,
                                        cost = cost,
                                        isMax = isMax,
                                        canAfford = canAfford,
                                        onUpgrade = { viewModel.purchaseFacilityUpgrade(facility) }
                                    )
                                }
                            }
                        }

                        UpgradeTab.COMMANDS -> {
                            val purchasableActions = DefenseActionCatalog.purchasableActions

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(purchasableActions) { action ->
                                    val isOwned = progress.unlockedActionIds.contains(action.id)
                                    val canAfford = progress.departmentBudget >= action.purchaseCost && !isOwned

                                    PurchasableActionCard(
                                        action = action,
                                        isOwned = isOwned,
                                        canAfford = canAfford,
                                        onBuy = { viewModel.purchaseActionCommand(action) }
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

/**
 * 7段階設備強化カード
 */
@Composable
fun FacilityUpgradeCard(
    facility: FacilityType,
    currentLevel: Int,
    cost: Int,
    isMax: Boolean,
    canAfford: Boolean,
    onUpgrade: () -> Unit
) {
    val bg = if (currentLevel > 0) Color.White else Color(0xFFF8FAFC)
    val border = if (currentLevel > 0) Color(0xFF003C74) else Color(0xFF94A3B8)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(3.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
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
                        text = facility.title,
                        color = Color(0xFF1E293B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Lv $currentLevel / ${facility.maxLevel}",
                        color = if (isMax) Color(0xFF15803D) else Color(0xFF003399),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }

                // 7段階レベルインジケータ (7つの四角形ブロック)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    for (i in 1..facility.maxLevel) {
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .height(6.dp)
                                .background(
                                    if (i <= currentLevel) Color(0xFF22C55E) else Color(0xFFCBD5E1),
                                    RoundedCornerShape(1.dp)
                                )
                                .border(1.dp, Color(0xFF64748B), RoundedCornerShape(1.dp))
                        )
                    }
                }
            }

            if (isMax) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF22C55E), RoundedCornerShape(2.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "[最大強化]",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
            } else {
                XpButton(
                    text = "Lv${currentLevel + 1}へ: ${cost.toString().reversed().chunked(3).joinToString(",").reversed()}円",
                    enabled = canAfford,
                    isPrimary = canAfford,
                    onClick = onUpgrade,
                    fontSize = 11,
                    minHeight = 44
                )
            }
        }

        Text(
            text = "効果: ${facility.shortDesc}",
            color = Color(0xFF0369A1),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily
        )

        Text(
            text = facility.detailedDesc,
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 14.sp
        )
    }
}

/**
 * 追加購入コマンドカード
 */
@Composable
fun PurchasableActionCard(
    action: DefenseAction,
    isOwned: Boolean,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    val bg = if (isOwned) Color(0xFFF0FDF4) else Color.White
    val border = if (isOwned) Color(0xFF16A34A) else Color(0xFF003C74)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(3.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${action.layer.shortName} : ${action.name}",
                    color = Color(0xFF1E293B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "威力: ${action.expectedDamage} / シールド: +${action.expectedShield}",
                    color = Color(0xFFB45309),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
            }

            if (isOwned) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF22C55E), RoundedCornerShape(2.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "[導入済]",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
            } else {
                XpButton(
                    text = "${action.purchaseCost.toString().reversed().chunked(3).joinToString(",").reversed()}円で調達",
                    enabled = canAfford,
                    isPrimary = canAfford,
                    onClick = onBuy,
                    fontSize = 11,
                    minHeight = 44
                )
            }
        }

        Text(
            text = action.description,
            color = Color(0xFF475569),
            fontSize = 10.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 14.sp
        )
    }
}
