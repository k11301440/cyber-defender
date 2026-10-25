package com.example.cyberdefender.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
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
    modifier: Modifier = Modifier
) {
    val state by viewModel.battleState.collectAsStateWithLifecycle()
    val currentStageId by viewModel.currentStageId.collectAsStateWithLifecycle()
    val stage = StageCatalog.stages.find { it.id == currentStageId } ?: StageCatalog.stages.first()

    // 画面全体をブラウン管（CRT）走査線フレームで覆う
    CrtMonitorFrame(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF004E98)) // Windows XPのデスクトップ背景風ブルー
                .padding(4.dp)
        ) {
            // Windows XP Lunaウィンドウ
            XpWindow(
                title = "情シス防御コンソール - ${stage.title} [第${state.turn}ターン]",
                onCloseClick = onNavigateBack,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 内線着信通報（ストーリーの導入）
                    XpBalloonNotification(
                        speaker = "内線通報 (社内LAN監視)",
                        message = state.threat.storyIntro,
                        isAngry = false
                    )

                    // 敵の侵入モニタ（アプセトネデブ層バッジ・HP・次回予兆）
                    ThreatCard(
                        threat = state.threat,
                        currentAttack = state.currentAttack,
                        isStunned = state.isThreatStunned
                    )

                    // Windows XP コマンドプロンプト風リアルタイムログ
                    TerminalLogView(
                        logs = state.logs,
                        modifier = Modifier.weight(1f)
                    )

                    // サーバー健全性 & 業務影響度（クレーム指数）
                    ServerStatusPanel(
                        serverHp = state.serverHp,
                        maxHp = state.maxServerHp,
                        shield = state.shield,
                        maxShield = state.maxShield,
                        ap = state.ap,
                        maxAp = state.maxAp,
                        businessImpact = state.businessImpact
                    )

                    // アプセトネデブ防護コマンド選択コンソール
                    ActionSelector(
                        actions = viewModel.availableActions,
                        currentAp = state.ap,
                        onActionClick = { action -> viewModel.executeAction(action) },
                        onEndTurnClick = { viewModel.endTurn() }
                    )
                }
            }
        }

        // バトル結果ダイアログ (過剰防衛ペナルティ・ボーナス査定)
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
            hasNextStage = currentStageId < StageCatalog.stages.size
        )
    }
}
