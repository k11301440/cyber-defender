package com.example.cyberdefender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cyberdefender.model.UpgradeType
import com.example.cyberdefender.ui.components.*
import com.example.cyberdefender.ui.theme.*
import com.example.cyberdefender.viewmodel.BattleViewModel

@Composable
fun UpgradeScreen(
    viewModel: BattleViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                title = "情報システム部 - 設備投資 & 強化コンソール",
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "💰", fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = "現在の部署IT予算（ボーナス原資）",
                                        color = Color(0xFF92400E),
                                        fontSize = 10.sp,
                                        fontFamily = PixelFontFamily
                                    )
                                    Text(
                                        text = "¥${progress.departmentBudget.toString().reversed().chunked(3).joinToString(",").reversed()}",
                                        color = Color(0xFFB45309),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = PixelFontFamily
                                    )
                                }
                            }

                            XpButton(
                                text = "作戦へ戻る ↩️",
                                onClick = onNavigateBack,
                                fontSize = 11
                            )
                        }
                    }

                    Text(
                        text = "🏢 導入可能な情シス設備・強化プラン一覧:",
                        color = XpTextDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )

                    // アップグレード一覧
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(UpgradeType.values()) { upgrade ->
                            val isPurchased = progress.purchasedUpgrades.contains(upgrade)
                            val canAfford = progress.departmentBudget >= upgrade.cost

                            UpgradeCard(
                                upgrade = upgrade,
                                isPurchased = isPurchased,
                                canAfford = canAfford,
                                onBuy = { viewModel.purchaseUpgrade(upgrade) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpgradeCard(
    upgrade: UpgradeType,
    isPurchased: Boolean,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    val bg = if (isPurchased) Color(0xFFF0FDF4) else Color.White
    val border = if (isPurchased) Color(0xFF16A34A) else Color(0xFFCBD5E1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(3.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
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
                Text(text = upgrade.icon, fontSize = 14.sp)
                Text(
                    text = upgrade.title,
                    color = if (isPurchased) Color(0xFF15803D) else Color(0xFF1E293B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isPurchased) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF22C55E), RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "✅ 導入完了",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
            } else {
                XpButton(
                    text = "¥${upgrade.cost.toString().reversed().chunked(3).joinToString(",").reversed()} で導入",
                    enabled = canAfford,
                    isPrimary = canAfford,
                    onClick = onBuy,
                    fontSize = 10
                )
            }
        }

        Text(
            text = "効果: ${upgrade.shortDesc}",
            color = Color(0xFF0369A1),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PixelFontFamily
        )

        Text(
            text = upgrade.detailedDesc,
            color = Color(0xFF64748B),
            fontSize = 10.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 14.sp
        )
    }
}
