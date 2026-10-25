package com.example.cyberdefender.model

enum class ThreatType {
    L1_USB_WORM,
    L2_ARP_POISON,
    L3_PING_OF_DEATH,
    L4_SYN_FLOOD,
    L5_SESSION_HIJACK,
    L6_ENCODE_BYPASS,
    L7_SQL_INJECTION
}

data class ThreatAttack(
    val name: String,
    val damage: Int,
    val piercesShield: Boolean = false,
    val warningMessage: String,
    val executedMessage: String
)

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
    val attackList: List<ThreatAttack>,
    val educationalDetail: String
) {
    fun copyWithHp(newHp: Int): Threat = copy(currentHp = newHp.coerceIn(0, maxHp))
}

object ThreatCatalog {
    fun createL1UsbWorm(): Threat = Threat(
        type = ThreatType.L1_USB_WORM,
        name = "拾い挿しUSBワーム",
        codeName = "PHYSICAL-AUTORUN-EXE",
        layer = OsiLayer.L1_PHYSICAL,
        maxHp = 80,
        currentHp = 80,
        avatarAscii = """
   .-------.
   | USB 2 | ===> [PORT]
   '-------'      (VIRUS!)
        """.trimIndent(),
        storyIntro = "内線「情シス！駐車場で『役員賞与リスト』って書いたUSB拾ったからPCに挿してみたんだけど、画面が真っ暗になったぞ！」",
        description = "営業部長が拾った怪しいUSBメモリからAutorunで感染拡大。物理端子から社内ネットワークを直接汚染しようとしている！",
        criticalWeakness = ActionId.L1_LOCK_SERVER_ROOM,
        weaknessHint = "「ブ (L1: 物理層)」の防護策！端子の無効化やサーバルームの施錠で物理接触を断て！",
        attackList = listOf(
            ThreatAttack(
                name = "Autorun.inf 実行",
                damage = 25,
                piercesShield = false,
                warningMessage = "【L1警報】USB端子経由で未許可実行ファイルが起動しようとしている！",
                executedMessage = "USBからウイルスがローカルにコピーされた！（25ダメージ）"
            ),
            ThreatAttack(
                name = "物理LANポート探索",
                damage = 35,
                piercesShield = true,
                warningMessage = "【L1警報】無施錠のハブからLAN全体への感染拡大シーケンス開始！",
                executedMessage = "物理的に接続された別端末へ感染が拡大！（貫通35ダメージ）"
            )
        ),
        educationalDetail = "【ブ: L1 物理層】どんなに強固なファイアウォールも、物理的にUSBを挿されたり回線を直結されたら無意味。USBポートの物理・論理的無効化（GPO）やサーバルームの施錠管理が基本中の基本です。"
    )

    fun createL2ArpPoison(): Threat {
        return Threat(
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
            storyIntro = "総務部「情シスさん、さっきからネットが急に重いし、証明書の警告画面が何度も出るんだけど…？」",
            description = "同一LAN内で偽のARP応答をブロードキャストし、ルータのMACアドレスを偽装して社内の全通信を中間者盗聴（Man-in-the-Middle）している！",
            criticalWeakness = ActionId.L2_ARP_SNOOPING,
            weaknessHint = "「デ (L2: データリンク層)」の防護策！スイッチのARP監視やMACアドレス固定で偽装を弾け！",
            attackList = listOf(
                ThreatAttack(
                    name = "Gratuitous ARP フラッド",
                    damage = 25,
                    piercesShield = false,
                    warningMessage = "【L2警報】偽のARP応答パケットが毎秒数千件社内LANに撒き散らされている！",
                    executedMessage = "社内PCのARPテーブルが改ざんされ、通信が盗聴端末へ吸い込まれた！（25ダメージ）"
                ),
                ThreatAttack(
                    name = "パケット中間者改ざん",
                    damage = 35,
                    piercesShield = true,
                    warningMessage = "【L2警報】横取りしたパケットのペイロードを改ざんして中継中！",
                    executedMessage = "データリンク層でのパケット改ざんにより基幹DB通信が破壊された！（貫通35ダメージ）"
                )
            ),
            educationalDetail = "【デ: L2 データリンク層】同一セグメント（LAN）内のMACアドレスによる直接通信。ARPには認証機構がないため、悪意ある端末が『ルータのMACは私です』と叫ぶと信じてしまう。対策はスイッチでのDynamic ARP Inspection（DAI）や静的MAC登録です。"
        )
    }

    fun createL3PingOfDeath(): Threat {
        return Threat(
            type = ThreatType.L3_PING_OF_DEATH,
            name = "Ping of Death 嵐",
            codeName = "ICMP-FRAGMENT-BOOM",
            layer = OsiLayer.L3_NETWORK,
            maxHp = 100,
            currentHp = 100,
            avatarAscii = """
  [PING > 65535B]
  ====ICMP====> [ROUTER]
  (BUFFER CRASH)
            """.trimIndent(),
            storyIntro = "情シス室の電話が鳴る「おい！金曜17時だぞ！社内グループウェアがタイムアウトで日報が出せないぞ！」",
            description = "IPヘッダの規約（65535バイト）を超える巨大パケットを分割送信し、ルータの再構築バッファをオーバーフローさせてクラッシュさせる古典的かつ強力なL3攻撃！",
            criticalWeakness = ActionId.L3_IP_FILTERING,
            weaknessHint = "「ネ (L3: ネットワーク層)」の防護策！ルータのACLで送信元IP偽装や異常ICMPを即時破棄せよ！",
            attackList = listOf(
                ThreatAttack(
                    name = "IPフラグメント巨大ICMP",
                    damage = 25,
                    piercesShield = false,
                    warningMessage = "【L3警報】許容サイズを超える断片化IPパケットがルータに殺到！",
                    executedMessage = "パケット再構築バッファが溢れ、基幹ルータが瞬間フリーズ！（25ダメージ）"
                ),
                ThreatAttack(
                    name = "IPスプーフィング・アタック",
                    damage = 35,
                    piercesShield = false,
                    warningMessage = "【L3警報】送信元IPを社内アドレスに偽装したパケットが外部から流入！",
                    executedMessage = "内部宛てパケットの誤ルーティングで社内ネットワークが大渋滞！（35ダメージ）"
                )
            ),
            educationalDetail = "【ネ: L3 ネットワーク層】IPパケットの経路制御（ルーティング）を担当する層。送信元IPを詐称するIPスプーフィングや巨大ICMP攻撃には、境界ルータでのIngressフィルタリング（送信元アドレス検証）やICMP流量制限で対処します。"
        )
    }

    fun createL4SynFlood(): Threat {
        return Threat(
            type = ThreatType.L4_SYN_FLOOD,
            name = "SYN Flood 枯渇ボット",
            codeName = "TCP-HALF-OPEN-STORM",
            layer = OsiLayer.L4_TRANSPORT,
            maxHp = 110,
            currentHp = 110,
            avatarAscii = """
  [TCP SYN] -> ?
  [TCP SYN] -> ?
  [TCP SYN] -> (QUEUE FULL)
            """.trimIndent(),
            storyIntro = "Web通販担当「情シス！顧客から『注文画面に繋がらない、エラーが出る』って問い合わせが殺到してる！」",
            description = "TCPの3ウェイ・ハンドシェイク（SYN→SYN/ACK→ACK）を悪用。最後のACKを返さず半開き接続（Half-open）を大量放置し、サーバの接続キューを満杯にする！",
            criticalWeakness = ActionId.L4_SYN_COOKIE_PORT_CLOSE,
            weaknessHint = "「ト (L4: トランスポート層)」の防護策！接続状態を保持しないSYN Cookie技術でキュー枯渇を防げ！",
            attackList = listOf(
                ThreatAttack(
                    name = "Half-Open 接続飽和",
                    damage = 25,
                    piercesShield = false,
                    warningMessage = "【L4警報】TCP SYNバックログキューが使用率99%に到達！",
                    executedMessage = "正規ユーザーのTCP新規接続が全てタイムアウト破棄された！（25ダメージ）"
                ),
                ThreatAttack(
                    name = "全ポートスキャン強襲",
                    damage = 35,
                    piercesShield = false,
                    warningMessage = "【L4警報】ポート1〜65535に対する高速FIN/NULLスキャンを検知！",
                    executedMessage = "開いていたテスト用ポートから内部通信を特定された！（35ダメージ）"
                )
            ),
            educationalDetail = "【ト: L4 トランスポート層】TCPやUDPによる通信品質やポート番号管理を担う層。SYN Flood対策には、接続完了までサーバ側でメモリ（接続状態）を確保しない『SYN Cookie』や不要ポートの閉塞が最も効果的です。"
        )
    }

    fun createL5SessionHijack(): Threat {
        return Threat(
            type = ThreatType.L5_SESSION_HIJACK,
            name = "セッション強奪ファントム",
            codeName = "SID-SNATCH-PHANTOM",
            layer = OsiLayer.L5_SESSION,
            maxHp = 110,
            currentHp = 110,
            avatarAscii = """
  [USER SESSION]
  ======STEAL=====> [HACKER]
  (ADMIN LOGIN OK)
            """.trimIndent(),
            storyIntro = "社長室「情シス君！私のパソコンで勝手に承認ボタンがポチポチ押されてるんだが、新種の幽霊かね!?」",
            description = "公衆Wi-Fi盗聴やXSS経由で社長のセッションID（Cookie）を盗取！ログイン処理を完全にすっ飛ばし、社長の権限になりすまして社内システムを操作している！",
            criticalWeakness = ActionId.L5_SESSION_SECURE_COOKIE,
            weaknessHint = "「セ (L5: セッション層)」の防護策！CookieにHttpOnly/Secureを付与し、セッション対話管理を保護せよ！",
            attackList = listOf(
                ThreatAttack(
                    name = "セッションID盗用なりすまし",
                    damage = 30,
                    piercesShield = true,
                    warningMessage = "【L5警報】社長のセッションIDを使った未承認API呼び出しが多発！",
                    executedMessage = "正規のセッションとして認識されたため、シールドを素通りして全社データが操作された！（貫通30ダメージ）"
                ),
                ThreatAttack(
                    name = "セッション固定化（Fixation）",
                    damage = 35,
                    piercesShield = false,
                    warningMessage = "【L5警報】外部から注入された固定セッションIDへのログイン誘導を検知！",
                    executedMessage = "攻撃者の指定したセッションでログインさせられ、アカウントが乗っ取られた！（35ダメージ）"
                )
            ),
            educationalDetail = "【セ: L5 セッション層】対話（セッション）の開始・維持・終了を制御する層。WebではCookieやトークンで実現されます。CookieへのHttpOnly属性（JavaScriptからの盗取防止）やSecure属性（HTTPS限定）、ログイン時のセッションID再生成が防御の要点です。"
        )
    }

    fun createL6EncodeBypass(): Threat {
        return Threat(
            type = ThreatType.L6_ENCODE_BYPASS,
            name = "5C文字コードすり抜け魔",
            codeName = "SJIS-0x5C-SMUGGLER",
            layer = OsiLayer.L6_PRESENTATION,
            maxHp = 110,
            currentHp = 110,
            avatarAscii = """
  [0x8F 0x5C '\']
  ====SMUGGLE===> [BACKEND]
  (ENCODE MISMATCH)
            """.trimIndent(),
            storyIntro = "開発課「昔のレガシーシステム（Shift-JIS環境）に特殊な文字コードを送られたらしく、文字化けして例外エラー連発です！」",
            description = "Shift-JIS特有の『ソ』や『表』の第2バイトがバックスラッシュ（0x5C）になる性質や、TLS暗号化の不備を突き、セキュリティチェックをすり抜ける狡猾なデータ表現攻撃！",
            criticalWeakness = ActionId.L6_TLS_ENCODE_FIX,
            weaknessHint = "「プ (L6: プレゼンテーション層)」の防護策！データの暗号化と文字コードの正規化・検証を行え！",
            attackList = listOf(
                ThreatAttack(
                    name = "0x5C 2バイト文字化けすり抜け",
                    damage = 25,
                    piercesShield = false,
                    warningMessage = "【L6警報】WAFのチェックをすり抜ける不正エンコード文字列を検知！",
                    executedMessage = "バックエンドの文字コード変換差異を突かれ、エスケープが無効化された！（25ダメージ）"
                ),
                ThreatAttack(
                    name = "SSL/TLSダウングレード強要",
                    damage = 35,
                    piercesShield = true,
                    warningMessage = "【L6警報】暗号化通信を平文HTTPや脆弱な暗号スイートへ強制引き下げ試行！",
                    executedMessage = "平文通信に引き下げられ、暗号化されるはずの機密データが露出！（貫通35ダメージ）"
                )
            ),
            educationalDetail = "【プ: L6 プレゼンテーション層】データの表現形式（暗号化、文字コード、圧縮）を変換・統一する層。文字コードの不一致を利用したすり抜けや、SSL/TLSのダウングレード攻撃には、文字エンコーディングの明示的な正規化と最新TLS（1.3）の強制適用で対抗します。"
        )
    }

    fun createL7SqlInjection(): Threat {
        return Threat(
            type = ThreatType.L7_SQL_INJECTION,
            name = "1=1 Tautology クラッカー",
            codeName = "SQLI-UNION-DROP",
            layer = OsiLayer.L7_APPLICATION,
            maxHp = 130,
            currentHp = 130,
            avatarAscii = """
  [ ' OR 1=1 -- ]
  ====INJECT====> [ORACLE DB]
  (DATA EXFILTRATION)
            """.trimIndent(),
            storyIntro = "営業部長「おい！Web受発注システムのログイン画面で変な文字入れたら、他社の未公開見積書が全部画面に出てきたぞ！どうなってるんだ！」",
            description = "Webブラウザの入力フォームから悪意あるSQL文を挿入。認証をすり抜け、全顧客の機密データとクレジットカード情報を強奪しようとする最凶のL7攻撃！",
            criticalWeakness = ActionId.L7_WAF_PARAM_BIND,
            weaknessHint = "「ア (L7: アプリケーション層)」の防護策！入力値とSQL文を完全分離するプリペアドステートメントを使え！",
            attackList = listOf(
                ThreatAttack(
                    name = "Tautology 認証完全バイパス",
                    damage = 30,
                    piercesShield = true,
                    warningMessage = "【L7警報】ログインSQLに ' OR '1'='1 -- が注入されようとしている！",
                    executedMessage = "SQL構文の改ざんにより管理者として不正ログインされた！（シールド貫通30ダメージ）"
                ),
                ThreatAttack(
                    name = "UNION SELECT 全顧客流出",
                    damage = 45,
                    piercesShield = false,
                    warningMessage = "【L7警報】UNION SELECTによる顧客テーブル一括ダンプクエリが実行寸前！",
                    executedMessage = "数万件の機密顧客リストが外部へ流出！企業の信頼性が大暴落！（45ダメージ）"
                )
            ),
            educationalDetail = "【ア: L7 アプリケーション層】HTTPやDNS、SQLなどアプリケーションソフトウェアが直接やり取りする最上位層。SQLインジェクションの根本対策は、動的文字列結合を排除し、プレースホルダーを用いたプリペアドステートメント（静的バインド）を100%徹底することです。"
        )
    }
}
