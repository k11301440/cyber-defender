package com.example.cyberdefender.model

enum class ChatSenderType(val icon: String, val label: String) {
    PHONE("📞", "内線着信"),
    EMAIL("✉️", "社内通報メール"),
    AI("🤖", "情シスAI ヘルプデスク君"),
    ALERT("🚨", "システム監視IDS/IPS"),
    ACTION("🛡️", "情シス防護コマンド"),
    COMPLAINT("💢", "激怒の内線")
}

data class ChatMessage(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val senderType: ChatSenderType,
    val speaker: String,
    val message: String,
    val timestamp: String = "17:00",
    val isImportant: Boolean = false
)

enum class BattlePhase {
    IN_BATTLE,
    VICTORY,
    DEFEAT
}

data class BattleState(
    val serverHp: Int = 100,
    val maxServerHp: Int = 100,
    val shield: Int = 20,
    val maxShield: Int = 100,
    val turn: Int = 1,
    val remainingActions: Int = 1, // 当該ターンの残り手数 (人員増員で2手)
    val maxActionsPerTurn: Int = 1,
    val threat: Threat,
    val nextAttackIndex: Int = 0,
    val isThreatStunned: Boolean = false,
    val businessImpact: Int = 0, // 業務影響度 (0%〜100%)
    val isOverkillUsed: Boolean = false,
    val phase: BattlePhase = BattlePhase.IN_BATTLE,
    val messages: List<ChatMessage> = emptyList(), // チャット画面風タイムライン
    val selectedLayer: OsiLayer? = null, // 2ステップ選択: 現在選択中の層
    val isOverkillMenuOpen: Boolean = false, // 2ステップ選択: 最終手段メニュー
    val remainingTimeSeconds: Int? = null, // エンドコンテンツ用制限時間
    val earnedBudget: Int = 0 // クリア時に付与される部署予算
) {
    val currentAttack: ThreatAttack
        get() = threat.attackList[nextAttackIndex % threat.attackList.size]

    // 評価ランクの算出
    val resultRank: String
        get() = when {
            serverHp <= 0 -> "F (解雇処分・始末書)"
            isOverkillUsed -> "C (過剰防衛・大クレーム)"
            businessImpact > 30 -> "B (業務遅延・減給)"
            serverHp >= 80 && businessImpact == 0 -> "S (神対応・社長賞！)"
            else -> "A (優秀防衛・ボーナス支給)"
        }

    val rewardBudgetAmount: Int
        get() = when {
            serverHp <= 0 -> 0
            isOverkillUsed -> 10000
            businessImpact > 30 -> 150000
            serverHp >= 80 && businessImpact == 0 -> 500000
            else -> 350000
        }

    val bonusAmountText: String
        get() = when {
            serverHp <= 0 -> "¥0 (ボーナスカット + 損害賠償検討)"
            isOverkillUsed -> "¥10,000 (激怒の内線殺到・ほぼ全額没収)"
            businessImpact > 30 -> "¥150,000 (業務影響分の査定マイナス)"
            serverHp >= 80 && businessImpact == 0 -> "¥500,000 (満額支給！伝説の情シス！)"
            else -> "¥350,000 (通常満額支給)"
        }
}
