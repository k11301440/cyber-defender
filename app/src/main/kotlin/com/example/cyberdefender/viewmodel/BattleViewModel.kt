package com.example.cyberdefender.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cyberdefender.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BattleViewModel : ViewModel() {

    private val _currentStageId = MutableStateFlow(1)
    val currentStageId: StateFlow<Int> = _currentStageId.asStateFlow()

    private val _battleState = MutableStateFlow(createInitialBattleState(1))
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    val availableActions = DefenseActionCatalog.allActions

    fun selectStage(stageId: Int) {
        _currentStageId.value = stageId
        _battleState.value = createInitialBattleState(stageId)
    }

    fun restartStage() {
        _battleState.value = createInitialBattleState(_currentStageId.value)
    }

    private fun createInitialBattleState(stageId: Int): BattleState {
        val stage = StageCatalog.stages.find { it.id == stageId } ?: StageCatalog.stages.first()
        val threat = stage.createThreat()
        val firstAttack = threat.attackList.first()

        val initialLogs = listOf(
            BattleLog(
                sender = "情シス室端末",
                message = "=== [内線着信] ${stage.title} ===",
                type = LogType.ALERT
            ),
            BattleLog(
                sender = "社内通報",
                message = threat.storyIntro,
                type = LogType.WARNING
            ),
            BattleLog(
                sender = "アプセトネデブ解析",
                message = "敵は【${threat.layer.code}: ${threat.layer.japaneseLabel}】から侵入中！",
                type = LogType.CRITICAL
            ),
            BattleLog(
                sender = "防御の指針",
                message = "迎撃ヒント: ${threat.weaknessHint}",
                type = LogType.INFO
            ),
            BattleLog(
                sender = "攻撃兆候",
                message = "敵の次の一手: ${firstAttack.warningMessage}",
                type = LogType.WARNING
            )
        )

        return BattleState(
            serverHp = 100,
            maxServerHp = 100,
            shield = 20,
            maxShield = 100,
            ap = 4,
            maxAp = 5,
            threat = threat,
            nextAttackIndex = 0,
            turn = 1,
            isThreatStunned = false,
            businessImpact = 0,
            latestComplaint = null,
            complaintSpeaker = null,
            isOverkillUsed = false,
            phase = BattlePhase.IN_BATTLE,
            logs = initialLogs,
            lastEducationalMessage = null
        )
    }

    fun executeAction(action: DefenseAction) {
        val currentState = _battleState.value
        if (currentState.phase != BattlePhase.IN_BATTLE) return

        if (currentState.ap < action.apCost) {
            addLog("情シス端末", "AP（PCメモリリソース）不足！コマンド実行不可 (必要AP: ${action.apCost}, 現在AP: ${currentState.ap})", LogType.WARNING)
            return
        }

        // 過剰防衛の判定
        if (action.isOverkill) {
            val newLogs = mutableListOf<BattleLog>()
            newLogs.add(
                BattleLog(
                    sender = "緊急遮断コマンド",
                    message = "!!! ${action.name} を強行実行 !!!",
                    type = LogType.CRITICAL
                )
            )
            newLogs.add(
                BattleLog(
                    sender = "社内大炎上",
                    message = action.complaintOnUse ?: "全社業務が強制停止！社内から怒号の内線が殺到！",
                    type = LogType.ALERT
                )
            )
            newLogs.add(
                BattleLog(
                    sender = "インシデント判定",
                    message = "敵の通信は遮断できたが、業務停止で始末書確定！（評価C・ボーナスカット）",
                    type = LogType.WARNING
                )
            )

            _battleState.update {
                it.copy(
                    ap = 0,
                    threat = it.threat.copyWithHp(0),
                    businessImpact = 100,
                    isOverkillUsed = true,
                    latestComplaint = action.complaintOnUse,
                    complaintSpeaker = "営業部・経理部・役員一同",
                    phase = BattlePhase.VICTORY,
                    logs = it.logs + newLogs,
                    lastEducationalMessage = "【過剰防衛による強制クリア】\n" + action.securityConcept
                )
            }
            return
        }

        val isCritical = (currentState.threat.criticalWeakness == action.id)
        val calculatedDamage = if (isCritical) {
            (action.baseDamage * 1.6).toInt()
        } else {
            action.baseDamage
        }

        val newAp = currentState.ap - action.apCost
        val newShield = (currentState.shield + action.baseShield).coerceAtMost(currentState.maxShield)
        val newHp = (currentState.serverHp + action.healAmount).coerceAtMost(currentState.maxServerHp)
        val newThreatHp = (currentState.threat.currentHp - calculatedDamage).coerceAtLeast(0)
        val updatedThreat = currentState.threat.copyWithHp(newThreatHp)
        val newImpact = (currentState.businessImpact + action.businessImpact).coerceAtMost(100)

        val newLogs = mutableListOf<BattleLog>()
        newLogs.add(
            BattleLog(
                sender = "情シス実行",
                message = "> ${action.commandCode} [${action.name}]",
                type = LogType.INFO
            )
        )

        if (action.healAmount > 0) {
            newLogs.add(
                BattleLog(
                    sender = "バックアップ",
                    message = "オフライン磁気テープからサーバー健全性を +${action.healAmount} 復旧！ (現在HP: $newHp)",
                    type = LogType.SUCCESS
                )
            )
        }

        var isStunned = currentState.isThreatStunned
        var eduMessage: String? = null

        if (isCritical) {
            isStunned = true
            eduMessage = "【アプセトネデブ適正防衛 成功！】\n${action.securityConcept}"
            newLogs.add(
                BattleLog(
                    sender = "適正層防衛",
                    message = "★ 層の弱点にズバリ的中！ ${calculatedDamage}ダメージを与え、敵の攻撃を遮断＆スタン！業務影響ゼロ！",
                    type = LogType.CRITICAL
                )
            )
        } else if (action.baseDamage > 0) {
            newLogs.add(
                BattleLog(
                    sender = "対抗措置",
                    message = "防御パケット送出: 敵HPを $calculatedDamage 削ったが、層が違うため完全には止められない！",
                    type = LogType.WARNING
                )
            )
        }

        // 敵HP 0 で勝利
        if (newThreatHp <= 0) {
            newLogs.add(
                BattleLog(
                    sender = "防衛完了",
                    message = "社内インシデントの封じ込めに成功！自社サーバを防衛した！",
                    type = LogType.CRITICAL
                )
            )

            _battleState.update {
                it.copy(
                    ap = newAp,
                    shield = newShield,
                    serverHp = newHp,
                    threat = updatedThreat,
                    businessImpact = newImpact,
                    phase = BattlePhase.VICTORY,
                    logs = it.logs + newLogs,
                    lastEducationalMessage = eduMessage ?: updatedThreat.educationalDetail
                )
            }
            return
        }

        _battleState.update {
            it.copy(
                ap = newAp,
                shield = newShield,
                serverHp = newHp,
                threat = updatedThreat,
                isThreatStunned = isStunned,
                businessImpact = newImpact,
                logs = it.logs + newLogs,
                lastEducationalMessage = eduMessage ?: it.lastEducationalMessage
            )
        }
    }

    fun endTurn() {
        val currentState = _battleState.value
        if (currentState.phase != BattlePhase.IN_BATTLE) return

        val newLogs = mutableListOf<BattleLog>()
        newLogs.add(
            BattleLog(
                sender = "情シス日誌",
                message = "--- 第${currentState.turn}ターン 終了 ---",
                type = LogType.INFO
            )
        )

        var newServerHp = currentState.serverHp
        var newShield = currentState.shield

        if (currentState.isThreatStunned) {
            newLogs.add(
                BattleLog(
                    sender = "脅威監視",
                    message = "敵は適正防衛により通信遮断中！攻撃をスキップした！",
                    type = LogType.SUCCESS
                )
            )
        } else {
            val attack = currentState.currentAttack
            newLogs.add(
                BattleLog(
                    sender = "敵攻撃直撃",
                    message = "敵の攻撃: ${attack.name}！ ${attack.executedMessage}",
                    type = LogType.ALERT
                )
            )

            if (attack.piercesShield) {
                newServerHp = (newServerHp - attack.damage).coerceAtLeast(0)
            } else {
                val remaining = attack.damage - newShield
                if (remaining > 0) {
                    newShield = 0
                    newServerHp = (newServerHp - remaining).coerceAtLeast(0)
                } else {
                    newShield = (newShield - attack.damage).coerceAtLeast(0)
                }
            }
        }

        // 敗北判定
        if (newServerHp <= 0) {
            newLogs.add(
                BattleLog(
                    sender = "重大インシデント",
                    message = "基幹サーバが停止。全社データ喪失・システムダウン...",
                    type = LogType.CRITICAL
                )
            )

            _battleState.update {
                it.copy(
                    serverHp = 0,
                    shield = newShield,
                    phase = BattlePhase.DEFEAT,
                    logs = it.logs + newLogs
                )
            }
            return
        }

        val nextTurnNumber = currentState.turn + 1
        val nextAttackIdx = currentState.nextAttackIndex + 1
        val nextAttack = currentState.threat.attackList[nextAttackIdx % currentState.threat.attackList.size]

        newLogs.add(
            BattleLog(
                sender = "リソース回復",
                message = "メモリリソース(AP)が回復 (AP: ${currentState.maxAp})",
                type = LogType.INFO
            )
        )
        newLogs.add(
            BattleLog(
                sender = "攻撃兆候",
                message = "【次回攻撃予測】${nextAttack.warningMessage}",
                type = LogType.WARNING
            )
        )

        _battleState.update {
            it.copy(
                serverHp = newServerHp,
                shield = newShield,
                ap = it.maxAp,
                turn = nextTurnNumber,
                nextAttackIndex = nextAttackIdx,
                isThreatStunned = false,
                logs = it.logs + newLogs
            )
        }
    }

    private fun addLog(sender: String, message: String, type: LogType) {
        _battleState.update {
            it.copy(
                logs = it.logs + BattleLog(sender = sender, message = message, type = type)
            )
        }
    }
}
