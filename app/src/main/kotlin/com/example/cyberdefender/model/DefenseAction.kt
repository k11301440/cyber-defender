package com.example.cyberdefender.model

enum class OsiLayer(
    val code: String,
    val layerNumber: Int,
    val japaneseLabel: String,
    val shortName: String
) {
    L7_APPLICATION("ア", 7, "アプリケーション層", "ア: L7 アプリ"),
    L6_PRESENTATION("プ", 6, "プレゼンテーション層", "プ: L6 プレゼン"),
    L5_SESSION("セ", 5, "セッション層", "セ: L5 セッション"),
    L4_TRANSPORT("ト", 4, "トランスポート層", "ト: L4 トランス"),
    L3_NETWORK("ネ", 3, "ネットワーク層", "ネ: L3 ネット"),
    L2_DATALINK("デ", 2, "データリンク層", "デ: L2 リンク"),
    L1_PHYSICAL("ブ", 1, "物理層", "ブ: L1 物理")
}

enum class ActionId {
    // L7 (ア)
    L7_WAF_PARAM_BIND,
    L7_INPUT_SANITIZE,

    // L6 (プ)
    L6_TLS_ENCODE_FIX,
    L6_CIPHER_RESTRICT,

    // L5 (セ)
    L5_SESSION_SECURE_COOKIE,
    L5_REGENERATE_SESSION_ID,

    // L4 (ト)
    L4_SYN_COOKIE,
    L4_RATE_LIMIT,

    // L3 (ネ)
    L3_BLOCK_ICMP_ROUTER,
    L3_INGRESS_FILTER,

    // L2 (デ)
    L2_ARP_SNOOPING,
    L2_PORT_SECURITY,

    // L1 (ブ)
    L1_LOCK_SERVER_ROOM,
    RESTORE_FROM_TAPE,

    // 最終手段 (過剰防衛)
    OVERKILL_CUT_MAIN_CABLE,
    OVERKILL_FORCE_SHUTDOWN
}

data class DefenseAction(
    val id: ActionId,
    val name: String,
    val commandCode: String,
    val layer: OsiLayer,
    val apCost: Int = 1,
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
        // === L7 アプリケーション層 (ア) ===
        DefenseAction(
            id = ActionId.L7_WAF_PARAM_BIND,
            name = "WAF適用 & 入力プレースホルダー",
            commandCode = "waf --block-sqli --rule=param_bind",
            layer = OsiLayer.L7_APPLICATION,
            apCost = 1,
            baseDamage = 50,
            businessImpact = 0,
            description = "入力値をSQL文から分離し、SQLインジェクションや不正クエリを無害化。",
            securityConcept = "【ア: アプリケーション層】HTTPやSQLなどを扱う最上位層。静的バインド（プレースホルダー）の徹底が根本治療。"
        ),
        DefenseAction(
            id = ActionId.L7_INPUT_SANITIZE,
            name = "Web入力サニタイズ & 特殊文字除去",
            commandCode = "sanitizer.filterHtmlSpecialChars()",
            layer = OsiLayer.L7_APPLICATION,
            apCost = 1,
            baseShield = 30,
            businessImpact = 0,
            description = "危険なタグやクォート文字を無害化し、アプリ層のシールドを+30展開。",
            securityConcept = "【ア: アプリケーション層】フォーム入力のバリデーションとエスケープ処理によりXSSやインジェクションを多層防護。"
        ),

        // === L6 プレゼンテーション層 (プ) ===
        DefenseAction(
            id = ActionId.L6_TLS_ENCODE_FIX,
            name = "TLS1.3強制 & UTF-8文字コード正規化",
            commandCode = "ssl_config --enforce-tls1.3 --sanitize-encoding",
            layer = OsiLayer.L6_PRESENTATION,
            apCost = 1,
            baseDamage = 50,
            businessImpact = 0,
            description = "文字コード不一致を解消し、0x5Cすり抜けやダウングレード攻撃を迎撃。",
            securityConcept = "【プ: プレゼンテーション層】データの表現形式（暗号化、文字コード、圧縮）を統一・管理する層。"
        ),
        DefenseAction(
            id = ActionId.L6_CIPHER_RESTRICT,
            name = "脆弱暗号スイート無効化 & HSTS",
            commandCode = "ssl.disableCiphers(RC4, 3DES, CBC)",
            layer = OsiLayer.L6_PRESENTATION,
            apCost = 1,
            baseShield = 30,
            businessImpact = 0,
            description = "安全な暗号スイートのみを許可し、シールドを+30展開。",
            securityConcept = "【プ: プレゼンテーション層】暗号化通信の強度を維持し、中間者による暗号解読を防止。"
        ),

        // === L5 セッション層 (セ) ===
        DefenseAction(
            id = ActionId.L5_SESSION_SECURE_COOKIE,
            name = "Cookie属性厳格化 (HttpOnly/Secure)",
            commandCode = "session.setFlags(HttpOnly, Secure, SameSite=Strict)",
            layer = OsiLayer.L5_SESSION,
            apCost = 1,
            baseDamage = 50,
            businessImpact = 0,
            description = "Cookieへの不正アクセスを遮断し、セッションハイジャックを阻止。",
            securityConcept = "【セ: セッション層】対話の確立・維持・終了を管理。Cookieの保護とセッション追跡防御が核心。"
        ),
        DefenseAction(
            id = ActionId.L5_REGENERATE_SESSION_ID,
            name = "全セッションID強制再採番",
            commandCode = "session.regenerateId(all=true)",
            layer = OsiLayer.L5_SESSION,
            apCost = 1,
            baseShield = 30,
            businessImpact = 0,
            description = "既存のセッション識別子を一新し、不正乗っ取り通信を弾いてシールド+30。",
            securityConcept = "【セ: セッション層】セッション固定化（Fixation）攻撃に対して、ID再発行が特効薬。"
        ),

        // === L4 トランスポート層 (ト) ===
        DefenseAction(
            id = ActionId.L4_SYN_COOKIE,
            name = "SYN Cookie有効化 & 半開接続防御",
            commandCode = "sysctl -w net.ipv4.tcp_syncookies=1",
            layer = OsiLayer.L4_TRANSPORT,
            apCost = 1,
            baseDamage = 50,
            businessImpact = 0,
            description = "メモリを消費しないSYN Cookie技術でTCP SYNフラッド攻撃を無力化。",
            securityConcept = "【ト: トランスポート層】TCP/UDPポートを管理。SYN CookieはTCPスタックのリソース枯渇を防ぐ決定打。"
        ),
        DefenseAction(
            id = ActionId.L4_RATE_LIMIT,
            name = "TCP接続レートリミット & 不要ポート遮断",
            commandCode = "iptables -A INPUT -p tcp --dport 80 -m limit --limit 50/s",
            layer = OsiLayer.L4_TRANSPORT,
            apCost = 1,
            baseShield = 30,
            businessImpact = 0,
            description = "異常なポートアクセス頻度を制限し、シールドを+30展開。",
            securityConcept = "【ト: トランスポート層】不要な待ち受けポートを閉じ、接続数を制限して帯域とキューを保護。"
        ),

        // === L3 ネットワーク層 (ネ) ===
        DefenseAction(
            id = ActionId.L3_BLOCK_ICMP_ROUTER,
            name = "ルータ巨大ICMP遮断 & 破棄",
            commandCode = "ip route add blackhole 198.51.100.0/24",
            layer = OsiLayer.L3_NETWORK,
            apCost = 1,
            baseDamage = 50,
            businessImpact = 0,
            description = "ルータのACLで不正なフラグメントPingや送信元偽装パケットを完全破棄。",
            securityConcept = "【ネ: ネットワーク層】IPアドレスによるパケット中継（ルーティング）。境界機器でのフィルタリングが基本。"
        ),
        DefenseAction(
            id = ActionId.L3_INGRESS_FILTER,
            name = "Ingressフィルタリング (送信元検証)",
            commandCode = "router(config)# ip verify unicast source reachable-via rx",
            layer = OsiLayer.L3_NETWORK,
            apCost = 1,
            baseShield = 30,
            businessImpact = 0,
            description = "到達不能な偽装送信元IPをルータで自動ドロップし、シールド+30展開。",
            securityConcept = "【ネ: ネットワーク層】送信元IP偽装（IPスプーフィング）をネットワーク境界で防止。"
        ),

        // === L2 データリンク層 (デ) ===
        DefenseAction(
            id = ActionId.L2_ARP_SNOOPING,
            name = "スイッチDynamic ARP Inspection",
            commandCode = "switch(config)# ip arp inspection vlan 1",
            layer = OsiLayer.L2_DATALINK,
            apCost = 1,
            baseDamage = 50,
            businessImpact = 0,
            description = "偽のARP応答パケットをL2スイッチで検知・破棄し、盗聴を粉砕。",
            securityConcept = "【デ: データリンク層】同一LAN内のMACアドレスによる通信。スイッチでのARP検査が中間者攻撃を防ぐ。"
        ),
        DefenseAction(
            id = ActionId.L2_PORT_SECURITY,
            name = "ポートセキュリティ (MACアドレス固定)",
            commandCode = "switch(config-if)# switchport port-security maximum 1",
            layer = OsiLayer.L2_DATALINK,
            apCost = 1,
            baseShield = 30,
            businessImpact = 0,
            description = "未許可MACアドレスのフレーム流入を阻止し、シールド+30展開。",
            securityConcept = "【デ: データリンク層】MACフラッディング攻撃によるスイッチのハブ化を防止。"
        ),

        // === L1 物理層 (ブ) ===
        DefenseAction(
            id = ActionId.L1_LOCK_SERVER_ROOM,
            name = "サーバルーム施錠 & USB端子封印",
            commandCode = "physical_security --lock-all --block-usb-storage",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            baseDamage = 50,
            businessImpact = 0,
            description = "物理的な接触や不正USBの拾い挿し・配線直結を物理的に完全遮断。",
            securityConcept = "【ブ: 物理層】ケーブル・端子・電源などの物理媒体。どんな論理防壁も物理接触されたら突破される。"
        ),
        DefenseAction(
            id = ActionId.RESTORE_FROM_TAPE,
            name = "磁気テープからサーバー整合性復元",
            commandCode = "lto_tape.restore_partition(\"/srv/core\")",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            healAmount = 35,
            businessImpact = 0,
            description = "物理的に隔離されたオフラインLTO磁気テープからバックアップを復元しHP+35回復。",
            securityConcept = "【ブ: 物理層】エアギャップ（物理的ネットワーク隔離）は、ランサムウェアに対する究極の保険。"
        ),

        // === 最終手段 (過剰防衛) ===
        DefenseAction(
            id = ActionId.OVERKILL_CUT_MAIN_CABLE,
            name = "【過剰防衛】全社光回線LANケーブル引っこ抜き",
            commandCode = "echo '光回線物理切断！全通信即時遮断！'",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            baseDamage = 999,
            isOverkill = true,
            businessImpact = 100,
            complaintOnUse = "営業部長「おい情シス！客と億単位のビデオ商談中だったんだぞ！回線抜く奴があるかバカモン！」",
            description = "物理的に全通信を即座に遮断！敵は100%死滅するが、全社業務が即時崩壊する！",
            securityConcept = "【過剰防衛の典型例】セキュリティは「事業を継続するため」にあるもの。業務そのものを止めてしまっては本末転倒となり、社内クレームでクビ寸前に。"
        ),
        DefenseAction(
            id = ActionId.OVERKILL_FORCE_SHUTDOWN,
            name = "【過剰防衛】社内全PC一括リモート強制電源OFF",
            commandCode = "shutdown /s /m \\* /f /t 0",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            baseDamage = 999,
            isOverkill = true,
            businessImpact = 85,
            complaintOnUse = "経理部長「ちょっと情シス！今日給与振込日なのよ！保存してないエクセル消えたじゃないの！」",
            description = "社内全PCを強制終了！感染拡散は止まるが、保存前の重要文書が全滅して大炎上！",
            securityConcept = "【過剰防衛の典型例】端末の電源強制断はファイル破損の危険大。まずは該当端末のみを論理的にネットワーク隔離するのが正しいインシデント初動対応。"
        )
    )

    fun getActionsForLayer(layer: OsiLayer): List<DefenseAction> {
        return allActions.filter { it.layer == layer && !it.isOverkill }
    }

    val overkillActions: List<DefenseAction>
        get() = allActions.filter { it.isOverkill }
}
