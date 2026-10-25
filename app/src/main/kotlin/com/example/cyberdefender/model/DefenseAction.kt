package com.example.cyberdefender.model

enum class OsiLayer(
    val code: String,
    val layerNumber: Int,
    val japaneseLabel: String,
    val shortName: String
) {
    L7_APPLICATION("ア", 7, "アプリケーション層", "[ア:L7] アプリケーション"),
    L6_PRESENTATION("プ", 6, "プレゼンテーション層", "[プ:L6] プレゼンテーション"),
    L5_SESSION("セ", 5, "セッション層", "[セ:L5] セッション"),
    L4_TRANSPORT("ト", 4, "トランスポート層", "[ト:L4] トランスポート"),
    L3_NETWORK("ネ", 3, "ネットワーク層", "[ネ:L3] ネットワーク"),
    L2_DATALINK("デ", 2, "データリンク層", "[デ:L2] データリンク"),
    L1_PHYSICAL("ブ", 1, "物理層", "[ブ:L1] 物理")
}

enum class ActionId {
    // L7 (ア)
    L7_WAF_PARAM_BIND,
    L7_INPUT_SANITIZE,
    L7_ADVANCED_PREPARED_STMT, // ショップ購入可能

    // L6 (プ)
    L6_TLS_ENCODE_FIX,
    L6_CIPHER_RESTRICT,
    L6_HSTS_STRICT,            // ショップ購入可能

    // L5 (セ)
    L5_SESSION_SECURE_COOKIE,
    L5_REGENERATE_SESSION_ID,
    L5_JWT_BLACK_LIST,         // ショップ購入可能

    // L4 (ト)
    L4_SYN_COOKIE,
    L4_RATE_LIMIT,
    L4_TCP_BBR_CONGESTION,     // ショップ購入可能

    // L3 (ネ)
    L3_BLOCK_ICMP_ROUTER,
    L3_INGRESS_FILTER,
    L3_BGP_BLACKHOLING,        // ショップ購入可能

    // L2 (デ)
    L2_ARP_SNOOPING,
    L2_PORT_SECURITY,
    L2_8021X_AUTH,             // ショップ購入可能

    // L1 (ブ)
    L1_LOCK_SERVER_ROOM,
    RESTORE_FROM_TAPE,
    L1_BIOMETRIC_LOCK,         // ショップ購入可能

    // 禁忌肢 (選んだら即負け)
    TABOO_RAW_SQL_STRING_CONCAT,
    TABOO_REBOOT_CORE_SWITCH,
    TABOO_FORWARD_INFECTED_EMAIL,
    TABOO_DISABLE_FIREWALL,

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
    val expectedDamage: Int = 0, // 表示は期待値
    val expectedShield: Int = 0,
    val healAmount: Int = 0,
    val isOverkill: Boolean = false,
    val isTaboo: Boolean = false, // 禁忌肢フラグ (即ゲームオーバー)
    val tabooGameOverMessage: String? = null,
    val businessImpact: Int = 0,
    val complaintOnUse: String? = null,
    val isPurchasable: Boolean = false, // ショップで購入する拡張コマンド
    val purchaseCost: Int = 0,
    val description: String,
    val securityConcept: String
)

object DefenseActionCatalog {
    val allActions = listOf(
        // === L7 アプリケーション層 (ア) ===
        DefenseAction(
            id = ActionId.L7_WAF_PARAM_BIND,
            name = "WAF適用 & パラメータ静的バインド",
            commandCode = "waf --block-sqli --rule=param_bind",
            layer = OsiLayer.L7_APPLICATION,
            apCost = 1,
            expectedDamage = 50,
            description = "入力値をSQL文から分離し、SQL構文改ざんやXSSを無害化する。",
            securityConcept = "[ア:L7] 静的プレースホルダーバインドが根本治療。動的SQL組み立ての完全排除。"
        ),
        DefenseAction(
            id = ActionId.L7_INPUT_SANITIZE,
            name = "Web入力値サニタイズ & 特殊文字エスケープ",
            commandCode = "sanitizer.filterHtmlSpecialChars()",
            layer = OsiLayer.L7_APPLICATION,
            apCost = 1,
            expectedShield = 30,
            description = "危険なタグやクォート文字を無害化し、アプリ層のシールドを30展開。",
            securityConcept = "[ア:L7] XSSやインジェクションに対する入力バリデーションの多層防御。"
        ),
        DefenseAction(
            id = ActionId.L7_ADVANCED_PREPARED_STMT,
            name = "高精度ORMプリペアドステートメント完全強制",
            commandCode = "db.enforceTypeSafePreparedStatements()",
            layer = OsiLayer.L7_APPLICATION,
            apCost = 1,
            expectedDamage = 75,
            expectedShield = 20,
            isPurchasable = true,
            purchaseCost = 250000,
            description = "[設備投資コマンド] 全SQLクエリの型安全コンパイルを強制。威力75+シールド20。",
            securityConcept = "[ア:L7] アプリケーションフレームワーク層での静的SQLコンパイル保証。"
        ),

        // === L6 プレゼンテーション層 (プ) ===
        DefenseAction(
            id = ActionId.L6_TLS_ENCODE_FIX,
            name = "TLS1.3強制 & UTF-8文字コード正規化",
            commandCode = "ssl_config --enforce-tls1.3 --sanitize-encoding",
            layer = OsiLayer.L6_PRESENTATION,
            apCost = 1,
            expectedDamage = 50,
            description = "文字コード不一致を解消し、0x5Cすり抜けやダウングレード攻撃を迎撃。",
            securityConcept = "[プ:L6] データの表現形式（暗号化、文字コード、圧縮）を統一・管理する層。"
        ),
        DefenseAction(
            id = ActionId.L6_CIPHER_RESTRICT,
            name = "脆弱暗号スイート無効化 & 厳格証明書検証",
            commandCode = "ssl.disableCiphers(RC4, 3DES, CBC)",
            layer = OsiLayer.L6_PRESENTATION,
            apCost = 1,
            expectedShield = 30,
            description = "安全な暗号スイートのみを許可し、シールドを30展開。",
            securityConcept = "[プ:L6] 暗号化通信の強度を維持し、中間者による暗号解読を防止。"
        ),
        DefenseAction(
            id = ActionId.L6_HSTS_STRICT,
            name = "HSTSプリロード & 証明書ピンニング(HPKP)",
            commandCode = "nginx.setHstsPreload(maxAge=31536000)",
            layer = OsiLayer.L6_PRESENTATION,
            apCost = 1,
            expectedDamage = 75,
            expectedShield = 20,
            isPurchasable = true,
            purchaseCost = 250000,
            description = "[設備投資コマンド] 平文HTTP接続を100%遮断し不正証明書を弾く。威力75+シールド20。",
            securityConcept = "[プ:L6] SSL Stripping攻撃の完全阻止と中間者偽造証明書排除。"
        ),

        // === L5 セッション層 (セ) ===
        DefenseAction(
            id = ActionId.L5_SESSION_SECURE_COOKIE,
            name = "Cookie属性厳格化 (HttpOnly/Secure/SameSite)",
            commandCode = "session.setFlags(HttpOnly, Secure, SameSite=Strict)",
            layer = OsiLayer.L5_SESSION,
            apCost = 1,
            expectedDamage = 50,
            description = "Cookieへのスクリプトアクセスを遮断し、セッション乗っ取りを阻止。",
            securityConcept = "[セ:L5] 対話の確立・維持・終了を管理。Cookieの保護とセッション追跡防御が核心。"
        ),
        DefenseAction(
            id = ActionId.L5_REGENERATE_SESSION_ID,
            name = "全セッションID強制再採番 & 短期タイムアウト",
            commandCode = "session.regenerateId(all=true)",
            layer = OsiLayer.L5_SESSION,
            apCost = 1,
            expectedShield = 30,
            description = "既存のセッション識別子を一新し、不正乗っ取り通信を弾いてシールド30。",
            securityConcept = "[セ:L5] セッション固定化（Fixation）攻撃に対して、ID再発行が特効薬。"
        ),
        DefenseAction(
            id = ActionId.L5_JWT_BLACK_LIST,
            name = "ステートフルJWTブラックリスト & 即時失効管理",
            commandCode = "redis.blacklistAllCompromisedTokens()",
            layer = OsiLayer.L5_SESSION,
            apCost = 1,
            expectedDamage = 75,
            expectedShield = 20,
            isPurchasable = true,
            purchaseCost = 250000,
            description = "[設備投資コマンド] 漏洩したトークンを瞬時に全社失効化。威力75+シールド20。",
            securityConcept = "[セ:L5] ステートレス認証におけるトークン失効管理の弱点を補完。"
        ),

        // === L4 トランスポート層 (ト) ===
        DefenseAction(
            id = ActionId.L4_SYN_COOKIE,
            name = "SYN Cookie有効化 & 半開接続バックログ防御",
            commandCode = "sysctl -w net.ipv4.tcp_syncookies=1",
            layer = OsiLayer.L4_TRANSPORT,
            apCost = 1,
            expectedDamage = 50,
            description = "メモリを消費しないSYN Cookie技術でTCP SYNフラッド攻撃を無力化。",
            securityConcept = "[ト:L4] TCP/UDPポートを管理。SYN CookieはTCPスタックのリソース枯渇を防ぐ決定打。"
        ),
        DefenseAction(
            id = ActionId.L4_RATE_LIMIT,
            name = "TCP接続レートリミット & 不要ポート動的閉塞",
            commandCode = "iptables -A INPUT -p tcp --dport 80 -m limit --limit 50/s",
            layer = OsiLayer.L4_TRANSPORT,
            apCost = 1,
            expectedShield = 30,
            description = "異常なポートアクセス頻度を制限し、シールドを30展開。",
            securityConcept = "[ト:L4] 不要な待ち受けポートを閉じ、接続数を制限して帯域とキューを保護。"
        ),
        DefenseAction(
            id = ActionId.L4_TCP_BBR_CONGESTION,
            name = "TCP BBR輻輳制御 & 高度ステートフルSYNドロップ",
            commandCode = "sysctl -w net.ipv4.tcp_congestion_control=bbr",
            layer = OsiLayer.L4_TRANSPORT,
            apCost = 1,
            expectedDamage = 75,
            expectedShield = 20,
            isPurchasable = true,
            purchaseCost = 250000,
            description = "[設備投資コマンド] ボトルネック帯域幅測定と高速破棄。威力75+シールド20。",
            securityConcept = "[ト:L4] 最新TCP輻輳制御アルゴリズムによる高負荷通信の安定化。"
        ),

        // === L3 ネットワーク層 (ネ) ===
        DefenseAction(
            id = ActionId.L3_BLOCK_ICMP_ROUTER,
            name = "ルータ巨大ICMP遮断 & フラグメント再構築破棄",
            commandCode = "ip route add blackhole 198.51.100.0/24",
            layer = OsiLayer.L3_NETWORK,
            apCost = 1,
            expectedDamage = 50,
            description = "ルータのACLで不正なフラグメントPingや送信元偽装パケットを完全破棄。",
            securityConcept = "[ネ:L3] IPアドレスによるパケット中継（ルーティング）。境界機器でのフィルタリングが基本。"
        ),
        DefenseAction(
            id = ActionId.L3_INGRESS_FILTER,
            name = "Ingress/Egressフィルタリング (uRPF送信元検証)",
            commandCode = "router(config)# ip verify unicast source reachable-via rx",
            layer = OsiLayer.L3_NETWORK,
            apCost = 1,
            expectedShield = 30,
            description = "到達不能な偽装送信元IPをルータで自動ドロップし、シールド30展開。",
            securityConcept = "[ネ:L3] 送信元IP偽装（IPスプーフィング）をネットワーク境界で防止。"
        ),
        DefenseAction(
            id = ActionId.L3_BGP_BLACKHOLING,
            name = "RTBH (リモートトリガードブラックホール) ルーティング",
            commandCode = "bgp community set 65535:666",
            layer = OsiLayer.L3_NETWORK,
            apCost = 1,
            expectedDamage = 75,
            expectedShield = 20,
            isPurchasable = true,
            purchaseCost = 250000,
            description = "[設備投資コマンド] 上流ISPと連携し不正トラフィックを上流で全破棄。威力75+シールド20。",
            securityConcept = "[ネ:L3] 自社回線に届く前のISPエッジでのDDoSトラフィック吸い込み。"
        ),

        // === L2 データリンク層 (デ) ===
        DefenseAction(
            id = ActionId.L2_ARP_SNOOPING,
            name = "スイッチDynamic ARP Inspection (DAI)",
            commandCode = "switch(config)# ip arp inspection vlan 1",
            layer = OsiLayer.L2_DATALINK,
            apCost = 1,
            expectedDamage = 50,
            description = "偽のARP応答パケットをL2スイッチで検知・破棄し、盗聴を粉砕。",
            securityConcept = "[デ:L2] 同一LAN内のMACアドレスによる通信。スイッチでのARP検査が中間者攻撃を防ぐ。"
        ),
        DefenseAction(
            id = ActionId.L2_PORT_SECURITY,
            name = "ポートセキュリティ (MACアドレス学習固定 & 閉塞)",
            commandCode = "switch(config-if)# switchport port-security maximum 1",
            layer = OsiLayer.L2_DATALINK,
            apCost = 1,
            expectedShield = 30,
            description = "未許可MACアドレスのフレーム流入を阻止し、シールド30展開。",
            securityConcept = "[デ:L2] MACフラッディング攻撃によるスイッチのハブ化を防止。"
        ),
        DefenseAction(
            id = ActionId.L2_8021X_AUTH,
            name = "IEEE 802.1X ポート認証 (RADIUS連携)",
            commandCode = "switch(config)# dot1x system-auth-control",
            layer = OsiLayer.L2_DATALINK,
            apCost = 1,
            expectedDamage = 75,
            expectedShield = 20,
            isPurchasable = true,
            purchaseCost = 250000,
            description = "[設備投資コマンド] 未認証端末のLAN接続を物理スイッチ側で即座に遮断。威力75+シールド20。",
            securityConcept = "[デ:L2] EAPOLによる強固なクライアント証明書・アカウント認証。"
        ),

        // === L1 物理層 (ブ) ===
        DefenseAction(
            id = ActionId.L1_LOCK_SERVER_ROOM,
            name = "サーバルーム物理施錠 & USB端子論理無効化",
            commandCode = "physical_security --lock-all --block-usb-storage",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            expectedDamage = 50,
            description = "物理的な接触や不正USBの拾い挿し・配線直結を物理的に完全遮断。",
            securityConcept = "[ブ:L1] ケーブル・端子・電源などの物理媒体。どんな論理防壁も物理接触されたら突破される。"
        ),
        DefenseAction(
            id = ActionId.RESTORE_FROM_TAPE,
            name = "磁気テープ(LTO)からサーバー整合性復旧",
            commandCode = "lto_tape.restore_partition(\"/srv/core\")",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            healAmount = 35,
            description = "物理的に隔離されたオフラインLTO磁気テープからバックアップを復元しHP35回復。",
            securityConcept = "[ブ:L1] エアギャップ（物理的ネットワーク隔離）はランサムウェアに対する究極の保険。"
        ),
        DefenseAction(
            id = ActionId.L1_BIOMETRIC_LOCK,
            name = "サーバルーム生体認証ゲート & 暗黒配線物理撤去",
            commandCode = "biometrics.enforceTwoFactorPhysical()",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            expectedDamage = 75,
            healAmount = 25,
            isPurchasable = true,
            purchaseCost = 250000,
            description = "[設備投資コマンド] 生体認証と配線整理で物理侵入を完全排除。威力75+HP25回復。",
            securityConcept = "[ブ:L1] 物理入退室管理記録とアンチパスバック機構の徹底。"
        ),

        // === 禁忌肢（選んだら即負け） ===
        DefenseAction(
            id = ActionId.TABOO_RAW_SQL_STRING_CONCAT,
            name = "[禁忌] 動的SQL文字列結合をデバッグ実行",
            commandCode = "sql = \"SELECT * FROM users WHERE id='\" + input + \"'\"",
            layer = OsiLayer.L7_APPLICATION,
            apCost = 1,
            isTaboo = true,
            tabooGameOverMessage = "[大惨事] 攻撃者が入力した「' OR '1'='1」がそのままSQLとして実行され、全データベースがDROP TABLEされました！全社データ完全消失で即日解雇処分！",
            description = "【禁忌肢】SQLインジェクション脆弱性のある動的組み立てコードを本番で実行してしまう。",
            securityConcept = "絶対禁止: 動的SQL生成はインジェクション攻撃の温床。必ずプレースホルダーを用いること。"
        ),
        DefenseAction(
            id = ActionId.TABOO_REBOOT_CORE_SWITCH,
            name = "[禁忌] 高負荷基幹L2スイッチの電源を強引に即時再起動",
            commandCode = "reload in 0 / force",
            layer = OsiLayer.L2_DATALINK,
            apCost = 1,
            isTaboo = true,
            tabooGameOverMessage = "[大惨事] ARPストーム発生中に基幹スイッチを強制電源断したため、設定ファイルが破損して社内全LANが永久沈黙！全社業務完全崩壊でクビ確定！",
            description = "【禁忌肢】原因究明やパケット遮断を行わず、主要機器の電源をいきなり引っこ抜く。",
            securityConcept = "絶対禁止: 障害中に無計画な電源断を行うと設定消失やループ増殖を招く。"
        ),
        DefenseAction(
            id = ActionId.TABOO_FORWARD_INFECTED_EMAIL,
            name = "[禁忌] 「怪しいメール注意！」と添付ファイルごと全社員へ一斉転送",
            commandCode = "mail --to all_staff@teito.co.jp --attach virus.exe",
            layer = OsiLayer.L7_APPLICATION,
            apCost = 1,
            isTaboo = true,
            tabooGameOverMessage = "[大惨事] 情シス自らがマルウェア添付メールを全社全社員数千人に拡散！社員が一斉に開いて社内全PCがランサムウェアに感染し会社倒産！",
            description = "【禁忌肢】注意喚起のつもりで感染ファイルを添付したまま社内へ二次配布してしまう。",
            securityConcept = "絶対禁止: 検体はサンドボックスに隔離し、社員にはテキストの注意文のみを通知すること。"
        ),
        DefenseAction(
            id = ActionId.TABOO_DISABLE_FIREWALL,
            name = "[禁忌] 通信確認のため境界ファイアウォールを全停止(DISABLE)",
            commandCode = "iptables -F && iptables -P INPUT ACCEPT",
            layer = OsiLayer.L3_NETWORK,
            apCost = 1,
            isTaboo = true,
            tabooGameOverMessage = "[大惨事] ファイアウォールを全面開放した瞬間、世界中のボットネットから毎秒数百万パケットが直撃しサーバが物理過熱で炎上！即死！",
            description = "【禁忌肢】疎通確認と称してセキュリティ境界を全解除してしまう致命的過失。",
            securityConcept = "絶対禁止: トラブルシューティング時も個別ルールのみを特定して検証すべき。"
        ),

        // === 最終手段 (過剰防衛) ===
        DefenseAction(
            id = ActionId.OVERKILL_CUT_MAIN_CABLE,
            name = "[過剰防衛] 全社光回線LANケーブル引っこ抜き",
            commandCode = "echo '光回線物理切断完了！全通信即時遮断！'",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            expectedDamage = 999,
            isOverkill = true,
            businessImpact = 100,
            complaintOnUse = "営業部長「おい情シス！客と億単位のビデオ商談中だったんだぞ！回線抜く奴があるかバカモン！」",
            description = "物理的に全通信を即座に遮断！敵は一撃で死滅するが、全社業務が即時崩壊する！",
            securityConcept = "[過剰防衛] セキュリティは事業継続のためにあるもの。業務を止めては本末転倒。"
        ),
        DefenseAction(
            id = ActionId.OVERKILL_FORCE_SHUTDOWN,
            name = "[過剰防衛] 社内全PC一括リモート強制電源OFF",
            commandCode = "shutdown /s /m \\* /f /t 0",
            layer = OsiLayer.L1_PHYSICAL,
            apCost = 1,
            expectedDamage = 999,
            isOverkill = true,
            businessImpact = 85,
            complaintOnUse = "経理部長「ちょっと情シス！今日給与振込日なのよ！保存してないエクセル消えたじゃないの！」",
            description = "社内全PCを強制終了！感染拡散は止まるが、保存前の重要文書が全滅して大炎上！",
            securityConcept = "[過剰防衛] 該当端末のみを論理隔離するのが初動対応の基本。"
        )
    )

    fun getActionsForLayer(layer: OsiLayer, unlockedActionIds: Set<ActionId>): List<DefenseAction> {
        return allActions.filter {
            it.layer == layer && !it.isOverkill && (!it.isPurchasable || unlockedActionIds.contains(it.id))
        }
    }

    val overkillActions: List<DefenseAction>
        get() = allActions.filter { it.isOverkill }

    val purchasableActions: List<DefenseAction>
        get() = allActions.filter { it.isPurchasable }
}
