package com.example.cyberdefender.model

/**
 * 設備投資の項目ID
 */
enum class FacilityType(
    val id: String,
    val title: String,
    val initialLevel: Int, // 初期レベル (SIEMは0で未所持)
    val maxLevel: Int = 7,
    val baseCost: Int,
    val shortDesc: String,
    val detailedDesc: String
) {
    SIEM_CONSOLE(
        id = "siem_console",
        title = "SIEM統合ログ監視コンソール",
        initialLevel = 0, // 初期は未所持！第1話クリア後に購入チュートリアル
        maxLevel = 7,
        baseCost = 150000,
        shortDesc = "社内機器からの侵入ログ・攻撃予兆をリアルタイム検知！",
        detailedDesc = "Lv1でSIEMが稼働開始。レベル上昇に伴い、ログの精度向上・予兆の早期受信が可能に。"
    ),
    IT_STAFF(
        id = "it_staff",
        title = "情シス人員の配属 (チーム増員)",
        initialLevel = 1,
        maxLevel = 7,
        baseCost = 200000,
        shortDesc = "Lv3以上で1ターンに2手の防御コマンドが実行可能！",
        detailedDesc = "情シスメンバーを増員。Lv3で2手行動が解禁され、高レベルで行動効率が向上。"
    ),
    AI_ASSISTANT(
        id = "ai_assistant",
        title = "情シス支援AI「ヘルプデスク君」換装",
        initialLevel = 1,
        maxLevel = 7,
        baseCost = 120000,
        shortDesc = "Lv2以上で敵攻撃の【有効な対策】をサジェスト！",
        detailedDesc = "Lv2で有効対策サジェスト機能が解禁。Lv4以上で【禁忌肢の警告】も追加。"
    ),
    ENTERPRISE_CDN(
        id = "enterprise_cdn",
        title = "全社CDN (コンテンツ配信網) 導入",
        initialLevel = 0,
        maxLevel = 7,
        baseCost = 180000,
        shortDesc = "サーバ最大HP増加 & L3/L4/L7のダメージを常時軽減！",
        detailedDesc = "エッジサーバによる負荷分散。レベルごとに最大HP+10、被ダメージ軽減率が向上。"
    ),
    UTM_FIREWALL(
        id = "utm_firewall",
        title = "次世代UTM (統合脅威管理) 導入",
        initialLevel = 0,
        maxLevel = 7,
        baseCost = 180000,
        shortDesc = "最大シールド拡張 & 毎ターンシールド自動修復！",
        detailedDesc = "ゲートウェイでの一元防御。レベルごとに最大シールド+10、ターン毎修復量が増加。"
    ),
    BACKUP_TAPE_SYSTEM(
        id = "backup_tape",
        title = "オフラインLTO磁気テープ保管庫",
        initialLevel = 1,
        maxLevel = 7,
        baseCost = 150000,
        shortDesc = "磁気テープからのサーバ整合性復旧量(HP回復)が向上！",
        detailedDesc = "エアギャップバックアップの強化。レベルごとに復旧コマンドの回復量が向上。"
    ),
    STAFF_SECURITY_TRAINING(
        id = "security_training",
        title = "全社員セキュリティ教育・啓蒙",
        initialLevel = 1,
        maxLevel = 7,
        baseCost = 100000,
        shortDesc = "業務影響度(クレーム指数)の増加を抑制 & 査定ペナルティ緩和！",
        detailedDesc = "社員のリテラシー向上。インシデント発生時の社内パニックや過剰防衛ペナルティを緩和。"
    ),
    PACKET_ANALYZER(
        id = "packet_analyzer",
        title = "リアルタイムパケットアナライザ",
        initialLevel = 1,
        maxLevel = 7,
        baseCost = 100000,
        shortDesc = "被弾した攻撃の内部構造・試験Tipsを図鑑に自動解析！",
        detailedDesc = "パケットキャプチャと統計解析。図鑑（ログ）の即時解析と暗記を強力にアシスト。"
    )
}

/**
 * 食らった（被弾した）攻撃インシデントログ（図鑑データ）
 */
data class IncidentLogEntry(
    val incidentId: Int,
    val attackName: String,
    val threatName: String,
    val layer: OsiLayer,
    val damage: Int,
    val effectiveActionName: String,
    val effectiveReason: String,
    val tabooActionName: String?,
    val examTip: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * ゲーム全体の進行状況データ
 */
data class GameProgress(
    val clearedStageIds: Set<Int> = emptySet(),
    val unlockedLayers: Set<OsiLayer> = setOf(OsiLayer.L1_PHYSICAL), // 最初は物理層(L1)のみ
    val departmentBudget: Int = 200000, // 初期配属予算 20万円
    val facilityLevels: Map<FacilityType, Int> = FacilityType.values().associateWith { it.initialLevel },
    val unlockedActionIds: Set<ActionId> = emptySet(), // ショップで購入した拡張コマンド
    val incidentLogBook: List<IncidentLogEntry> = emptyList(), // 被弾・遭遇した攻撃ログ図鑑
    val isSiemTutorialShown: Boolean = false, // SIEM購入チュートリアル表示フラグ
    val isPrologueStorySeen: Boolean = false  // プロローグ会話イベント閲覧フラグ
) {
    fun getLevel(type: FacilityType): Int = facilityLevels[type] ?: type.initialLevel

    fun getUpgradeCost(type: FacilityType): Int {
        val currentLvl = getLevel(type)
        if (currentLvl >= type.maxLevel) return 0
        return (type.baseCost * (1.0 + currentLvl * 0.45)).toInt()
    }

    val isSiemOwned: Boolean
        get() = getLevel(FacilityType.SIEM_CONSOLE) >= 1

    val nextStoryStageId: Int
        get() {
            for (id in 1..7) {
                if (!clearedStageIds.contains(id)) return id
            }
            return 8 // エンドコンテンツ
        }

    val isAllStoryCleared: Boolean
        get() = (1..7).all { clearedStageIds.contains(it) }

    // 人員Lv3以上で1ターン2手行動
    val canTakeTwoActions: Boolean
        get() = getLevel(FacilityType.IT_STAFF) >= 3

    // AI Lv2以上で有効対策サジェスト機能
    val canAiSuggestEffectiveAction: Boolean
        get() = getLevel(FacilityType.AI_ASSISTANT) >= 2

    // AI Lv4以上で禁忌肢警告
    val canAiWarnTaboo: Boolean
        get() = getLevel(FacilityType.AI_ASSISTANT) >= 4

    val cdnBonusHp: Int
        get() = getLevel(FacilityType.ENTERPRISE_CDN) * 10

    val cdnDamageReduction: Int
        get() = getLevel(FacilityType.ENTERPRISE_CDN) * 3

    val utmBonusShield: Int
        get() = getLevel(FacilityType.UTM_FIREWALL) * 10

    val utmRegenAmount: Int
        get() = getLevel(FacilityType.UTM_FIREWALL) * 5
}
