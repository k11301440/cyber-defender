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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cyberdefender.model.Stage
import com.example.cyberdefender.model.StageCatalog
import com.example.cyberdefender.ui.components.*
import com.example.cyberdefender.ui.theme.*
import com.example.cyberdefender.viewmodel.BattleViewModel

@Composable
fun StageSelectScreen(
    viewModel: BattleViewModel,
    onSelectStage: (Int) -> Unit,
    onOpenKnowledge: () -> Unit,
    onOpenUpgrades: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.gameProgress.collectAsStateWithLifecycle()
    val nextStoryStageId = progress.nextStoryStageId
    val nextStage = StageCatalog.stages.find { it.id == nextStoryStageId } ?: StageCatalog.stages.first()

    val prologueStep by viewModel.currentPrologueStep.collectAsStateWithLifecycle()
    val showSiemTutorial by viewModel.showSiemTutorial.collectAsStateWithLifecycle()

    CrtMonitorFrame(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(XpDesktopBlue) // Windows XP公式デスクトップ青
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            XpWindow(
                title = "帝都物産 情報システム部 - アプセトネデブ防衛作戦一覧",
                menuItems = listOf(
                    "設備投資(U)" to onOpenUpgrades,
                    "早見表・図鑑(H)" to onOpenKnowledge
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 課長の訓示バルーン
                    XpBalloonNotification(
                        speaker = "情シス課長 (内線: 101)",
                        message = "「アプセトネデブの各層をピンポイントで防護せよ！適正な層を選ばないと効果が半減するぞ。防衛成功の部署予算で人員増員やSIEM導入を進めろ！」",
                        isAngry = false
                    )

                    // 部署予算 ＆ 各種ショートカット
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp))
                            .background(XpWindowBg)
                            .border(1.dp, Color(0xFF003C74), RoundedCornerShape(3.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
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
                                Text(
                                    text = "部署IT予算:",
                                    fontSize = 12.sp,
                                    color = XpTextDark,
                                    fontFamily = PixelFontFamily
                                )
                                Text(
                                    text = "${progress.departmentBudget.toString().reversed().chunked(3).joinToString(",").reversed()} 円",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    fontFamily = PixelFontFamily
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                XpButton(
                                    text = "設備投資",
                                    onClick = onOpenUpgrades,
                                    fontSize = 11,
                                    minHeight = 38
                                )
                                XpButton(
                                    text = "図鑑・早見表",
                                    onClick = onOpenKnowledge,
                                    fontSize = 11,
                                    minHeight = 38
                                )
                            }
                        }
                    }

                    // メインリスト
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. 次の話へ出撃（推奨）
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "[次に出撃すべき防衛任務]:",
                                    color = Color(0xFF003C74),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = PixelFontFamily
                                )

                                NextStageHeroCard(
                                    stage = nextStage,
                                    onClick = { viewModel.startStage(nextStage.id); if (nextStage.id > 1 || progress.isPrologueStorySeen) onSelectStage(nextStage.id) }
                                )
                            }
                        }

                        // 2. 作戦アーカイブ
                        item {
                            Text(
                                text = "作戦アーカイブ一覧 (Clear状況):",
                                color = XpTextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = PixelFontFamily
                            )
                        }

                        val storyStages = StageCatalog.stages.filter { !it.isEndgame }
                        items(storyStages) { stage ->
                            val isCleared = progress.clearedStageIds.contains(stage.id)
                            val isUnlocked = stage.id <= progress.nextStoryStageId

                            StageArchiveCard(
                                stage = stage,
                                isCleared = isCleared,
                                isUnlocked = isUnlocked,
                                onClick = {
                                    if (isUnlocked) {
                                        viewModel.startStage(stage.id)
                                        if (stage.id > 1 || progress.isPrologueStorySeen) onSelectStage(stage.id)
                                    }
                                }
                            )
                        }

                        // 3. エンドコンテンツ
                        val endgameStage = StageCatalog.stages.find { it.isEndgame }
                        if (endgameStage != null) {
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        text = "[エンドコンテンツ (高難度サバイバル)]:",
                                        color = Color(0xFF991B1B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = PixelFontFamily
                                    )

                                    EndgameStageCard(
                                        stage = endgameStage,
                                        isUnlocked = progress.isAllStoryCleared,
                                        onClick = {
                                            if (progress.isAllStoryCleared) {
                                                viewModel.selectStage(endgameStage.id)
                                                onSelectStage(endgameStage.id)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // プロローグ会話イベントダイアログ (第1話突入前)
        prologueStep?.let { step ->
            StoryDialog(
                step = step,
                onChoiceSelected = { choice ->
                    viewModel.advancePrologue(choice)
                    if (choice.nextStepId == 0) {
                        onSelectStage(1)
                    }
                }
            )
        }

        // 第1話クリア後のSIEM購入チュートリアルダイアログ
        if (showSiemTutorial) {
            XpClassicDialog(
                title = "情シス新任研修 - [チュートリアル通知]",
                onDismissRequest = { viewModel.dismissSiemTutorial() }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "情シス課長より業務指示:",
                        color = Color(0xFF0055EA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                    Text(
                        text = "「第1話の防衛成功おめでとう！\n" +
                                "獲得した部署予算を使って、まずはメニューの【設備投資】から『SIEM統合ログ監視コンソール (Lv1)』を購入して導入しろ！\n" +
                                "SIEMを導入すれば、第2話以降で機器からの侵入ログがリアルタイムに手元に届くようになるぞ！」",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontFamily = PixelFontFamily,
                        lineHeight = 17.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        XpButton(
                            text = "設備投資へ行く [>>]",
                            onClick = {
                                viewModel.dismissSiemTutorial()
                                onOpenUpgrades()
                            },
                            isPrimary = true,
                            modifier = Modifier.weight(1f),
                            minHeight = 44
                        )
                        XpButton(
                            text = "後で確認 [OK]",
                            onClick = { viewModel.dismissSiemTutorial() },
                            modifier = Modifier.weight(1f),
                            minHeight = 44
                        )
                    }
                }
            }
        }
    }
}

/**
 * 次の話へ出撃する大型推奨カード
 */
@Composable
fun NextStageHeroCard(
    stage: Stage,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE), Color(0xFFBFDBFE))
                )
            )
            .border(2.dp, Color(0xFF2563EB), RoundedCornerShape(3.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2563EB), RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NEXT MISSION",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PixelFontFamily
                        )
                    }
                    Text(
                        text = "[難易度: ${stage.difficulty}]",
                        color = Color(0xFF1E40AF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }

                Text(
                    text = stage.title,
                    color = Color(0xFF1E3A8A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stage.subtitle,
                    color = Color(0xFF3B82F6),
                    fontSize = 11.sp,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            XpButton(
                text = "出撃 [>>]",
                onClick = onClick,
                isPrimary = true,
                fontSize = 13,
                minHeight = 44
            )
        }
    }
}

/**
 * 過去の話一覧カード (Clearバッジの横位置を完全に揃えた表形式レイアウト)
 */
@Composable
fun StageArchiveCard(
    stage: Stage,
    isCleared: Boolean,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isUnlocked) Color.White else Color(0xFFF1F5F9)
    val border = if (isCleared) Color(0xFF16A34A) else if (isUnlocked) Color(0xFFCBD5E1) else Color(0xFFE2E8F0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp) // 操作域拡大
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(3.dp))
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左側: タイトル & サブタイトル
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = if (isUnlocked) stage.title else "[未開放] 第${stage.id}話",
                color = if (isUnlocked) Color(0xFF1E293B) else Color(0xFF94A3B8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PixelFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stage.subtitle,
                color = if (isUnlocked) Color(0xFF64748B) else Color(0xFFCBD5E1),
                fontSize = 10.sp,
                fontFamily = PixelFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 右側: 固定幅(70.dp)のClearバッジ枠 ＆ ボタン (完全に垂直位置を整列！)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 固定幅のClearステータス領域
            Box(
                modifier = Modifier.width(64.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isCleared) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF22C55E), RoundedCornerShape(2.dp))
                            .border(1.dp, Color(0xFF15803D), RoundedCornerShape(2.dp))
                            .padding(vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "[Clear]",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PixelFontFamily
                        )
                    }
                } else if (isUnlocked) {
                    Text(
                        text = "[未挑戦]",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontFamily = PixelFontFamily
                    )
                } else {
                    Text(
                        text = "[封鎖]",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontFamily = PixelFontFamily
                    )
                }
            }

            if (isUnlocked) {
                XpButton(
                    text = if (isCleared) "再挑戦" else "挑戦",
                    onClick = onClick,
                    fontSize = 11,
                    minHeight = 38
                )
            }
        }
    }
}

/**
 * エンドコンテンツ用カード
 */
@Composable
fun EndgameStageCard(
    stage: Stage,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val bgBrush = if (isUnlocked) {
        Brush.verticalGradient(
            listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2), Color(0xFFFECACA))
        )
    } else {
        Brush.verticalGradient(listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0)))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(3.dp))
            .background(bgBrush)
            .border(
                2.dp,
                if (isUnlocked) Color(0xFFDC2626) else Color(0xFF94A3B8),
                RoundedCornerShape(3.dp)
            )
            .clickable(enabled = isUnlocked, onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(if (isUnlocked) Color(0xFFDC2626) else Color(0xFF64748B), RoundedCornerShape(2.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isUnlocked) "[90秒制限]" else "[封鎖中]",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PixelFontFamily
                        )
                    }
                    Text(
                        text = "[難易度: NIGHTMARE]",
                        color = if (isUnlocked) Color(0xFF991B1B) else Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }

                Text(
                    text = stage.title,
                    color = if (isUnlocked) Color(0xFF7F1D1D) else Color(0xFF64748B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (isUnlocked) "全アプセトネデブ層から複合波状攻撃！多層防御で生き残れ！" else "全ストーリー（第1話〜最終話）をクリアすると解放されます",
                    color = if (isUnlocked) Color(0xFF991B1B) else Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontFamily = PixelFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isUnlocked) {
                XpButton(
                    text = "非常出撃",
                    onClick = onClick,
                    isPrimary = true,
                    fontSize = 12,
                    minHeight = 44
                )
            }
        }
    }
}
