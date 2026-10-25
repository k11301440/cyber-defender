package com.example.cyberdefender.model

data class Stage(
    val id: Int,
    val title: String,
    val subtitle: String,
    val layer: OsiLayer,
    val difficulty: String,
    val timeLimitSeconds: Int? = null, // エンドコンテンツは制限時間あり
    val isEndgame: Boolean = false,
    val createThreat: () -> Threat
)

data class OsiKnowledge(
    val layerNumber: Int,
    val osiInitial: String, // ア・プ・セ・ト・ネ・デ・ブ
    val layerName: String,
    val representativeProtocols: String,
    val typicalThreats: String,
    val realWorldDefense: String,
    val itExamTip: String
)

object StageCatalog {
    val stages = listOf(
        Stage(
            id = 1,
            title = "第1話: 駐車場の拾い挿しUSB",
            subtitle = "【ブ: L1 物理層】の防護",
            layer = OsiLayer.L1_PHYSICAL,
            difficulty = "EASY",
            createThreat = { ThreatCatalog.createL1UsbWorm() }
        ),
        Stage(
            id = 2,
            title = "第2話: 社内LANのARP盗聴鬼",
            subtitle = "【デ: L2 データリンク層】の防護",
            layer = OsiLayer.L2_DATALINK,
            difficulty = "NORMAL",
            createThreat = { ThreatCatalog.createL2ArpPoison() }
        ),
        Stage(
            id = 3,
            title = "第3話: 金曜17時の巨大Ping嵐",
            subtitle = "【ネ: L3 ネットワーク層】の防護",
            layer = OsiLayer.L3_NETWORK,
            difficulty = "NORMAL",
            createThreat = { ThreatCatalog.createL3PingOfDeath() }
        ),
        Stage(
            id = 4,
            title = "第4話: サーバを襲うSYNフラッド",
            subtitle = "【ト: L4 トランスポート層】の防護",
            layer = OsiLayer.L4_TRANSPORT,
            difficulty = "HARD",
            createThreat = { ThreatCatalog.createL4SynFlood() }
        ),
        Stage(
            id = 5,
            title = "第5話: 社長のクッキーが抜かれた!?",
            subtitle = "【セ: L5 セッション層】の防護",
            layer = OsiLayer.L5_SESSION,
            difficulty = "HARD",
            createThreat = { ThreatCatalog.createL5SessionHijack() }
        ),
        Stage(
            id = 6,
            title = "第6話: 5C文字コードすり抜け魔",
            subtitle = "【プ: L6 プレゼンテーション層】の防護",
            layer = OsiLayer.L6_PRESENTATION,
            difficulty = "EXPERT",
            createThreat = { ThreatCatalog.createL6EncodeBypass() }
        ),
        Stage(
            id = 7,
            title = "最終話: 1=1 Tautology クラッカー",
            subtitle = "【ア: L7 アプリケーション層】の防護",
            layer = OsiLayer.L7_APPLICATION,
            difficulty = "FINAL BOSS",
            createThreat = { ThreatCatalog.createL7SqlInjection() }
        ),
        Stage(
            id = 8,
            title = "エンドコンテンツ: OSI多段波状攻撃サバイバル",
            subtitle = "【全層複合: アプセトネデブ緊急スクランブル】",
            layer = OsiLayer.L7_APPLICATION,
            difficulty = "NIGHTMARE",
            timeLimitSeconds = 90, // 90秒制限
            isEndgame = true,
            createThreat = { ThreatCatalog.createEndgameMultiApt() }
        )
    )

    val osiKnowledges = listOf(
        OsiKnowledge(
            layerNumber = 7,
            osiInitial = "ア",
            layerName = "アプリケーション層 (Application)",
            representativeProtocols = "HTTP, HTTPS, DNS, SMTP, FTP, SQL",
            typicalThreats = "SQLインジェクション, XSS, CSRF, メール標的型攻撃",
            realWorldDefense = "プリペアドステートメント（静的バインド）、WAF、入力サニタイズ。過剰防衛（全社メール全面停止など）をすると業務麻痺。",
            itExamTip = "基本情報・支援士最頻出！動的SQL生成の禁止とプレースホルダー利用が鉄則。WAFは補助的な防壁。"
        ),
        OsiKnowledge(
            layerNumber = 6,
            osiInitial = "プ",
            layerName = "プレゼンテーション層 (Presentation)",
            representativeProtocols = "SSL/TLS, ASCII, UTF-8, Shift-JIS, JPEG",
            typicalThreats = "SSL/TLSダウングレード攻撃、文字コードすり抜け（0x5C問題等）",
            realWorldDefense = "最新TLS（1.3）の強制、文字コード（UTF-8）の明示的正規化とバリデーション。",
            itExamTip = "「データの表現形式」を扱う層。暗号化や圧縮、文字エンコーディングがこの層に該当。"
        ),
        OsiKnowledge(
            layerNumber = 5,
            osiInitial = "セ",
            layerName = "セッション層 (Session)",
            representativeProtocols = "TLSハンドシェイク、RPC、PPTP",
            typicalThreats = "セッションハイジャック、セッション固定化（Fixation）攻撃",
            realWorldDefense = "CookieへのHttpOnly/Secure属性付与、ログイン成功時のセッションID再生成、短い有効期限。",
            itExamTip = "「対話（セッション）の開始から終了」を管理する層。認証Cookieの安全な運用がポイント。"
        ),
        OsiKnowledge(
            layerNumber = 4,
            osiInitial = "ト",
            layerName = "トランスポート層 (Transport)",
            representativeProtocols = "TCP, UDP (ポート番号: 80, 443, 22等)",
            typicalThreats = "SYN Flood攻撃、ポートスキャン、UDP Flood",
            realWorldDefense = "SYN Cookieの有効化、ファイアウォールによる不要ポート閉塞、Rate Limiting。",
            itExamTip = "3ウェイハンドシェイク（SYN→SYN/ACK→ACK）の成立確認を遅延させる「SYN Cookie」が頻出対策。"
        ),
        OsiKnowledge(
            layerNumber = 3,
            osiInitial = "ネ",
            layerName = "ネットワーク層 (Network)",
            representativeProtocols = "IP (IPv4/IPv6), ICMP, ルーティングプロトコル",
            typicalThreats = "IPスプーフィング（送信元IP偽装）、Ping of Death、Smurf攻撃",
            realWorldDefense = "境界ルータでのIngressフィルタリング（外部から社内IPが来るパケットを破棄）、巨大ICMP遮断。",
            itExamTip = "「異なるネットワーク間をつなぐエンドツーエンドのルーティング（IPアドレス）」を担当する層。"
        ),
        OsiKnowledge(
            layerNumber = 2,
            osiInitial = "デ",
            layerName = "データリンク層 (Data Link)",
            representativeProtocols = "Ethernet (MACアドレス), ARP, スイッチ",
            typicalThreats = "ARPスプーフィング（ARPポイズニング）、MACフラッディング",
            realWorldDefense = "スイッチのDynamic ARP Inspection（DAI）、ポートセキュリティ（MACアドレス固定）。",
            itExamTip = "「同一LAN（セグメント）内の直接通信」を担当。ARP応答を詐称してルータになりすます中間者攻撃に注意。"
        ),
        OsiKnowledge(
            layerNumber = 1,
            osiInitial = "ブ",
            layerName = "物理層 (Physical)",
            representativeProtocols = "LANケーブル (UTP/光), コネクタ (RJ-45), 電圧, 電波",
            typicalThreats = "LANケーブル引っこ抜き、悪意あるUSB拾い挿し、サーバルーム物理侵入、盗聴器設置",
            realWorldDefense = "サーバルームの施錠・生体認証、USB自動実行（Autorun）の無効化、監視カメラ。",
            itExamTip = "「電気信号・物理媒体」の層。物理アクセスされたらあらゆる論理セキュリティが突破される。"
        )
    )
}
