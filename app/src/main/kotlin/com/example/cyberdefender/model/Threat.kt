package com.example.cyberdefender.model

enum class ThreatType {
    L1_USB_WORM,
    L2_ARP_POISON,
    L3_PING_OF_DEATH,
    L4_SYN_FLOOD,
    L5_SESSION_HIJACK,
    L6_ENCODE_BYPASS,
    L7_SQL_INJECTION,
    ENDGAME_MULTI_APT
}

data class Threat(
    val type: ThreatType,
    val name: String,
    val codeName: String,
    val layer: OsiLayer,
    val maxHp: Int,
    val currentHp: Int,
    val avatarAscii: String,
    val storyIntro: String,
    val description: String,
    val criticalWeakness: ActionId,
    val weaknessHint: String,
    val attackIncidentIds: List<Int>, // 100種類のインシデントIDリスト
    val educationalDetail: String
) {
    fun copyWithHp(newHp: Int): Threat = copy(currentHp = newHp.coerceIn(0, maxHp))
}

object ThreatCatalog {
    fun createL1UsbWorm(): Threat = Threat(
        type = ThreatType.L1_USB_WORM,
        name = "拾い挿しUSB Autorunワーム",
        codeName = "PHYSICAL-AUTORUN-EXE",
        layer = OsiLayer.L1_PHYSICAL,
        maxHp = 80,
        currentHp = 80,
        avatarAscii = """
   +-------+
   | USB 2 | ===> [PORT]
   +-------+      (VIRUS!)
        """.trimIndent(),
        storyIntro = "内線: 「情シス！駐車場で『役員賞与リスト』って書いたUSB拾ったからPCに挿してみたんだが、画面が真っ暗になったぞ！」",
        description = "営業部長が拾った怪しいUSBメモリからAutorunで感染拡大。物理端子から社内ネットワークを直接汚染しようとしている。",
        criticalWeakness = ActionId.L1_LOCK_SERVER_ROOM,
        weaknessHint = "[ブ:L1 物理層] の防護策。端子の無効化やサーバルーム施錠で物理接触を遮断せよ。",
        attackIncidentIds = listOf(1, 2, 5),
        educationalDetail = "[ブ:L1 物理層] どんな強固なファイアウォールも、物理的にUSBを挿されたり回線を直結されたら無意味。USBポートの物理・論理的無効化やサーバルームの施錠管理が最優先。"
    )

    fun createL2ArpPoison(): Threat = Threat(
        type = ThreatType.L2_ARP_POISON,
        name = "ARPスプーフィング盗聴鬼",
        codeName = "L2-MITM-POISON",
        layer = OsiLayer.L2_DATALINK,
        maxHp = 90,
        currentHp = 90,
        avatarAscii = """
  [MAC: 00-AA-...] 
  ==ARP FAKE==> [SWITCH]
  [MITM SNIFFER]
        """.trimIndent(),
        storyIntro = "内線: 「総務部です。社内LANの通信が急激に重くなり、ブラウザに怪しい証明書警告が何度も出ます！」",
        description = "同一LAN内で偽のARP応答をブロードキャストし、ルータのMACアドレスを偽装して社内の全通信を中間者盗聴（Man-in-the-Middle）している。",
        criticalWeakness = ActionId.L2_ARP_SNOOPING,
        weaknessHint = "[デ:L2 データリンク層] の防護策。L2スイッチのARP監視やMACアドレス固定で偽装を弾け。",
        attackIncidentIds = listOf(15, 16, 18),
        educationalDetail = "[デ:L2 データリンク層] 同一ネットワーク内のMACアドレスを基にフレームを転送する層。スイッチのDynamic ARP Inspection (DAI) やポートセキュリティで中間者を防御。"
    )

    fun createL3PingOfDeath(): Threat = Threat(
        type = ThreatType.L3_PING_OF_DEATH,
        name = "巨大Ping of Death 嵐",
        codeName = "ICMP-FRAGMENT-OVERSIZE",
        layer = OsiLayer.L3_NETWORK,
        maxHp = 95,
        currentHp = 95,
        avatarAscii = """
  [IP PKT > 65535] 
  ===FRAGMENTS===> [ROUTER]
  (BUFFER OVERFLOW!)
        """.trimIndent(),
        storyIntro = "内線: 「情シス！外部と社内のルータが再起動ループに陥って基幹通信が寸断されています！」",
        description = "規定の最大サイズ（65,535バイト）を超える巨大なICMPパケットを分割送信し、再構築バッファオーバーフローでルータをクラッシュさせる。",
        criticalWeakness = ActionId.L3_BLOCK_ICMP_ROUTER,
        weaknessHint = "[ネ:L3 ネットワーク層] の防護策。境界ルータで不正パケットのフラグメントや不要なICMPを遮断せよ。",
        attackIncidentIds = listOf(29, 30, 31),
        educationalDetail = "[ネ:L3 ネットワーク層] エンドツーエンドのパケット配送（ルーティング）を担当する層。境界ルータでの適切なフィルタリングとuRPFが防衛の鍵。"
    )

    fun createL4SynFlood(): Threat = Threat(
        type = ThreatType.L4_SYN_FLOOD,
        name = "TCP SYNフラッド飽和攻撃魔",
        codeName = "TCP-3WAY-HALFOPEN",
        layer = OsiLayer.L4_TRANSPORT,
        maxHp = 100,
        currentHp = 100,
        avatarAscii = """
  [SYN] ========> [SERVER]
                  (WAIT ACK...)
  [SYN] ========> [BACKLOG FULL]
        """.trimIndent(),
        storyIntro = "内線: 「Webサーバが『接続タイムアウト』で全社ログイン不能になりました！助けてください！」",
        description = "TCPの3方向ハンドシェイクを悪用。大量のSYNパケットを送りつけ、接続待ち行列（バックログ）を満杯にして正規ユーザーを締め出す。",
        criticalWeakness = ActionId.L4_SYN_COOKIE,
        weaknessHint = "[ト:L4 トランスポート層] の防護策。TCPスタックでSYN Cookiesを有効化して半開接続キューを守れ。",
        attackIncidentIds = listOf(43, 44, 45),
        educationalDetail = "[ト:L4 トランスポート層] ポート番号を用いて通信の信頼性を保証する層。SYN CookiesのようにOSのTCPスタック設定でリソース枯渇に対抗。"
    )

    fun createL5SessionHijack(): Threat = Threat(
        type = ThreatType.L5_SESSION_HIJACK,
        name = "クッキー泥棒セッション偽装魔",
        codeName = "SESSION-ID-SPOOF",
        layer = OsiLayer.L5_SESSION,
        maxHp = 105,
        currentHp = 105,
        avatarAscii = """
  [COOKIE: JSESSION=abc]
  ===STEAL&USE==> [PORTAL]
  (ADMIN LOGIN OK!)
        """.trimIndent(),
        storyIntro = "内線: 「大変です！出張中の社長のアカウントから、社内ポータルに『全社員昇給ゼロ』の通達が投稿されました！」",
        description = "公衆Wi-Fi盗聴やXSS経由で社長のセッションID（Cookie）を盗取。ログイン処理を飛ばし、社長の権限になりすまして操作している。",
        criticalWeakness = ActionId.L5_SESSION_SECURE_COOKIE,
        weaknessHint = "[セ:L5 セッション層] の防護策。CookieにHttpOnly/Secureを付与し、セッション対話管理を保護せよ。",
        attackIncidentIds = listOf(57, 58, 59),
        educationalDetail = "[セ:L5 セッション層] 対話（セッション）の開始・維持・終了を制御する層。CookieへのHttpOnly/Secure属性付与やセッションID再生成が防御の要点。"
    )

    fun createL6EncodeBypass(): Threat = Threat(
        type = ThreatType.L6_ENCODE_BYPASS,
        name = "0x5C文字コードすり抜け魔",
        codeName = "SJIS-0x5C-SMUGGLER",
        layer = OsiLayer.L6_PRESENTATION,
        maxHp = 110,
        currentHp = 110,
        avatarAscii = """
  [0x8F 0x5C '\']
  ====SMUGGLE===> [BACKEND]
  (ENCODE MISMATCH)
        """.trimIndent(),
        storyIntro = "内線: 「レガシーシステム（Shift-JIS環境）に特殊な文字コードが送り込まれ、文字化けして例外エラーが連発しています！」",
        description = "Shift-JIS特有の『ソ』や『表』の第2バイトが0x5C（バックスラッシュ）になる性質や、TLS暗号化の不備を突き、セキュリティチェックをすり抜ける。",
        criticalWeakness = ActionId.L6_TLS_ENCODE_FIX,
        weaknessHint = "[プ:L6 プレゼンテーション層] の防護策。通信の暗号化と文字コードの正規化・検証を行え。",
        attackIncidentIds = listOf(71, 72, 73),
        educationalDetail = "[プ:L6 プレゼンテーション層] データの表現形式（暗号化、文字コード、圧縮）を変換・統一する層。文字コードの明示的正規化と最新TLSの強制適用で対抗。"
    )

    fun createL7SqlInjection(): Threat = Threat(
        type = ThreatType.L7_SQL_INJECTION,
        name = "1=1 Tautology SQLクラッカー",
        codeName = "SQLI-UNION-DROP",
        layer = OsiLayer.L7_APPLICATION,
        maxHp = 130,
        currentHp = 130,
        avatarAscii = """
  [ ' OR 1=1 -- ]
  ====INJECT====> [ORACLE DB]
  (DATA EXFILTRATION)
        """.trimIndent(),
        storyIntro = "内線: 「おい！Web受発注システムのログイン画面で変な文字入れたら、他社の未公開見積書が画面に出てきたぞ！」",
        description = "Webブラウザの入力フォームから悪意あるSQL文を挿入。認証をすり抜け、全顧客の機密データとクレジットカード情報を強奪しようとする。",
        criticalWeakness = ActionId.L7_WAF_PARAM_BIND,
        weaknessHint = "[ア:L7 アプリケーション層] の防護策。入力値とSQL文を完全分離するプリペアドステートメントを使え。",
        attackIncidentIds = listOf(85, 86, 87),
        educationalDetail = "[ア:L7 アプリケーション層] アプリケーションソフトウェアが直接やり取りする最上位層。SQLインジェクションの根本対策はプレースホルダーを用いた静的バインド。"
    )

    fun createEndgameMultiApt(): Threat = Threat(
        type = ThreatType.ENDGAME_MULTI_APT,
        name = "[最終防衛線] OSI多段波状攻撃：APT複合部隊",
        codeName = "APT-FULL-STACK-INVASION",
        layer = OsiLayer.L7_APPLICATION,
        maxHp = 220,
        currentHp = 220,
        avatarAscii = """
  [L7: SQLi]  <=>  [L4: SYN]
       \            /
     [APT MULTI INTRUDER]
       /            \
  [L3: PING]  <=>  [L2: ARP]
        """.trimIndent(),
        storyIntro = "全社非常警報: 「社内外の全監視装置が赤色点灯！L1からL7までの全層を一斉に突く標的型複合サイバー攻撃が勃発！時間内に全方位迎撃せよ！」",
        description = "国家級攻撃グループによる多層波状攻撃。ターンごとに異なるアプセトネデブの層から致命傷を狙ってくる過酷な防衛戦。",
        criticalWeakness = ActionId.L7_WAF_PARAM_BIND,
        weaknessHint = "器材ログを熟読し、どの層（アプセトネデブ）から攻めてきているか毎ターン見極めろ。",
        attackIncidentIds = listOf(30, 43, 57, 85, 100),
        educationalDetail = "[アプセトネデブ多層防衛] 現代の高度標的型攻撃（APT）は複数の階層を連鎖的に攻撃する。各層ごとの役割に応じた多層防御（Defense in Depth）の構築が必須。"
    )
}
