package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.DefenseAction
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP風 防御コマンド選択コンソール
 */
@Composable
fun ActionSelector(
    actions: List<DefenseAction>,
    currentAp: Int,
    onActionClick: (DefenseAction) -> Unit,
    onEndTurnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedActionForDetail by remember { mutableStateOf<DefenseAction?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XpWindowBg)
            .border(1.dp, XpBorderShadow)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🛡️ アプセトネデブ防護コマンド一覧:",
                color = XpTextDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            XpButton(
                text = "ターン終了 (次へ) ⏩",
                onClick = onEndTurnClick,
                isPrimary = true
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.heightIn(max = 175.dp)
        ) {
            items(actions) { action ->
                val canAfford = currentAp >= action.apCost
                ActionItemCard(
                    action = action,
                    canAfford = canAfford,
                    onClick = { if (canAfford) onActionClick(action) },
                    onInfoClick = { selectedActionForDetail = action }
                )
            }
        }
    }

    selectedActionForDetail?.let { action ->
        ActionDetailDialog(
            action = action,
            onDismiss = { selectedActionForDetail = null }
        )
    }
}

@Composable
fun ActionItemCard(
    action: DefenseAction,
    canAfford: Boolean,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOverkill = action.isOverkill

    val bgBrush = if (canAfford) {
        if (isOverkill) {
            Brush.verticalGradient(listOf(Color(0xFFFFEEEE), Color(0xFFFFD5D5)))
        } else {
            Brush.verticalGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF0EEE1)))
        }
    } else {
        Brush.verticalGradient(listOf(Color(0xFFE5E5E5), Color(0xFFD5D5D5)))
    }

    val borderColor = if (isOverkill) XpAlertRed else if (canAfford) Color(0xFF003C74) else XpBorderShadow

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(bgBrush)
            .border(1.dp, borderColor, RoundedCornerShape(3.dp))
            .clickable(enabled = canAfford, onClick = onClick)
            .padding(4.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = action.name,
                    color = if (isOverkill) XpAlertRed else if (canAfford) XpTextDark else XpTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${action.apCost}AP",
                    color = if (canAfford) Color(0xFFB45309) else XpTextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }

            Text(
                text = if (isOverkill) "【過剰防衛: 業務影響100%】" else action.commandCode,
                color = if (isOverkill) XpAlertRed else Color(0xFF003399),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = action.description,
                    color = XpTextMuted,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "[解説]",
                    color = XpLinkBlue,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable(onClick = onInfoClick)
                        .padding(start = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ActionDetailDialog(
    action: DefenseAction,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = XpWindowBg,
        title = {
            Column {
                Text(
                    text = action.name,
                    color = if (action.isOverkill) XpAlertRed else XpTextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "対応層: ${action.layer.code} (${action.layer.japaneseLabel})",
                    color = XpLinkBlue,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "【効果】\n${action.description}",
                    fontSize = 11.sp,
                    color = XpTextDark
                )
                if (action.isOverkill) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFEEEE))
                            .border(1.dp, XpAlertRed)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "⚠️【過剰防衛の警告】\nこの措置をとると敵は一瞬で撃退できますが、全社業務が麻痺します！\n社員・役員から怒号の内線が殺到し、評価ランクは最低（ボーナス大減額）になります。",
                            fontSize = 10.sp,
                            color = XpAlertRed
                        )
                    }
                }
                Text(
                    text = "【情報セキュリティ解説】\n${action.securityConcept}",
                    fontSize = 10.sp,
                    color = Color(0xFF1E3A8A)
                )
            }
        },
        confirmButton = {
            XpButton(text = "OK", onClick = onDismiss)
        }
    )
}
