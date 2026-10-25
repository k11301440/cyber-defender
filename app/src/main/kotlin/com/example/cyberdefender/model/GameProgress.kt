package com.example.cyberdefender.model

/**
 * 部署設備投資（アップグレード）の種類
 */
enum class UpgradeType(
    val id: String,
    val title: String,
    val cost: Int,
    val icon: String,
    val shortDesc: String,
    val detailedDesc: String
) {
    EXTRA_STAFF(
        id = "extra_staff",
        title = "情シス人員の増員 (副担当配属)",
        cost = 500000,
        icon = "👥",
        shortDesc = "1ターンに2手の防御コマンドが実行可能になる！",
        detailedDesc = "優秀な派遣エンジニアを情シスに配属。1ターンに実行できる防御アクションが2回になり、一度に弱点対策とシールド展開の両立などが可能に。"
    ),
    AI_UPGRADE(
        id = "ai_upgrade",
        title = "情シス支援AI「ヘルプデスク君v2.0」換装",
        cost = 300000,
        icon = "🤖",
        shortDesc = "敵の攻撃層（アプセトネデブ）を高精度で即時分析！",
        detailedDesc = "社内AIアシスタントを最新LLM基盤に換装。敵の攻撃予兆から「どのアプセトネデブ層が狙われているか」をチャットでより的確に示唆してくれる。"
    ),
    ENTERPRISE_CDN(
        id = "enterprise_cdn",
        title = "全社CDN (コンテンツ配信網) 導入",
        cost = 450000,
        icon = "🌐",
        shortDesc = "サーバ最大HP +30 & L3/L4/L7のダメージを常時軽減！",
        detailedDesc = "世界中にエッジサーバを展開し、社内公開サービスへのトラフィックを分散。サーバ耐久力が大幅向上し、フラッド系やインジェクション攻撃の負荷を緩和。"
    ),
    UTM_FIREWALL(
        id = "utm_firewall",
        title = "次世代UTM (統合脅威管理ルータ) 導入",
        cost = 400000,
        icon = "🛡️",
        shortDesc = "シールド最大値 +30 & 毎ターンシールド自動修復(+10)",
        detailedDesc = "ゲートウェイにAI搭載ファイアウォールを配備。シールド容量が拡張され、ターン終了時にファイアウォール定義が自動再生成されてシールドが回復。"
    )
}

/**
 * 食らった（被弾した）攻撃インシデントログ（図鑑データ）
 */
data class IncidentLogEntry(
    val attackName: String,
    val threatName: String,
    val layer: OsiLayer,
    val damage: Int,
    val mechanism: String,
    val defensePrinciple: String,
    val examTip: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * ゲーム全体の永続進行状況
 */
data class GameProgress(
    val clearedStageIds: Set<Int> = emptySet(),
    val unlockedLayers: Set<OsiLayer> = setOf(OsiLayer.L1_PHYSICAL), // 初期は第1話(L1)のみ
    val departmentBudget: Int = 100000, // 初期配属予算 10万円
    val purchasedUpgrades: Set<UpgradeType> = emptySet(),
    val incidentLogBook: List<IncidentLogEntry> = emptyList() // 遭遇・被弾したインシデント図鑑
) {
    /**
     * 次に挑戦すべき話のID（未クリアの最小ステージ、全クリア時は1）
     */
    val nextStoryStageId: Int
        get() {
            for (id in 1..7) {
                if (!clearedStageIds.contains(id)) return id
            }
            return 8 // エンドコンテンツ
        }

    val isAllStoryCleared: Boolean
        get() = (1..7).all { clearedStageIds.contains(it) }

    val hasExtraStaff: Boolean
        get() = purchasedUpgrades.contains(UpgradeType.EXTRA_STAFF)

    val hasAiUpgrade: Boolean
        get() = purchasedUpgrades.contains(UpgradeType.AI_UPGRADE)

    val hasCdn: Boolean
        get() = purchasedUpgrades.contains(UpgradeType.ENTERPRISE_CDN)

    val hasUtm: Boolean
        get() = purchasedUpgrades.contains(UpgradeType.UTM_FIREWALL)
}
