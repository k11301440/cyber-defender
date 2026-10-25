package com.example.cyberdefender.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberdefender.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BattleViewModel : ViewModel() {

    // ゲーム全体の永続進行状況（クリア状況、アンロック層、予算、強化、図鑑）
    private val _gameProgress = MutableStateFlow(GameProgress())
    val gameProgress: StateFlow<GameProgress> = _gameProgress.asStateFlow()

    private val _currentStageId = MutableStateFlow(1)
    val currentStageId: StateFlow<Int> = _currentStageId.asStateFlow()

    private val _battleState = MutableStateFlow(createInitialBattleState(1, _gameProgress.value))
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimerIfNeeded()
    }

    fun selectStage(stageId: Int) {
        _currentStageId.value = stageId
        _battleState.value = createInitialBattleState(stageId, _gameProgress.value)
        startTimerIfNeeded()
    }

    fun restartStage() {
        _battleState.value = createInitialBattleState(_currentStageId.value, _gameProgress.value)
        startTimerIfNeeded()
    }

    private fun startTimerIfNeeded() {
        timerJob?.cancel()
        val stage = StageCatalog.stages.find { it.id == _currentStageId.value } ?: return
        val limit = stage.timeLimitSeconds ?: return

        timerJob = viewModelScope.launch {
            var timeLeft = limit
            while (timeLeft > 0) {
                delay(1000L)
                if (_battleState.value.phase != BattlePhase.IN_BATTLE) break
                timeLeft--
                _battleState.update { it.copy(remainingTimeSeconds = timeLeft) }
            }
            if (timeLeft <= 0 && _battleState.value.phase == BattlePhase.IN_BATTLE) {
                // タイムアップ敗北
                addChatMessage(
                    ChatSenderType.ALERT,
                    "緊急スクランブル警報",
                    "制限時間（90秒）が超過！APT部隊により全社基幹サーバが完全に掌握されました…！",
                    isImportant = true
                )
                _battleState.update { it.copy(phase = BattlePhase.DEFEAT, serverHp = 0) }
            }
        }
    }

    private fun createInitialBattleState(stageId: Int, progress: GameProgress): BattleState {
        val stage = StageCatalog.stages.find { it.id == stageId } ?: StageCatalog.stages.first()
        val threat = stage.createThreat()
        val firstAttack = threat.attackList.first()

        // 設備投資（アップグレード）の効果反映
        val maxHp = if (progress.hasCdn) 130 else 100
        val maxShield = if (progress.hasUtm) 130 else 100
        val initialShield = if (progress.hasUtm) 50 else 20
        val actionsPerTurn = if (progress.hasExtraStaff) 2 else 1

        val initialMessages = mutableListOf<ChatMessage>()

        // 1. 内線着信
        initialMessages.add(
            ChatMessage(
                senderType = ChatSenderType.PHONE,
                speaker = "内線着信 (社内通報)",
                message = threat.storyIntro,
                timestamp = "17:00",
                isImportant = true
            )
        )

        // 2. システム監視アラート
        initialMessages.add(
            ChatMessage(
                senderType = ChatSenderType.ALERT,
                speaker = "SIEM監視コンソール",
                message = "【侵入検知】${threat.name} [${threat.codeName}] の攻撃パケットを捕捉！",
                timestamp = "17:01"
            )
        )

        // 3. 情シスAIの分析メッセージ
        if (progress.hasAiUpgrade) {
            initialMessages.add(
                ChatMessage(
                    senderType = ChatSenderType.AI,
                    speaker = "情シスAI ヘルプデスク君v2.0",
                    message = "💡【AI高精度解析】この攻撃はアプセトネデブの『${threat.layer.shortName}』に直結しています！下部操作パネルから『${threat.layer.code}』を選んでピンポイント迎撃してください！",
                    timestamp = "17:01",
                    isImportant = true
                )
            )
        } else {
            initialMessages.add(
                ChatMessage(
                    senderType = ChatSenderType.AI,
                    speaker = "情シスAI ヘルプデスク君",
                    message = "💡 迎撃ヒント: ${threat.weaknessHint}",
                    timestamp = "17:01"
                )
            )
        }

        // 4. 敵の第1撃予告
        initialMessages.add(
            ChatMessage(
                senderType = ChatSenderType.ALERT,
                speaker = "攻撃兆候モニタ",
                message = "敵の次の一手: ${firstAttack.warningMessage}",
                timestamp = "17:02"
            )
        )

        return BattleState(
            serverHp = maxHp,
            maxServerHp = maxHp,
            shield = initialShield,
            maxShield = maxShield,
            turn = 1,
            remainingActions = actionsPerTurn,
            maxActionsPerTurn = actionsPerTurn,
            threat = threat,
            nextAttackIndex = 0,
            isThreatStunned = false,
            businessImpact = 0,
            isOverkillUsed = false,
            phase = BattlePhase.IN_BATTLE,
            messages = initialMessages,
            selectedLayer = null,
            isOverkillMenuOpen = false,
            remainingTimeSeconds = stage.timeLimitSeconds
        )
    }

    // === 2ステップ選択UI操作 ===

    fun selectLayer(layer: OsiLayer) {
        _battleState.update {
            it.copy(
                selectedLayer = layer,
                isOverkillMenuOpen = false
            )
        }
    }

    fun openOverkillMenu() {
        _battleState.update {
            it.copy(
                selectedLayer = null,
                isOverkillMenuOpen = true
            )
        }
    }

    fun clearLayerSelection() {
        _battleState.update {
            it.copy(
                selectedLayer = null,
                isOverkillMenuOpen = false
            )
        }
    }

    // === 防御アクション実行 ===

    fun executeAction(action: DefenseAction) {
        val currentState = _battleState.value
        if (currentState.phase != BattlePhase.IN_BATTLE) return
        if (currentState.remainingActions <= 0) return

        // 実行メッセージ追加
        addChatMessage(
            ChatSenderType.ACTION,
            "情シス操作実行",
            "▶ [${action.name}] を実行！\n$ ${action.commandCode}"
        )

        // 過剰防衛の場合
        if (action.isOverkill) {
            val complaint = action.complaintOnUse ?: "社員「おい！仕事にならないぞ！」"
            addChatMessage(
                ChatSenderType.COMPLAINT,
                "怒りの内線殺到",
                complaint,
                isImportant = true
            )

            _battleState.update { state ->
                state.copy(
                    threat = state.threat.copyWithHp(0),
                    businessImpact = 100,
                    isOverkillUsed = true,
                    phase = BattlePhase.VICTORY,
                    remainingActions = 0,
                    selectedLayer = null,
                    isOverkillMenuOpen = false
                )
            }
            onVictory()
            return
        }

        // 通常または弱点攻撃
        val isCriticalWeakness = (action.id == currentState.threat.criticalWeakness) || (action.layer == currentState.threat.layer && action.baseDamage > 0)
        var actualDamage = action.baseDamage
        var newStun = currentState.isThreatStunned

        if (isCriticalWeakness && action.baseDamage > 0) {
            actualDamage = (actualDamage * 1.6).toInt()
            newStun = true
            addChatMessage(
                ChatSenderType.AI,
                "アプセトネデブ解析",
                "🎯 適正層（${action.layer.shortName}）へのピンポイント対策成功！大打撃を与えて敵がスタンした！",
                isImportant = true
            )
        } else if (action.baseDamage > 0) {
            addChatMessage(
                ChatSenderType.ACTION,
                "コマンド結果",
                "敵に通常対処を実行（${actualDamage}ダメージ）。"
            )
        }

        // シールド・HP回復
        var newShield = (currentState.shield + action.baseShield).coerceAtMost(currentState.maxShield)
        var newHp = (currentState.serverHp + action.healAmount).coerceAtMost(currentState.maxServerHp)
        if (action.baseShield > 0) {
            addChatMessage(ChatSenderType.ACTION, "防壁展開", "シールドを +${action.baseShield} 展開した！（現在: $newShield）")
        }
        if (action.healAmount > 0) {
            addChatMessage(ChatSenderType.ACTION, "整合性復旧", "オフライン磁気テープからサーバを +${action.healAmount} 修復！（現在: $newHp）")
        }

        val newThreatHp = (currentState.threat.currentHp - actualDamage).coerceAtLeast(0)
        val updatedThreat = currentState.threat.copyWithHp(newThreatHp)

        val nextRemainingActions = currentState.remainingActions - 1

        _battleState.update { state ->
            state.copy(
                threat = updatedThreat,
                serverHp = newHp,
                shield = newShield,
                isThreatStunned = newStun,
                remainingActions = nextRemainingActions,
                selectedLayer = null,
                isOverkillMenuOpen = false
            )
        }

        // 敵撃破判定
        if (newThreatHp <= 0) {
            addChatMessage(
                ChatSenderType.AI,
                "情シスAI",
                "🎉 敵インシデントの完全沈静化を確認！防衛ミッション完了！",
                isImportant = true
            )
            _battleState.update { it.copy(phase = BattlePhase.VICTORY) }
            onVictory()
            return
        }

        // 残り手数が0になったら自動でターン終了処理へ
        if (nextRemainingActions <= 0) {
            endTurn()
        }
    }

    // === ターン終了 & 敵の反撃フェーズ ===

    fun endTurn() {
        val currentState = _battleState.value
        if (currentState.phase != BattlePhase.IN_BATTLE) return

        val attack = currentState.currentAttack

        // 敵がスタンしている場合
        if (currentState.isThreatStunned) {
            addChatMessage(
                ChatSenderType.ALERT,
                "敵状態モニタ",
                "敵はスタン中のため、反撃不能！（スタン解除）"
            )

            // UTMのシールド自動回復
            var nextShield = currentState.shield
            if (_gameProgress.value.hasUtm) {
                nextShield = (nextShield + 10).coerceAtMost(currentState.maxShield)
                addChatMessage(ChatSenderType.ACTION, "UTM自動修復", "🛡️ 次世代UTMによりシールドが自動で +10 回復！")
            }

            val nextAttackIdx = (currentState.nextAttackIndex + 1) % currentState.threat.attackList.size
            val nextAttack = currentState.threat.attackList[nextAttackIdx]

            _battleState.update { state ->
                state.copy(
                    turn = state.turn + 1,
                    remainingActions = state.maxActionsPerTurn,
                    isThreatStunned = false,
                    shield = nextShield,
                    nextAttackIndex = nextAttackIdx,
                    selectedLayer = null,
                    isOverkillMenuOpen = false
                )
            }

            addChatMessage(
                ChatSenderType.ALERT,
                "攻撃兆候モニタ",
                "次ターンの予兆: ${nextAttack.warningMessage}"
            )
            return
        }

        // 敵の反撃実行
        addChatMessage(
            ChatSenderType.ALERT,
            "敵の反撃開始",
            "⚡ 敵の攻撃: 【${attack.name}】が直撃！\n${attack.executedMessage}",
            isImportant = true
        )

        // 被弾した攻撃を図鑑に登録！
        recordIncidentToLogBook(attack, currentState.threat.name)

        var damage = attack.damage
        // CDN効果: L3/L4/L7のダメージを常時-10軽減
        if (_gameProgress.value.hasCdn && (attack.layer == OsiLayer.L3_NETWORK || attack.layer == OsiLayer.L4_TRANSPORT || attack.layer == OsiLayer.L7_APPLICATION)) {
            damage = (damage - 10).coerceAtLeast(5)
            addChatMessage(ChatSenderType.ACTION, "CDN防御網", "🌐 全社CDNが分散キャッシュにより敵の負荷を -10 軽減！")
        }

        var currentShield = currentState.shield
        var currentHp = currentState.serverHp

        if (attack.piercesShield) {
            currentHp -= damage
            addChatMessage(ChatSenderType.ALERT, "シールド貫通", "⚠️ シールドを無視してサーバ本体に直接 $damage ダメージ！")
        } else {
            if (currentShield >= damage) {
                currentShield -= damage
                addChatMessage(ChatSenderType.ALERT, "シールド防衛", "シールドが $damage ダメージを吸収！（残シールド: $currentShield）")
            } else {
                val remainingDamage = damage - currentShield
                currentShield = 0
                currentHp -= remainingDamage
                addChatMessage(ChatSenderType.ALERT, "シールド損壊", "シールド破壊！サーバ本体に $remainingDamage ダメージ！（残HP: $currentHp）")
            }
        }

        // UTMのシールド自動回復
        if (_gameProgress.value.hasUtm && currentHp > 0) {
            currentShield = (currentShield + 10).coerceAtMost(currentState.maxShield)
            addChatMessage(ChatSenderType.ACTION, "UTM自動修復", "🛡️ 次世代UTMによりシールドが自動で +10 回復！")
        }

        if (currentHp <= 0) {
            currentHp = 0
            addChatMessage(
                ChatSenderType.ALERT,
                "全社サーバ停止",
                "💀 サーバ耐久力がゼロになりました。社内業務完全停止、防衛作戦失敗…",
                isImportant = true
            )
            _battleState.update { state ->
                state.copy(
                    serverHp = 0,
                    shield = currentShield,
                    phase = BattlePhase.DEFEAT,
                    selectedLayer = null,
                    isOverkillMenuOpen = false
                )
            }
            return
        }

        val nextAttackIdx = (currentState.nextAttackIndex + 1) % currentState.threat.attackList.size
        val nextAttack = currentState.threat.attackList[nextAttackIdx]

        _battleState.update { state ->
            state.copy(
                serverHp = currentHp,
                shield = currentShield,
                turn = state.turn + 1,
                remainingActions = state.maxActionsPerTurn,
                nextAttackIndex = nextAttackIdx,
                selectedLayer = null,
                isOverkillMenuOpen = false
            )
        }

        // 次の攻撃予告
        addChatMessage(
            ChatSenderType.ALERT,
            "攻撃兆候モニタ",
            "次ターンの予兆: ${nextAttack.warningMessage}"
        )
    }

    // === 図鑑（インシデントログ）への自動登録 ===

    private fun recordIncidentToLogBook(attack: ThreatAttack, threatName: String) {
        val existing = _gameProgress.value.incidentLogBook.any { it.attackName == attack.name }
        if (!existing) {
            val entry = IncidentLogEntry(
                attackName = attack.name,
                threatName = threatName,
                layer = attack.layer,
                damage = attack.damage,
                mechanism = attack.mechanism,
                defensePrinciple = attack.defensePrinciple,
                examTip = attack.examTip
            )
            _gameProgress.update { prog ->
                prog.copy(incidentLogBook = prog.incidentLogBook + entry)
            }
            addChatMessage(
                ChatSenderType.AI,
                "インシデント図鑑更新",
                "📋 新たな攻撃手口『${attack.name}』のデータをセキュリティ図鑑（ログ）に登録しました！"
            )
        }
    }

    // === 勝利時処理 (アンロック & 予算支給) ===

    private fun onVictory() {
        val stageId = _currentStageId.value
        val state = _battleState.value
        val rewardBudget = state.rewardBudgetAmount

        _gameProgress.update { prog ->
            val newCleared = prog.clearedStageIds + stageId
            // 次の層のアンロック
            val newUnlockedLayers = prog.unlockedLayers.toMutableSet()
            // クリアしたステージに応じた層のアンロック追加
            when (stageId) {
                1 -> newUnlockedLayers.add(OsiLayer.L2_DATALINK)
                2 -> newUnlockedLayers.add(OsiLayer.L3_NETWORK)
                3 -> newUnlockedLayers.add(OsiLayer.L4_TRANSPORT)
                4 -> newUnlockedLayers.add(OsiLayer.L5_SESSION)
                5 -> newUnlockedLayers.add(OsiLayer.L6_PRESENTATION)
                6 -> newUnlockedLayers.add(OsiLayer.L7_APPLICATION)
                7 -> {
                    // 全層開放
                    newUnlockedLayers.addAll(OsiLayer.values())
                }
            }

            prog.copy(
                clearedStageIds = newCleared,
                unlockedLayers = newUnlockedLayers,
                departmentBudget = prog.departmentBudget + rewardBudget
            )
        }
    }

    // === 部署設備投資（アップグレード購入） ===

    fun purchaseUpgrade(upgrade: UpgradeType): Boolean {
        val prog = _gameProgress.value
        if (prog.purchasedUpgrades.contains(upgrade)) return false
        if (prog.departmentBudget < upgrade.cost) return false

        _gameProgress.update {
            it.copy(
                departmentBudget = it.departmentBudget - upgrade.cost,
                purchasedUpgrades = it.purchasedUpgrades + upgrade
            )
        }
        return true
    }

    private fun addChatMessage(
        type: ChatSenderType,
        speaker: String,
        message: String,
        isImportant: Boolean = false
    ) {
        _battleState.update { state ->
            val newMsg = ChatMessage(
                senderType = type,
                speaker = speaker,
                message = message,
                isImportant = isImportant
            )
            state.copy(messages = state.messages + newMsg)
        }
    }
}
