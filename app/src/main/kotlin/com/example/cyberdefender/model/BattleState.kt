package com.example.cyberdefender.model

enum class LogType {
    INFO,
    WARNING,
    ALERT,
    SUCCESS,
    CRITICAL
}

data class BattleLog(
    val id: Long = System.currentTimeMillis() + (0..999).random(),
    val sender: String,
    val message: String,
    val type: LogType
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
    val ap: Int = 4,
    val maxAp: Int = 5,
    val threat: Threat,
    val nextAttackIndex: Int = 0,
    val turn: Int = 1,
    val isThreatStunned: Boolean = false,
    val businessImpact: Int = 0, // 業務影響度 (0%〜100%)
    val latestComplaint: String? = null,
    val complaintSpeaker: String? = null,
    val isOverkillUsed: Boolean = false,
    val phase: BattlePhase = BattlePhase.IN_BATTLE,
    val logs: List<BattleLog> = emptyList(),
    val lastEducationalMessage: String? = null
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

    val bonusAmountText: String
        get() = when {
            serverHp <= 0 -> "¥0 (ボーナスカット + 損害賠償検討)"
            isOverkillUsed -> "¥3,000 (激怒の内線殺到・ほぼ全額没収)"
            businessImpact > 30 -> "¥120,000 (業務影響分の査定マイナス)"
            serverHp >= 80 && businessImpact == 0 -> "¥500,000 (満額支給！伝説の情シス！)"
            else -> "¥380,000 (通常満額支給)"
        }
}
