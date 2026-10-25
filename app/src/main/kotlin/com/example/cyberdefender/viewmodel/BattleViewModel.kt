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

    private val _gameProgress = MutableStateFlow(GameProgress())
    val gameProgress: StateFlow<GameProgress> = _gameProgress.asStateFlow()

    private val _currentStageId = MutableStateFlow(1)
    val currentStageId: StateFlow<Int> = _currentStageId.asStateFlow()

    private val _battleState = MutableStateFlow(createInitialBattleState(1, _gameProgress.value))
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    // プロローグ会話イベント
    private val _currentPrologueStep = MutableStateFlow<StoryDialogStep?>(null)
    val currentPrologueStep: StateFlow<StoryDialogStep?> = _currentPrologueStep.asStateFlow()

    // SIEM購入チュートリアル表示フラグ
    private val _showSiemTutorial = MutableStateFlow(false)
    val showSiemTutorial: StateFlow<Boolean> = _showSiemTutorial.asStateFlow()

    private var timerJob: Job? = null

    init {
        startTimerIfNeeded()
    }

    fun startStage(stageId: Int) {
        val progress = _gameProgress.value
        if (stageId == 1 && !progress.isPrologueStorySeen) {
            // プロローグ会話を表示
            _currentPrologueStep.value = StoryCatalog.prologueSteps.first()
            _currentStageId.value = stageId
            return
        }
        selectStage(stageId)
    }

    fun advancePrologue(choice: StoryDialogChoice) {
        if (choice.nextStepId == 0) {
            // プロローグ終了 -> 第1話開始
            _currentPrologueStep.value = null
            _gameProgress.update { it.copy(isPrologueStorySeen = true) }
            selectStage(1)
        } else {
            val nextStep = StoryCatalog.prologueSteps.find { it.stepId == choice.nextStepId }
            _currentPrologueStep.value = nextStep
        }
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

    fun dismissSiemTutorial() {
        _showSiemTutorial.value = false
        _gameProgress.update { it.copy(isSiemTutorialShown = true) }
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
                addChatMessage(
                    ChatSenderType.ALERT,
                    "緊急スクランブル警報",
                    "制限時間（90秒）が超過！APT部隊により全社基幹サーバが完全に掌握されました。",
                    isImportant = true
                )
                _battleState.update { it.copy(phase = BattlePhase.DEFEAT, serverHp = 0) }
            }
        }
    }

    private fun createInitialBattleState(stageId: Int, progress: GameProgress): BattleState {
        val stage = StageCatalog.stages.find { it.id == stageId } ?: StageCatalog.stages.first()
        val threat = stage.createThreat()
        val firstIncidentId = threat.attackIncidentIds.first()
        val firstIncident = IncidentCatalog.getIncident(firstIncidentId)

        val maxHp = 100 + progress.cdnBonusHp
        val maxShield = 100 + progress.utmBonusShield
        val initialShield = 20 + progress.utmBonusShield
        val actionsPerTurn = if (progress.canTakeTwoActions) 2 else 1

        val initialMessages = mutableListOf<ChatMessage>()

        // 1日のはじまり（始業シークエンス）
        initialMessages.add(
            ChatMessage(
                senderType = ChatSenderType.SYSTEM,
                speaker = "勤怠管理システム",
                message = "08:45 [打刻] 出勤確認。情シス業務端末へログインしました。",
                timestamp = "08:45"
            )
        )
        initialMessages.add(
            ChatMessage(
                senderType = ChatSenderType.EMAIL,
                speaker = "総務部",
                message = "08:50 [全社連絡] 本日10時より経営会議予定。通信の安定稼働を願います。",
                timestamp = "08:50"
            )
        )
        initialMessages.add(
            ChatMessage(
                senderType = ChatSenderType.SYSTEM,
                speaker = "社内チャイム",
                message = "09:00 [始業] 定時業務を開始しました。",
                timestamp = "09:00"
            )
        )

        // 敵のストーリー通報
        initialMessages.add(
            ChatMessage(
                senderType = ChatSenderType.PHONE,
                speaker = "内線着信",
                message = threat.storyIntro,
                timestamp = "09:05",
                isImportant = true
            )
        )

        // SIEMを所持している場合のみSIEMからの高度な検知ログが出る
        if (progress.isSiemOwned) {
            initialMessages.add(
                ChatMessage(
                    senderType = ChatSenderType.SIEM,
                    speaker = "SIEM監視コンソール",
                    message = "【侵入検知】${firstIncident.deviceSource} より異常パケット捕捉: ${firstIncident.warningMessage}",
                    timestamp = "09:06",
                    isImportant = true
                )
            )
        } else {
            initialMessages.add(
                ChatMessage(
                    senderType = ChatSenderType.ALERT,
                    speaker = firstIncident.deviceSource,
                    message = "警告: ${firstIncident.warningMessage}",
                    timestamp = "09:06"
                )
            )
        }

        // AI Lv2以上の場合、有効対策サジェスト機能が発動
        if (progress.canAiSuggestEffectiveAction) {
            val effectiveAction = DefenseActionCatalog.allActions.find { it.id == firstIncident.effectiveActionId }
            val effectiveName = effectiveAction?.name ?: "適正コマンド"
            var aiText = "【AI解析 Lv${progress.getLevel(FacilityType.AI_ASSISTANT)}】敵の攻撃手口は『${firstIncident.name}』です。有効な対策は【${effectiveName}】です。"
            if (progress.canAiWarnTaboo && firstIncident.tabooActionId != null) {
                val tabooAction = DefenseActionCatalog.allActions.find { it.id == firstIncident.tabooActionId }
                aiText += "\n※警告: 【${tabooAction?.name}】は禁忌肢です！絶対に実行しないでください。"
            }
            initialMessages.add(
                ChatMessage(
                    senderType = ChatSenderType.AI,
                    speaker = "情シスAI ヘルプデスク君",
                    message = aiText,
                    timestamp = "09:07",
                    isImportant = true
                )
            )
        } else {
            initialMessages.add(
                ChatMessage(
                    senderType = ChatSenderType.AI,
                    speaker = "情シスAI ヘルプデスク君",
                    message = "迎撃ヒント: ${threat.weaknessHint}",
                    timestamp = "09:07"
                )
            )
        }

        return BattleState(
            serverHp = maxHp,
            maxServerHp = maxHp,
            shield = initialShield,
            maxShield = maxShield,
            turn = 1,
            remainingActions = actionsPerTurn,
            maxActionsPerTurn = actionsPerTurn,
            threat = threat,
            currentIncident = firstIncident,
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

    // === 2ステップ選択操作 ===

    fun selectLayer(layer: OsiLayer) {
        _battleState.update { it.copy(selectedLayer = layer, isOverkillMenuOpen = false) }
    }

    fun openOverkillMenu() {
        _battleState.update { it.copy(selectedLayer = null, isOverkillMenuOpen = true) }
    }

    fun clearLayerSelection() {
        _battleState.update { it.copy(selectedLayer = null, isOverkillMenuOpen = false) }
    }

    // === アクション実行 ===

    fun executeAction(action: DefenseAction) {
        val currentState = _battleState.value
        if (currentState.phase != BattlePhase.IN_BATTLE) return
        if (currentState.remainingActions <= 0) return

        // 1. 禁忌肢判定 (選んだら即負け)
        if (action.isTaboo) {
            val msg = action.tabooGameOverMessage ?: "[大惨事] 禁忌コマンドの実行により全社システムが完全壊滅しました！"
            addChatMessage(ChatSenderType.ACTION, "コマンド強行", "> ${action.name}\n${action.commandCode}")
            addChatMessage(ChatSenderType.ALERT, "致命的破壊発生", msg, isImportant = true)

            _battleState.update {
                it.copy(
                    phase = BattlePhase.TABOO_GAME_OVER,
                    tabooGameOverMessage = msg,
                    serverHp = 0,
                    remainingActions = 0,
                    selectedLayer = null,
                    isOverkillMenuOpen = false
                )
            }
            return
        }

        // 2. 過剰防衛判定
        if (action.isOverkill) {
            addChatMessage(ChatSenderType.ACTION, "緊急遮断実行", "> ${action.name}\n${action.commandCode}")
            val complaint = action.complaintOnUse ?: "社員「おい！仕事にならないぞ！」"
            addChatMessage(ChatSenderType.COMPLAINT, "怒りの内線殺到", complaint, isImportant = true)

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

        // 3. 通常防護アクションの実行
        addChatMessage(ChatSenderType.ACTION, "コマンド実行", "> [${action.name}]\n$ ${action.commandCode}")

        val incident = currentState.currentIncident
        val isEffective = (action.id == incident.effectiveActionId)
        val isSameLayer = (action.layer == incident.layer)

        var damage = action.expectedDamage
        var shieldGain = action.expectedShield
        var healGain = action.healAmount
        var stunEnemy = false

        if (isEffective) {
            // 正しい選択肢: 100%以上の大効果
            damage = (damage * 1.5).toInt()
            stunEnemy = true
            addChatMessage(
                ChatSenderType.AI,
                "解析判定: 適正対策",
                "【適正】攻撃『${incident.name}』に対するピンポイント対策成功！大打撃を与えて敵がスタンした！\n解説: ${incident.effectiveReason}",
                isImportant = true
            )
        } else if (isSameLayer) {
            // 同一層だがピンポイントではない: 通常効果 (敵はスタンしない)
            addChatMessage(
                ChatSenderType.ACTION,
                "解析判定: 通常対処",
                "同一層への対処を実行。（期待値どおりの効果。敵はスタンしません）"
            )
        } else {
            // 全く異なる層の対策: 効果半減 (または無効)
            damage = (damage * 0.4).toInt()
            shieldGain = (shieldGain * 0.4).toInt()
            addChatMessage(
                ChatSenderType.ALERT,
                "解析判定: 不適正層",
                "【不適正】攻撃の層と防護の層が一致していません！効果が半減しました。（与ダメージ: $damage）",
                isImportant = true
            )
        }

        val newShield = (currentState.shield + shieldGain).coerceAtMost(currentState.maxShield)
        val newHp = (currentState.serverHp + healGain).coerceAtMost(currentState.maxServerHp)
        val newThreatHp = (currentState.threat.currentHp - damage).coerceAtLeast(0)
        val updatedThreat = currentState.threat.copyWithHp(newThreatHp)

        val nextActions = currentState.remainingActions - 1

        _battleState.update { state ->
            state.copy(
                threat = updatedThreat,
                serverHp = newHp,
                shield = newShield,
                isThreatStunned = if (isEffective) true else state.isThreatStunned,
                remainingActions = nextActions,
                selectedLayer = null,
                isOverkillMenuOpen = false
            )
        }

        // 敵撃破判定
        if (newThreatHp <= 0) {
            addChatMessage(
                ChatSenderType.AI,
                "作戦完了",
                "敵インシデントの完全沈静化を確認！防衛任務完了。",
                isImportant = true
            )
            _battleState.update { it.copy(phase = BattlePhase.VICTORY) }
            onVictory()
            return
        }

        if (nextActions <= 0) {
            endTurn()
        }
    }

    // === ターン終了 & 敵の反撃 ===

    fun endTurn() {
        val currentState = _battleState.value
        if (currentState.phase != BattlePhase.IN_BATTLE) return

        val incident = currentState.currentIncident

        // 敵がスタンしている場合
        if (currentState.isThreatStunned) {
            addChatMessage(
                ChatSenderType.ALERT,
                "敵状態",
                "敵はスタン中のため反撃不能。（スタン解除）"
            )

            // UTMのシールド自動修復
            var nextShield = currentState.shield
            val utmRegen = _gameProgress.value.utmRegenAmount
            if (utmRegen > 0) {
                nextShield = (nextShield + utmRegen).coerceAtMost(currentState.maxShield)
                addChatMessage(ChatSenderType.ACTION, "UTM自動修復", "次世代UTMによりシールドが自動で +$utmRegen 修復。")
            }

            val nextAttackIdx = (currentState.nextAttackIndex + 1) % currentState.threat.attackIncidentIds.size
            val nextIncidentId = currentState.threat.attackIncidentIds[nextAttackIdx]
            val nextIncident = IncidentCatalog.getIncident(nextIncidentId)

            _battleState.update { state ->
                state.copy(
                    turn = state.turn + 1,
                    remainingActions = state.maxActionsPerTurn,
                    isThreatStunned = false,
                    shield = nextShield,
                    currentIncident = nextIncident,
                    nextAttackIndex = nextAttackIdx,
                    selectedLayer = null,
                    isOverkillMenuOpen = false
                )
            }

            deliverNextTurnLogs(nextIncident)
            return
        }

        // 敵の反撃直撃
        addChatMessage(
            ChatSenderType.ALERT,
            "敵の反撃直撃",
            "敵の攻撃: 【${incident.name}】が直撃！\n${incident.executedMessage}",
            isImportant = true
        )

        // 被弾したインシデントを図鑑に登録
        recordIncidentToLogBook(incident, currentState.threat.name)

        var incomingDamage = incident.damage
        val cdnReduction = _gameProgress.value.cdnDamageReduction
        if (cdnReduction > 0 && (incident.layer == OsiLayer.L3_NETWORK || incident.layer == OsiLayer.L4_TRANSPORT || incident.layer == OsiLayer.L7_APPLICATION)) {
            incomingDamage = (incomingDamage - cdnReduction).coerceAtLeast(5)
            addChatMessage(ChatSenderType.ACTION, "CDN防御網", "全社CDNのキャッシュ分散により負荷を -$cdnReduction 軽減。")
        }

        var currentShield = currentState.shield
        var currentHp = currentState.serverHp

        if (incident.piercesShield) {
            currentHp -= incomingDamage
            addChatMessage(ChatSenderType.ALERT, "シールド貫通", "シールドを無視してサーバ本体に $incomingDamage ダメージ！")
        } else {
            if (currentShield >= incomingDamage) {
                currentShield -= incomingDamage
                addChatMessage(ChatSenderType.ALERT, "シールド防衛", "シールドが $incomingDamage 吸収。（残シールド: $currentShield）")
            } else {
                val remaining = incomingDamage - currentShield
                currentShield = 0
                currentHp -= remaining
                addChatMessage(ChatSenderType.ALERT, "シールド損壊", "シールド破壊！サーバ本体に $remaining ダメージ！（残HP: $currentHp）")
            }
        }

        // UTM自動修復
        val utmRegen = _gameProgress.value.utmRegenAmount
        if (utmRegen > 0 && currentHp > 0) {
            currentShield = (currentShield + utmRegen).coerceAtMost(currentState.maxShield)
            addChatMessage(ChatSenderType.ACTION, "UTM自動修復", "次世代UTMによりシールドが自動で +$utmRegen 修復。")
        }

        if (currentHp <= 0) {
            currentHp = 0
            addChatMessage(
                ChatSenderType.ALERT,
                "サーバ停止",
                "サーバ耐久力がゼロになりました。社内業務完全停止、防衛作戦失敗。",
                isImportant = true
            )
            _battleState.update { it.copy(serverHp = 0, shield = currentShield, phase = BattlePhase.DEFEAT) }
            return
        }

        val nextAttackIdx = (currentState.nextAttackIndex + 1) % currentState.threat.attackIncidentIds.size
        val nextIncidentId = currentState.threat.attackIncidentIds[nextAttackIdx]
        val nextIncident = IncidentCatalog.getIncident(nextIncidentId)

        _battleState.update { state ->
            state.copy(
                serverHp = currentHp,
                shield = currentShield,
                turn = state.turn + 1,
                remainingActions = state.maxActionsPerTurn,
                currentIncident = nextIncident,
                nextAttackIndex = nextAttackIdx,
                selectedLayer = null,
                isOverkillMenuOpen = false
            )
        }

        deliverNextTurnLogs(nextIncident)
    }

    private fun deliverNextTurnLogs(nextIncident: IncidentMaster) {
        val progress = _gameProgress.value
        if (progress.isSiemOwned) {
            addChatMessage(
                ChatSenderType.SIEM,
                "SIEM監視コンソール",
                "【次の一手予兆】${nextIncident.deviceSource}: ${nextIncident.warningMessage}"
            )
        } else {
            addChatMessage(
                ChatSenderType.ALERT,
                nextIncident.deviceSource,
                "攻撃兆候: ${nextIncident.warningMessage}"
            )
        }

        if (progress.canAiSuggestEffectiveAction) {
            val effectiveAction = DefenseActionCatalog.allActions.find { it.id == nextIncident.effectiveActionId }
            var aiText = "【AI解析】次の有効対策は【${effectiveAction?.name}】です。"
            if (progress.canAiWarnTaboo && nextIncident.tabooActionId != null) {
                val tabooAction = DefenseActionCatalog.allActions.find { it.id == nextIncident.tabooActionId }
                aiText += "\n※警告: 【${tabooAction?.name}】は禁忌肢です！"
            }
            addChatMessage(ChatSenderType.AI, "情シスAI", aiText, isImportant = true)
        }
    }

    // === 図鑑登録 ===

    private fun recordIncidentToLogBook(incident: IncidentMaster, threatName: String) {
        val existing = _gameProgress.value.incidentLogBook.any { it.incidentId == incident.id }
        if (!existing) {
            val effectiveAction = DefenseActionCatalog.allActions.find { it.id == incident.effectiveActionId }
            val tabooAction = DefenseActionCatalog.allActions.find { it.id == incident.tabooActionId }

            val entry = IncidentLogEntry(
                incidentId = incident.id,
                attackName = incident.name,
                threatName = threatName,
                layer = incident.layer,
                damage = incident.damage,
                effectiveActionName = effectiveAction?.name ?: "未特定",
                effectiveReason = incident.effectiveReason,
                tabooActionName = tabooAction?.name,
                examTip = incident.examTip
            )
            _gameProgress.update { it.copy(incidentLogBook = it.incidentLogBook + entry) }
            addChatMessage(
                ChatSenderType.AI,
                "図鑑記録完了",
                "攻撃手口『${incident.name}』の詳細データをセキュリティ図鑑（ログ）に記録しました。"
            )
        }
    }

    // === 勝利時処理 (アンロック・予算付与・SIEMチュートリアル) ===

    private fun onVictory() {
        val stageId = _currentStageId.value
        val state = _battleState.value
        val reward = state.rewardBudgetAmount

        _gameProgress.update { prog ->
            val newCleared = prog.clearedStageIds + stageId
            val newUnlockedLayers = prog.unlockedLayers.toMutableSet()
            when (stageId) {
                1 -> newUnlockedLayers.add(OsiLayer.L2_DATALINK)
                2 -> newUnlockedLayers.add(OsiLayer.L3_NETWORK)
                3 -> newUnlockedLayers.add(OsiLayer.L4_TRANSPORT)
                4 -> newUnlockedLayers.add(OsiLayer.L5_SESSION)
                5 -> newUnlockedLayers.add(OsiLayer.L6_PRESENTATION)
                6 -> newUnlockedLayers.add(OsiLayer.L7_APPLICATION)
                7 -> newUnlockedLayers.addAll(OsiLayer.values())
            }

            prog.copy(
                clearedStageIds = newCleared,
                unlockedLayers = newUnlockedLayers,
                departmentBudget = prog.departmentBudget + reward
            )
        }

        // 第1話クリア時、SIEM未所持ならショップ購入チュートリアルを起動
        if (stageId == 1 && !_gameProgress.value.isSiemOwned && !_gameProgress.value.isSiemTutorialShown) {
            _showSiemTutorial.value = true
        }
    }

    // === 設備投資 (7段階アップグレード購入) ===

    fun purchaseFacilityUpgrade(type: FacilityType): Boolean {
        val prog = _gameProgress.value
        val currentLvl = prog.getLevel(type)
        if (currentLvl >= type.maxLevel) return false

        val cost = prog.getUpgradeCost(type)
        if (prog.departmentBudget < cost) return false

        val newLevels = prog.facilityLevels.toMutableMap()
        newLevels[type] = currentLvl + 1

        _gameProgress.update {
            it.copy(
                departmentBudget = it.departmentBudget - cost,
                facilityLevels = newLevels
            )
        }
        return true
    }

    // === 対処コマンド購入 ===

    fun purchaseActionCommand(action: DefenseAction): Boolean {
        val prog = _gameProgress.value
        if (prog.unlockedActionIds.contains(action.id)) return false
        if (prog.departmentBudget < action.purchaseCost) return false

        _gameProgress.update {
            it.copy(
                departmentBudget = it.departmentBudget - action.purchaseCost,
                unlockedActionIds = it.unlockedActionIds + action.id
            )
        }
        return true
    }

    private fun addChatMessage(type: ChatSenderType, speaker: String, message: String, isImportant: Boolean = false) {
        _battleState.update { state ->
            val newMsg = ChatMessage(senderType = type, speaker = speaker, message = message, isImportant = isImportant)
            state.copy(messages = state.messages + newMsg)
        }
    }
}
