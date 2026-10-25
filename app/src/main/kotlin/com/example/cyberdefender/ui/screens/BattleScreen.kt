package com.example.cyberdefender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cyberdefender.model.StageCatalog
import com.example.cyberdefender.ui.components.*
import com.example.cyberdefender.ui.theme.*
import com.example.cyberdefender.viewmodel.BattleViewModel

@Composable
fun BattleScreen(
    viewModel: BattleViewModel,
    onNavigateBack: () -> Unit,
    onOpenKnowledge: () -> Unit,
    onOpenUpgrades: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.battleState.collectAsStateWithLifecycle()
    val progress by viewModel.gameProgress.collectAsStateWithLifecycle()
    val currentStageId by viewModel.currentStageId.collectAsStateWithLifecycle()
    val stage = StageCatalog.stages.find { it.id == currentStageId } ?: StageCatalog.stages.first()

    CrtMonitorFrame(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(XpDesktopBlue) // Windows XP公式デスクトップブルー
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            XpWindow(
                title = "情シス防御コンソール - ${stage.title} [第${state.turn}ターン]",
                onCloseClick = onNavigateBack,
                menuItems = listOf(
                    "作戦一覧(O)" to onNavigateBack,
                    "ログ図鑑(L)" to onOpenKnowledge,
                    "設備投資(U)" to onOpenUpgrades,
                    "早見表(H)" to onOpenKnowledge
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 1. スリムなステータスダッシュボード（敵HP & 自社サーバHP & シールド）
                    BattleStatusBar(
                        threat = state.threat,
                        serverHp = state.serverHp,
                        maxServerHp = state.maxServerHp,
                        shield = state.shield,
                        maxShield = state.maxShield,
                        isStunned = state.isThreatStunned
                    )

                    // 2. 上部: クラシック白地タイムライン（リアルタイム感演出・絵文字完全ゼロ）
                    ChatTimelineView(
                        messages = state.messages,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    // 3. 下部: 2ステップ操作画面（アプセトネデブ層選択 -> コマンド選択・操作域拡大）
                    TwoStepActionSelector(
                        selectedLayer = state.selectedLayer,
                        isOverkillOpen = state.isOverkillMenuOpen,
                        unlockedLayers = progress.unlockedLayers,
                        unlockedActionIds = progress.unlockedActionIds,
                        remainingActions = state.remainingActions,
                        maxActions = state.maxActionsPerTurn,
                        remainingTimeSeconds = state.remainingTimeSeconds,
                        onSelectLayer = { layer -> viewModel.selectLayer(layer) },
                        onOpenOverkill = { viewModel.openOverkillMenu() },
                        onBackToLayerSelect = { viewModel.clearLayerSelection() },
                        onExecuteAction = { action -> viewModel.executeAction(action) },
                        onEndTurn = { viewModel.endTurn() }
                    )
                }
            }
        }

        // バトル結果ダイアログ (XPクラシックダイアログ形式)
        BattleResultDialog(
            state = state,
            onRestart = { viewModel.restartStage() },
            onNextStage = {
                val nextStageId = currentStageId + 1
                if (nextStageId <= StageCatalog.stages.size) {
                    viewModel.selectStage(nextStageId)
                }
            },
            onBackToStages = onNavigateBack,
            onOpenUpgrades = onOpenUpgrades,
            hasNextStage = currentStageId < StageCatalog.stages.size
        )
    }
}
