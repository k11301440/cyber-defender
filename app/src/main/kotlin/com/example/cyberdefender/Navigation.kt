package com.example.cyberdefender

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.cyberdefender.ui.screens.BattleScreen
import com.example.cyberdefender.ui.screens.KnowledgeScreen
import com.example.cyberdefender.ui.screens.StageSelectScreen
import com.example.cyberdefender.ui.screens.UpgradeScreen
import com.example.cyberdefender.viewmodel.BattleViewModel

@Composable
fun MainNavigation(
    viewModel: BattleViewModel = viewModel()
) {
    val backStack = rememberNavBackStack(StageSelectKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<StageSelectKey> {
                StageSelectScreen(
                    viewModel = viewModel,
                    onSelectStage = { stageId ->
                        viewModel.selectStage(stageId)
                        backStack.add(BattleKey)
                    },
                    onOpenKnowledge = {
                        backStack.add(KnowledgeKey)
                    },
                    onOpenUpgrades = {
                        backStack.add(UpgradeKey)
                    }
                )
            }
            entry<BattleKey> {
                BattleScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onOpenKnowledge = {
                        backStack.add(KnowledgeKey)
                    },
                    onOpenUpgrades = {
                        backStack.add(UpgradeKey)
                    }
                )
            }
            entry<KnowledgeKey> {
                KnowledgeScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<UpgradeKey> {
                UpgradeScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
