package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.*
import com.example.cyberdefender.ui.theme.*

/**
 * 2ステップ防護コンソール (絵文字完全排除・操作域拡大)
 * Step 1: まずアプセトネデブの層（+最終手段）を選択
 * Step 2: 選択した層に対応する防護コマンドを選択して実行
 */
@Composable
fun TwoStepActionSelector(
    selectedLayer: OsiLayer?,
    isOverkillOpen: Boolean,
    unlockedLayers: Set<OsiLayer>,
    unlockedActionIds: Set<ActionId>,
    remainingActions: Int,
    maxActions: Int,
    remainingTimeSeconds: Int?,
    onSelectLayer: (OsiLayer) -> Unit,
    onOpenOverkill: () -> Unit,
    onBackToLayerSelect: () -> Unit,
    onExecuteAction: (DefenseAction) -> Unit,
    onEndTurn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFFEDE9D8))
            .border(2.dp, Color(0xFF003C74), RoundedCornerShape(3.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // コンソールヘッダー: 残り手数・タイマー・ターン終了ボタン
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF1E3A8A))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "残り行動: $remainingActions / $maxActions 手",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily,
                        maxLines = 1
                    )
                }

                if (remainingTimeSeconds != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (remainingTimeSeconds <= 20) Color(0xFFDC2626) else Color(0xFFD97706))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "残り時間: ${remainingTimeSeconds}秒",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PixelFontFamily,
                            maxLines = 1
                        )
                    }
                }
            }

            XpButton(
                text = "ターン終了 [>>]",
                onClick = onEndTurn,
                isPrimary = true,
                fontSize = 12,
                minHeight = 44
            )
        }

        // === Step 1: 層の選択（7層 + 最終手段） ===
        if (selectedLayer == null && !isOverkillOpen) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "[Step 1] 迎撃するアプセトネデブの層を選択してください:",
                    color = XpTextDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1
                )

                val layersInOrder = listOf(
                    OsiLayer.L7_APPLICATION,
                    OsiLayer.L6_PRESENTATION,
                    OsiLayer.L5_SESSION,
                    OsiLayer.L4_TRANSPORT,
                    OsiLayer.L3_NETWORK,
                    OsiLayer.L2_DATALINK,
                    OsiLayer.L1_PHYSICAL
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(layersInOrder) { layer ->
                        val isUnlocked = unlockedLayers.contains(layer)
                        LayerSelectBtn(
                            layer = layer,
                            isUnlocked = isUnlocked,
                            onClick = { if (isUnlocked) onSelectLayer(layer) }
                        )
                    }

                    item {
                        OverkillSelectBtn(onClick = onOpenOverkill)
                    }
                }
            }
        }

        // === Step 2: 選択した層のコマンド一覧 ===
        if (selectedLayer != null) {
            val actions = DefenseActionCatalog.getActionsForLayer(selectedLayer, unlockedActionIds)

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "[Step 2] ${selectedLayer.shortName} の防護コマンド:",
                        color = Color(0xFF003C74),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    XpButton(
                        text = "層を選び直す [<-]",
                        onClick = onBackToLayerSelect,
                        fontSize = 11,
                        minHeight = 38
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    actions.forEach { action ->
                        ActionCommandCard(
                            action = action,
                            canAfford = remainingActions > 0,
                            onClick = { onExecuteAction(action) }
                        )
                    }
                }
            }
        }

        // === Step 2 (別枠): 最終手段（過剰防衛）メニュー ===
        if (isOverkillOpen) {
            val overkillActions = DefenseActionCatalog.overkillActions

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "[警告] 最終手段（過剰防衛措置）:",
                        color = Color(0xFFB91C1C),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily,
                        maxLines = 1
                    )

                    XpButton(
                        text = "通常防護に戻る [<-]",
                        onClick = onBackToLayerSelect,
                        fontSize = 11,
                        minHeight = 38
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    overkillActions.forEach { action ->
                        OverkillCommandCard(
                            action = action,
                            canAfford = remainingActions > 0,
                            onClick = { onExecuteAction(action) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LayerSelectBtn(
    layer: OsiLayer,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val bgBrush = if (isUnlocked) {
        Brush.verticalGradient(
            listOf(Color(0xFFFFFFFF), Color(0xFFF0EDE0), Color(0xFFDBD5C0))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
        )
    }

    Box(
        modifier = Modifier
            .height(48.dp) // 操作域拡大
            .clip(RoundedCornerShape(3.dp))
            .background(bgBrush)
            .border(
                1.dp,
                if (isUnlocked) Color(0xFF003C74) else Color(0xFF94A3B8),
                RoundedCornerShape(3.dp)
            )
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isUnlocked) {
                Text(
                    text = "[${layer.code}] L${layer.layerNumber}",
                    color = Color(0xFF003C74),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1
                )
                Text(
                    text = layer.japaneseLabel.take(4),
                    color = Color(0xFF475569),
                    fontSize = 10.sp,
                    fontFamily = PixelFontFamily,
                    maxLines = 1
                )
            } else {
                Text(
                    text = "[x] L${layer.layerNumber}",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1
                )
                Text(
                    text = "未開放",
                    color = Color(0xFF94A3B8),
                    fontSize = 9.sp,
                    fontFamily = PixelFontFamily,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun OverkillSelectBtn(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFEE2E2), Color(0xFFFECACA), Color(0xFFFCA5A5))
                )
            )
            .border(1.dp, Color(0xFFB91C1C), RoundedCornerShape(3.dp))
            .clickable(onClick = onClick)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "[!] 最終手段",
                color = Color(0xFFB91C1C),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PixelFontFamily,
                maxLines = 1
            )
            Text(
                text = "過剰防衛",
                color = Color(0xFF991B1B),
                fontSize = 9.sp,
                fontFamily = PixelFontFamily,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ActionCommandCard(
    action: DefenseAction,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    val bgBrush = if (canAfford) {
        Brush.verticalGradient(
            listOf(Color(0xFFFFFFFF), Color(0xFFF4F1E6), Color(0xFFE2DDD0))
        )
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0)))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp) // 操作域拡大
            .clip(RoundedCornerShape(3.dp))
            .background(bgBrush)
            .border(1.dp, Color(0xFF003C74), RoundedCornerShape(3.dp))
            .clickable(enabled = canAfford, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = action.name,
                    color = if (canAfford) Color(0xFF003C74) else Color(0xFF64748B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (action.expectedDamage > 0) {
                    Text(
                        text = "威力:${action.expectedDamage}(期待値)",
                        color = Color(0xFFB45309),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
                if (action.expectedShield > 0) {
                    Text(
                        text = "シールド:+${action.expectedShield}",
                        color = Color(0xFF1D4ED8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
                if (action.healAmount > 0) {
                    Text(
                        text = "回復:+${action.healAmount}",
                        color = Color(0xFF15803D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }
            }
            Text(
                text = action.description,
                color = Color(0xFF475569),
                fontSize = 10.sp,
                fontFamily = PixelFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF2563EB))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "[実行]",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PixelFontFamily
            )
        }
    }
}

@Composable
fun OverkillCommandCard(
    action: DefenseAction,
    canAfford: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF1F2), Color(0xFFFFE4E6), Color(0xFFFECDD3))
                )
            )
            .border(1.dp, Color(0xFFBE123C), RoundedCornerShape(3.dp))
            .clickable(enabled = canAfford, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = action.name,
                color = Color(0xFF9F1239),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PixelFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "※注意: 敵は一撃死しますが、全社業務が即死し査定が全額没収されます。",
                color = Color(0xFFE11D48),
                fontSize = 10.sp,
                fontFamily = PixelFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFE11D48))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "[強行]",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PixelFontFamily
            )
        }
    }
}
