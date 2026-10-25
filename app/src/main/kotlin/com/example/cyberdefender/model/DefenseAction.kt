package com.example.cyberdefender.model

enum class OsiLayer(val code: String, val layerNumber: Int, val japaneseLabel: String) {
    L7_APPLICATION("ア", 7, "アプリケーション層"),
    L6_PRESENTATION("プ", 6, "プレゼンテーション層"),
    L5_SESSION("セ", 5, "セッション層"),
    L4_TRANSPORT("ト", 4, "トランスポート層"),
    L3_NETWORK("ネ", 3, "ネットワーク層"),
    L2_DATALINK("デ", 2, "データリンク層"),
    L1_PHYSICAL("ブ", 1, "物理層")
}

enum class ActionId {
    L7_WAF_PARAM_BIND,        // L7(ア): WAF & 入力プレースホルダー
    L6_TLS_ENCODE_FIX,        // L6(プ): 暗号化通信 & 文字コード検証
    L5_SESSION_SECURE_COOKIE, // L5(セ): セッション管理 & HttpOnly Cookie
    L4_SYN_COOKIE_PORT_CLOSE, // L4(ト): SYN Cookie & ポートフィルタ
    L3_IP_FILTERING,          // L3(ネ): ルータIPスプーフィング破棄
    L2_ARP_SNOOPING,          // L2(デ): スイッチARPスヌーピング
    L1_LOCK_SERVER_ROOM,      // L1(ブ): サーバルーム施錠 & USB無効化
    OVERKILL_CUT_MAIN_CABLE,  // 【過剰防衛】全社光回線引っこ抜き
    OVERKILL_FORCE_SHUTDOWN,  // 【過剰防衛】全社PC強制電源断
    RESTORE_FROM_TAPE         // L1/L7: 磁気テープバックアップから復元 (HP回復)
}

data class DefenseAction(
    val id: ActionId,
    val name: String,
    val commandCode: String,
    val layer: OsiLayer,
    val apCost: Int,
    val baseDamage: Int = 0,
    val baseShield: Int = 0,
    val healAmount: Int = 0,
    val isOverkill: Boolean = false,
    val businessImpact: Int = 0, // 業務影響度 (0%〜100%)
    val complaintOnUse: String? = null,
    val description: String,
    val securityConcept: String
)

object DefenseActionCatalog {
    val allActions = listOf(
        DefenseAction(
            id = ActionId.L7_WAF_PARAM_BIND,
            name = "[ア] WAF適用 & 入力バインド",
            commandCode = "waf --block-sqli --rule=param_bind",
            layer = OsiLayer.L7_APPLICATION,
            apCost = 2,
            baseDamage = 45,
            businessImpact = 0,
            description = "L7(ア)対策。入力値をプレースホルダー化し、SQL構文改ざんやXSSを無効化。",
            securityConcept = "【ア: アプリケーション層】HTTPやSQLなどユーザーと直結する層。動的クエリではなく静的バインドを用いるのがWeb防御の鉄則。"
        ),
        DefenseAction(
            id = ActionId.L6_TLS_ENCODE_FIX,
            name = "[プ] TLS暗号化 & 文字コード検証",
            commandCode = "ssl_config --enforce-tls1.3 --sanitize-encoding",
            layer = OsiLayer.L6_PRESENTATION,
            apCost = 2,
            baseDamage = 45,
            businessImpact = 0,
            description = "L6(プ)対策。通信データをTLSで暗号化し、文字コード偽装によるすり抜けを防止。",
            securityConcept = "【プ: プレゼンテーション層】データの形式（暗号化、圧縮、文字コード）を扱う層。ダウングレード攻撃やエンコード偽装を検知・正規化する。"
        ),
        DefenseAction(
            id = ActionId.L5_SESSION_SECURE_COOKIE,
            name = "[セ] セッションCookie厳格化",
            commandCode = "session.setFlags(HttpOnly, Secure, SameSite=Strict)",
            layer = OsiLayer.L5_SESSION,
            apCost = 2,
            baseDamage = 45,
            businessImpact = 0,
            description = "L5(セ)対策。通信の開始から終了（対話）を管理し、セッション乗っ取りを阻止。",
            securityConcept = "【セ: セッション層】通信プロセスの確立・維持・切断を管理。Cookieの不正読み取り防止や一定時間でのタイムアウトでセッションジャックを防ぐ。"
        ),
        DefenseAction(
            id = ActionId.L4_SYN_COOKIE_PORT_CLOSE,
            name = "[ト] SYN Cookie & ポート閉塞",
            commandCode = "sysctl -w net.ipv4.tcp_syncookies=1",
            layer = OsiLayer.L4_TRANSPORT,
            apCost = 2,
            baseDamage = 45,
            businessImpact = 0,
            description = "L4(ト)対策。TCP接続リソース枯渇（SYN Flood）を防ぎ、不要ポートを遮断。",
            securityConcept = "【ト: トランスポート層】TCP/UDPによる信頼性のあるデータ転送を担う層。SYN Floodには接続状態を保持しないSYN Cookie技術が極めて有効。"
        ),
        DefenseAction(
            id = ActionId.L3_IP_FILTERING,
            name = "[ネ] ルータ送信元IPフィルタリング",
            commandCode = "ip route add blackhole 198.51.100.0/24",
            layer = OsiLayer.L3_NETWORK,
            apCost = 2,
            baseDamage = 45,
            businessImpact = 0,
            description = "L3(ネ)対策。IPアドレスに基づき、偽装パケットや巨大Pingパケットをルータで破棄。",
            securityConcept = "【ネ: ネットワーク層】エンドツーエンドのパケットルーティングを担う層（IP、ICMP）。ルータのACLで送信元偽装IPや不正サブネットを弾く。"
        ),
        DefenseAction(
            id = ActionId.L2_ARP_SNOOPING,
            name = "[デ] ARPスヌーピング & MAC固定",
            commandCode = "switch(config)# ip arp inspection vlan 1",
            layer = OsiLayer.L2_DATALINK,
            apCost = 2,
            baseDamage = 45,
            businessImpact = 0,
            description = "L2(デ)対策。同一LAN内のMACアドレス偽装やARPポイズニングによる盗聴を防止。",
            securityConcept = "【デ: データリンク層】同一ネットワーク（LAN）内のMACアドレスによる直接通信。スイッチでARP応答を監視し、ルータのフリをする盗聴端末を遮断する。"
        ),
        DefenseAction(
            id = ActionId.L1_LOCK_SERVER_ROOM,
            name = "[ブ] サーバルーム施錠 & USB無効化",
            commandCode = "physical_security --lock-all --block-usb-storage",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 2,
            baseDamage = 45,
            businessImpact = 0,
            description = "L1(ブ)対策。ケーブル・機器・USB端子など物理的な接触・持ち出し・拾い挿しを阻止。",
            securityConcept = "【ブ: 物理層】ケーブル、端子、電気信号など目に見える物理世界。どんな強力な暗号も、サーバルームに侵入されたりUSBを直挿しされたら突破される。"
        ),
        DefenseAction(
            id = ActionId.RESTORE_FROM_TAPE,
            name = "磁気テープからサーバー健全性復元",
            commandCode = "lto_tape.restore_partition(\"/srv/core\")",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 2,
            healAmount = 35,
            businessImpact = 0,
            description = "オフラインのLTO磁気テープから整合性を修復。HPを35回復。",
            securityConcept = "テープバックアップ（L1/物理的エアギャップ）：ネットワークから物理的に切り離された媒体は、マルウェアが物理法則上アクセス不能。"
        ),
        DefenseAction(
            id = ActionId.OVERKILL_CUT_MAIN_CABLE,
            name = "【過剰防衛】全社光回線LANケーブル引っこ抜き",
            commandCode = "echo '引っこ抜き完了！全通信遮断！'",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 3,
            baseDamage = 999,
            isOverkill = true,
            businessImpact = 100,
            complaintOnUse = "営業部長「おい情シス！客と億単位のビデオ会議中だったんだぞ！回線抜く奴があるかバカモン！」",
            description = "物理的に全通信を即座に遮断！敵は100%死滅するが、全社業務が完全崩壊する！",
            securityConcept = "【過剰防衛の典型例】セキュリティは「事業を継続するため」にあるもの。業務そのものを止めてしまっては本末転倒となり、社内クレームでクビ寸前に。"
        ),
        DefenseAction(
            id = ActionId.OVERKILL_FORCE_SHUTDOWN,
            name = "【過剰防衛】社内全PC一括リモート強制電源OFF",
            commandCode = "shutdown /s /m \\* /f /t 0",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 3,
            baseDamage = 999,
            isOverkill = true,
            businessImpact = 85,
            complaintOnUse = "経理部長「ちょっと情シス！今日給与振込日なのよ！保存してないエクセル消えたじゃないの！」",
            description = "社内全PCを強制終了！感染拡散は止まるが、保存前の重要文書が全滅して大炎上！",
            securityConcept = "【過剰防衛の典型例】端末の電源強制断はファイル破損の危険大。まずは該当端末のみを論理的にネットワーク隔離するのが正しいインシデント初動対応。"
        )
    )
}
