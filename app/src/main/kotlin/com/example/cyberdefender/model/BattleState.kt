package com.example.cyberdefender.model

enum class ChatSenderType(val tag: String, val speakerName: String) {
    SYSTEM("[システム]", "社内システム勤怠管理"),
    PHONE("[内線]", "内線着信 (社内通報)"),
    EMAIL("[メール]", "全社通知メール"),
    AI("[AI]", "情シス支援AI ヘルプデスク君"),
    SIEM("[SIEM]", "SIEM統合監視コンソール"),
    ALERT("[警告]", "機器監視アラート"),
    ACTION("[実行]", "情シス防護コマンド"),
    COMPLAINT("[クレーム]", "怒りの内線")
}

data class ChatMessage(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val senderType: ChatSenderType,
    val speaker: String,
    val message: String,
    val timestamp: String = "09:00",
    val isImportant: Boolean = false
)

enum class BattlePhase {
    IN_BATTLE,
    VICTORY,
    DEFEAT,
    TABOO_GAME_OVER // 禁忌肢による即ゲームオーバー
}

data class BattleState(
    val serverHp: Int = 100,
    val maxServerHp: Int = 100,
    val shield: Int = 20,
    val maxShield: Int = 100,
    val turn: Int = 1,
    val remainingActions: Int = 1, // 当該ターンの残り手数
    val maxActionsPerTurn: Int = 1,
    val threat: Threat,
    val currentIncident: IncidentMaster, // 現在飛来している攻撃（100種から）
    val nextAttackIndex: Int = 0,
    val isThreatStunned: Boolean = false,
    val businessImpact: Int = 0, // 業務影響度 (0%〜100%)
    val isOverkillUsed: Boolean = false,
    val tabooGameOverMessage: String? = null,
    val phase: BattlePhase = BattlePhase.IN_BATTLE,
    val messages: List<ChatMessage> = emptyList(), // チャット風タイムライン
    val selectedLayer: OsiLayer? = null, // 2ステップ選択: 現在選択中の層
    val isOverkillMenuOpen: Boolean = false,
    val remainingTimeSeconds: Int? = null,
    val earnedBudget: Int = 0
) {
    val resultRank: String
        get() = when {
            phase == BattlePhase.TABOO_GAME_OVER -> "F- (禁忌肢実行・即日解雇)"
            serverHp <= 0 -> "F (解雇処分・始末書提出)"
            isOverkillUsed -> "C (過剰防衛・全社クレーム炎上)"
            businessImpact > 30 -> "B (業務遅延・ボーナス減額)"
            serverHp >= 80 && businessImpact == 0 -> "S (神対応・社長賞表彰)"
            else -> "A (優秀防衛・規定賞与支給)"
        }

    val rewardBudgetAmount: Int
        get() = when {
            phase == BattlePhase.TABOO_GAME_OVER -> 0
            serverHp <= 0 -> 0
            isOverkillUsed -> 10000
            businessImpact > 30 -> 150000
            serverHp >= 80 && businessImpact == 0 -> 500000
            else -> 350000
        }

    val bonusAmountText: String
        get() = when {
            phase == BattlePhase.TABOO_GAME_OVER -> "0円 (懲戒解雇・損害賠償請求)"
            serverHp <= 0 -> "0円 (全額カット・始末書)"
            isOverkillUsed -> "10,000円 (クレーム相殺・ほぼ没収)"
            businessImpact > 30 -> "150,000円 (業務遅延ペナルティ控除)"
            serverHp >= 80 && businessImpact == 0 -> "500,000円 (満額＋社長特別賞与)"
            else -> "350,000円 (規定満額支給)"
        }
}
