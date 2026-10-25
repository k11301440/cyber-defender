package com.example.cyberdefender.model

enum class ThreatType {
    L1_USB_WORM,
    L2_ARP_POISON,
    L3_PING_OF_DEATH,
    L4_SYN_FLOOD,
    L5_SESSION_HIJACK,
    L6_ENCODE_BYPASS,
    L7_SQL_INJECTION,
    ENDGAME_MULTI_APT // エンドコンテンツ: 複合多段攻撃
}

data class ThreatAttack(
    val name: String,
    val damage: Int,
    val layer: OsiLayer,
    val piercesShield: Boolean = false,
    val warningMessage: String,
    val executedMessage: String,
    val mechanism: String,
    val defensePrinciple: String,
    val examTip: String
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
                layer = OsiLayer.L1_PHYSICAL,
                piercesShield = false,
                warningMessage = "【L1警報】USB端子経由で未許可実行ファイルが起動しようとしている！",
                executedMessage = "USBからウイルスがローカルにコピーされた！（25ダメージ）",
                mechanism = "OSの外部メディア自動実行機能（Autorun）を悪用し、接続と同時にマルウェアバイナリを自動起動させる手口。",
                defensePrinciple = "USBポートの物理的閉塞（物理ポートガード）やグループポリシー（GPO）による外部記憶装置接続制限。",
                examTip = "情報セキュリティマネジメント基本頻出。物理的セキュリティ対策（USB・サーバルーム入退出管理）が問われる。"
            ),
            ThreatAttack(
                name = "物理LANポート探索",
                damage = 35,
                layer = OsiLayer.L1_PHYSICAL,
                piercesShield = true,
                warningMessage = "【L1警報】無施錠のハブからLAN全体への感染拡大シーケンス開始！",
                executedMessage = "物理的に接続された別端末へ感染が拡大！（貫通35ダメージ）",
                mechanism = "物理的な配線・HUBの空きポートに勝手に持ち込み端末を直結して内部ネットワークに侵入する手口。",
                defensePrinciple = "サーバルーム・分電盤の施錠管理、フロアの空きLANポートのパッチ無効化。",
                examTip = "「情報資産の物理的隔離」の原則。入退室管理記録や施錠管理が最重要監査項目。"
            )
        ),
        educationalDetail = "【ブ: L1 物理層】どんなに強固なファイアウォールも、物理的にUSBを挿されたり回線を直結されたら無意味。USBポートの物理・論理的無効化（GPO）やサーバルームの施錠管理が基本中の基本です。"
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
        storyIntro = "総務部「情シスさん、さっきからネットが急に重いし、証明書の警告画面が何度も出るんだけど…？」",
        description = "同一LAN内で偽のARP応答をブロードキャストし、ルータのMACアドレスを偽装して社内の全通信を中間者盗聴（Man-in-the-Middle）している！",
        criticalWeakness = ActionId.L2_ARP_SNOOPING,
        weaknessHint = "「デ (L2: データリンク層)」の防護策！スイッチのARP監視やMACアドレス固定で偽装を弾け！",
        attackList = listOf(
            ThreatAttack(
                name = "Gratuitous ARP フラッド",
                damage = 25,
                layer = OsiLayer.L2_DATALINK,
                piercesShield = false,
                warningMessage = "【L2警報】偽のARP応答パケットが毎秒数千件社内LANに撒き散らされている！",
                executedMessage = "社内PCのARPテーブルが改ざんされ、通信が盗聴端末へ吸い込まれた！（25ダメージ）",
                mechanism = "自分から要求していないARP応答（GARP）を送信し、ターゲット端末のARPテーブル（IPとMACの対応表）を不正に上書きする。",
                defensePrinciple = "L2スイッチのDynamic ARP Inspection (DAI) 機能を有効化し、DHCPスヌーピングバインディングと照合。",
                examTip = "基本情報・応用情報で頻出。同一ブロードキャストドメイン内の盗聴手法（MITM）。"
            ),
            ThreatAttack(
                name = "MACフラッディング攻撃",
                damage = 35,
                layer = OsiLayer.L2_DATALINK,
                piercesShield = true,
                warningMessage = "【L2警報】L2スイッチのMACアドレステーブルをパンクさせる攻撃を検知！",
                executedMessage = "スイッチがフェイルオープン（全ポート転送ハブ化）し、全パケットがダダ漏れになった！（貫通35ダメージ）",
                mechanism = "大量のランダムな送信元MACアドレスを持つフレームを送り、スイッチのCAMテーブルを枯渇させてバカハブ（リピータ）状態に追い込む。",
                defensePrinciple = "スイッチポートごとのポートセキュリティ設定（学習可能MACアドレス数の上限設定）。",
                examTip = "スイッチがフェイルオープンしてリピータハブのように動作する挙動の理解が問われる。"
            )
        ),
        educationalDetail = "【デ: L2 データリンク層】同一ネットワーク内のMACアドレスを基にフレームを転送する層。ARPスプーフィングはL2の信頼前提を悪用するため、L2スイッチの機能（DAIやポートセキュリティ）で防護します。"
    )

    fun createL3PingOfDeath(): Threat = Threat(
        type = ThreatType.L3_PING_OF_DEATH,
        name = "Ping of Death 嵐",
        codeName = "ICMP-FRAGMENT-OVERSIZE",
        layer = OsiLayer.L3_NETWORK,
        maxHp = 95,
        currentHp = 95,
        avatarAscii = """
  [IP PKT > 65535] 
  ===FRAGMENTS===> [ROUTER]
  (BUFFER OVERFLOW!)
        """.trimIndent(),
        storyIntro = "監視アラート「警告: ルータのCPU使用率が99%に到達！ルータの再起動ループが発生しています！」",
        description = "規定の最大サイズ（65,535バイト）を超える巨大なICMP（Ping）パケットを分割送信し、組み立て時のバッファオーバーフローでルータをクラッシュさせる！",
        criticalWeakness = ActionId.L3_BLOCK_ICMP_ROUTER,
        weaknessHint = "「ネ (L3: ネットワーク層)」の防護策！外向きルータで不正パケットのフラグメントや不要なICMPを遮断せよ！",
        attackList = listOf(
            ThreatAttack(
                name = "巨大ICMPフラグメント爆撃",
                damage = 25,
                layer = OsiLayer.L3_NETWORK,
                piercesShield = false,
                warningMessage = "【L3警報】65535バイトを超える分割ICMPエコー要求がルータに殺到！",
                executedMessage = "IP再構築バッファが圧迫され、ルータのパケット処理が著しく遅延！（25ダメージ）",
                mechanism = "RFCで定められた最大IPパケット長（65,535バイト）を超える不正パケットを断片化（フラグメント）して送り、再構成時にクラッシュさせる。",
                defensePrinciple = "境界ルータ・ファイアウォールでのフラグメントパケット検査および不正サイズのドロップ。",
                examTip = "L3は「IPアドレスと経路制御（ルーティング）」を司る層。ICMPやARPとの層の違いを意識しよう。"
            ),
            ThreatAttack(
                name = "スマーフ（Smurf）増幅攻撃",
                damage = 35,
                layer = OsiLayer.L3_NETWORK,
                piercesShield = true,
                warningMessage = "【L3警報】ブロードキャスト宛てに送信元偽装Pingが投げ込まれた！",
                executedMessage = "第三者のネットワーク機器数千台から一斉に返信が集中し、回線がパンク！（貫通35ダメージ）",
                mechanism = "送信元IPアドレスを攻撃対象のIPに偽装し、増幅ネットワークのブロードキャスト宛てにICMP Echo Requestを送信する増幅型DDoS。",
                defensePrinciple = "ネットワーク機器でダイレクトブロードキャストへの応答を無効化（no ip directed-broadcast）。",
                examTip = "DDoSの増幅攻撃（アンプ攻撃）の代表例。送信元IP偽装（IPスプーフィング）とセットで出題。"
            )
        ),
        educationalDetail = "【ネ: L3 ネットワーク層】エンドツーエンドのパケット配送（ルーティング）を担当する層。IPアドレスやICMP、ルーティングプロトコルが該当。境界ルータでの適切なフィルタリングが防衛の鍵。"
    )

    fun createL4SynFlood(): Threat = Threat(
        type = ThreatType.L4_SYN_FLOOD,
        name = "SYNフラッド過負荷魔",
        codeName = "TCP-3WAY-HALFOPEN",
        layer = OsiLayer.L4_TRANSPORT,
        maxHp = 100,
        currentHp = 100,
        avatarAscii = """
  [SYN] ========> [SERVER]
                  (WAIT ACK...)
  [SYN] ========> [BACKLOG FULL]
        """.trimIndent(),
        storyIntro = "ヘルプデスク「社内Webサーバが『接続がタイムアウトしました』になって、全員ログインできません！」",
        description = "TCPの3方向ハンドシェイクを悪用。大量のSYNパケットを送りつけ、ACKを返さずにサーバの接続待ち行列（バックログ）を満杯にして正規ユーザーを締め出す！",
        criticalWeakness = ActionId.L4_SYN_COOKIE,
        weaknessHint = "「ト (L4: トランスポート層)」の防護策！TCPスタックでSYN Cookiesを有効化して半接続キューを守れ！",
        attackList = listOf(
            ThreatAttack(
                name = "ハーフオープン接続枯渇",
                damage = 30,
                layer = OsiLayer.L4_TRANSPORT,
                piercesShield = false,
                warningMessage = "【L4警報】毎秒数万件のTCP SYNパケットが80/443ポートに急増中！",
                executedMessage = "接続待ちキューが枯渇し、正規の社員アクセスが切断された！（30ダメージ）",
                mechanism = "TCP 3ウェイハンドシェイク（SYN→SYN/ACK→ACK）の最後のACKを意図的に返さず、サーバのリソースを割り当てたまま待機させる。",
                defensePrinciple = "SYN Cookieの導入（ACKが返るまでサーバメモリに接続状態を保存しない技術）。",
                examTip = "応用情報・高度情報で超頻出のL4トランスポート層DoS攻撃。SYN Cookiesの仕組みは必修。"
            ),
            ThreatAttack(
                name = "ポートスキャン＆RSTインジェクション",
                damage = 30,
                layer = OsiLayer.L4_TRANSPORT,
                piercesShield = true,
                warningMessage = "【L4警報】TCPシーケンス番号を推測したセッション強制切断（RST）攻撃！",
                executedMessage = "社内基幹システムのアクティブな通信が次々と強制切断された！（貫通30ダメージ）",
                mechanism = "通信中のTCPセッションのシーケンス番号を推測し、偽造したRST（リセット）パケットを注入してコネクションを強制終了させる。",
                defensePrinciple = "ランダムな初期シーケンス番号（ISN）の生成とTCP状態監視ファイアウォール（ステートフルインスペクション）。",
                examTip = "TCPのコネクション管理（ESTABLISHED状態やRSTフラグ）の役割に関する問題で登場。"
            )
        ),
        educationalDetail = "【ト: L4 トランスポート層】ポート番号を用いて通信の信頼性（TCPの再送制御・順序制御）やリアルタイム性（UDP）を保証する層。SYN CookiesのようにOSのTCPスタック設定で対処します。"
    )

    fun createL5SessionHijack(): Threat = Threat(
        type = ThreatType.L5_SESSION_HIJACK,
        name = "クッキー泥棒セッション魔",
        codeName = "SESSION-ID-SPOOF",
        layer = OsiLayer.L5_SESSION,
        maxHp = 105,
        currentHp = 105,
        avatarAscii = """
  [COOKIE: JSESSION=abc]
  ===STEAL&USE==> [PORTAL]
  (ADMIN LOGIN OK!)
        """.trimIndent(),
        storyIntro = "総務部「大変です！社長が海外出張中なのに、社内ポータルで社長アカウントから全社へ『全社員昇給ゼロ』の通達が投稿されました！」",
        description = "公衆Wi-Fi盗聴やXSS経由で社長のセッションID（Cookie）を盗取！ログイン処理を完全にすっ飛ばし、社長の権限になりすまして社内システムを操作している！",
        criticalWeakness = ActionId.L5_SESSION_SECURE_COOKIE,
        weaknessHint = "「セ (L5: セッション層)」の防護策！CookieにHttpOnly/Secureを付与し、セッション対話管理を保護せよ！",
        attackList = listOf(
            ThreatAttack(
                name = "セッションID盗用なりすまし",
                damage = 30,
                layer = OsiLayer.L5_SESSION,
                piercesShield = true,
                warningMessage = "【L5警報】社長のセッションIDを使った未承認API呼び出しが多発！",
                executedMessage = "正規のセッションとして認識されたため、シールドを素通りして全社データが操作された！（貫通30ダメージ）",
                mechanism = "認証後に払い出されたセッション識別子（Session ID）を盗聴・奪取し、再認証なしで被害者の権限を完全に行使する。",
                defensePrinciple = "Cookie属性にHttpOnly（JavaScriptからのアクセス禁止）およびSameSite/Secureを設定、ログイン時のセッションID再生成。",
                examTip = "情報処理安全確保支援士の超重要テーマ。セッションハイジャックとセッション固定化（Fixation）の区別が問われる。"
            ),
            ThreatAttack(
                name = "セッション固定化（Fixation）",
                damage = 35,
                layer = OsiLayer.L5_SESSION,
                piercesShield = false,
                warningMessage = "【L5警報】外部から注入された固定セッションIDへのログイン誘導を検知！",
                executedMessage = "攻撃者の指定したセッションでログインさせられ、アカウントが乗っ取られた！（35ダメージ）",
                mechanism = "攻撃者が事前に用意したセッションIDをURLやフォームで被害者に踏ませ、ログインさせた後にそのセッションIDを使って不正アクセスする。",
                defensePrinciple = "ログイン成功時に必ず新しいセッションIDを再採番・再発行する（Session Regeneration）。",
                examTip = "セッション固定化の唯一かつ確実な対策は「認証成功時のセッションID再発行」。"
            )
        ),
        educationalDetail = "【セ: L5 セッション層】対話（セッション）の開始・維持・終了を制御する層。WebではCookieやトークンで実現されます。CookieへのHttpOnly属性やSecure属性、ログイン時のセッションID再生成が防御の要点です。"
    )

    fun createL6EncodeBypass(): Threat = Threat(
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
                layer = OsiLayer.L6_PRESENTATION,
                piercesShield = false,
                warningMessage = "【L6警報】WAFのチェックをすり抜ける不正エンコード文字列を検知！",
                executedMessage = "バックエンドの文字コード変換差異を突かれ、エスケープが無効化された！（25ダメージ）",
                mechanism = "Shift-JISの2バイト目に0x5C（バックスラッシュ）を含む文字を利用し、文字列エスケープ記号を誤認・相殺させて攻撃文字列を通す。",
                defensePrinciple = "内部エンコーディングのUTF-8統一、文字列バリデーション前の正規化（Normalization）。",
                examTip = "「プレゼンテーション層＝データ表現形式（文字コード・暗号化・圧縮）」の役割を理解すること。"
            ),
            ThreatAttack(
                name = "SSL/TLSダウングレード強要",
                damage = 35,
                layer = OsiLayer.L6_PRESENTATION,
                piercesShield = true,
                warningMessage = "【L6警報】暗号化通信を平文HTTPや脆弱な暗号スイートへ強制引き下げ試行！",
                executedMessage = "平文通信に引き下げられ、暗号化されるはずの機密データが露出！（貫通35ダメージ）",
                mechanism = "中間者攻撃によりハンドシェイクを改ざんし、古いTLSバージョンやRC4/DESなどの脆弱な暗号スイートへ強制的にネゴシエーションさせる。",
                defensePrinciple = "最新TLS（1.3）の強制適用、脆弱な暗号スイートの無効化、HSTSのヘッダー付与。",
                examTip = "POODLEやFREAKなどのダウングレード攻撃。プレゼンテーション層の暗号化プロトコル対策。"
            )
        ),
        educationalDetail = "【プ: L6 プレゼンテーション層】データの表現形式（暗号化、文字コード、圧縮）を変換・統一する層。文字コードの不一致を利用したすり抜けや、SSL/TLSのダウングレード攻撃には、文字エンコーディングの明示的な正規化と最新TLS（1.3）の強制適用で対抗します。"
    )

    fun createL7SqlInjection(): Threat = Threat(
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
                layer = OsiLayer.L7_APPLICATION,
                piercesShield = true,
                warningMessage = "【L7警報】ログインSQLに ' OR '1'='1 -- が注入されようとしている！",
                executedMessage = "SQL構文の改ざんにより管理者として不正ログインされた！（シールド貫通30ダメージ）",
                mechanism = "常に真（恒真式）となる条件式をSQL文に注入し、WHERE句の認証条件を無力化して不正ログインする手口。",
                defensePrinciple = "SQLクエリとパラメータを完全に分離する「プリペアドステートメント（プレースホルダー）」の利用。",
                examTip = "基本・応用・支援士問わず情報セキュリティ試験のド定番。プリペアドステートメントは根本対策、WAFは保険的対策。"
            ),
            ThreatAttack(
                name = "UNION SELECT 全顧客流出",
                damage = 45,
                layer = OsiLayer.L7_APPLICATION,
                piercesShield = false,
                warningMessage = "【L7警報】UNION SELECTによる顧客テーブル一括ダンプクエリが実行寸前！",
                executedMessage = "数万件の機密顧客リストが外部へ流出！企業の信頼性が大暴落！（45ダメージ）",
                mechanism = "UNION演算子を利用して本来の検索結果に別テーブル（会員テーブル等）の検索結果を連結し、機密データを画面上に吐き出させる。",
                defensePrinciple = "バインド機構の徹底、データベース接続ユーザーの権限最小化、エラーメッセージの非表示化。",
                examTip = "UNIONベースのSQLインジェクション手法。最小権限の原則（Principle of Least Privilege）の重要性。"
            )
        ),
        educationalDetail = "【ア: L7 アプリケーション層】HTTPやDNS、SQLなどアプリケーションソフトウェアが直接やり取りする最上位層。SQLインジェクションの根本対策は、動的文字列結合を排除し、プレースホルダーを用いたプリペアドステートメント（静的バインド）を100%徹底することです。"
    )

    /**
     * エンドコンテンツ用: アプセトネデブ複合波状攻撃
     */
    fun createEndgameMultiApt(): Threat = Threat(
        type = ThreatType.ENDGAME_MULTI_APT,
        name = "【最終防衛線】OSI多段波状攻撃：APT複合部隊",
        codeName = "APT-FULL-STACK-INVASION",
        layer = OsiLayer.L7_APPLICATION, // 複合体
        maxHp = 220,
        currentHp = 220,
        avatarAscii = """
  [L7: SQLi]  <=>  [L4: SYN]
       \            /
     [APT MULTI INTRUDER]
       /            \
  [L3: PING]  <=>  [L2: ARP]
        """.trimIndent(),
        storyIntro = "全社非常警報「社内外の全監視装置が赤色点灯！L1からL7までの全層を一斉に突く標的型複合サイバー攻撃（APT）が勃発しました！時間内に全方位迎撃してください！」",
        description = "国家級攻撃グループによる多層波状攻撃！ターンごとに異なるアプセトネデブの層から致命傷を狙ってくる。瞬時の層判別とピンポイント迎撃が求められる過酷な防衛戦！",
        criticalWeakness = ActionId.L7_WAF_PARAM_BIND,
        weaknessHint = "敵の予兆メッセージを熟読し、どの層（ア・プ・セ・ト・ネ・デ・ブ）から攻めてきているか毎ターン見極めろ！",
        attackList = listOf(
            ThreatAttack(
                name = "第1波: L3 Ping of Death波状攻撃",
                damage = 30,
                layer = OsiLayer.L3_NETWORK,
                piercesShield = false,
                warningMessage = "【警報】巨大IPフラグメントがルータを強襲！ネットワーク層（ネ: L3）を固めろ！",
                executedMessage = "L3境界ルータが悲鳴を上げ、ネットワーク遅延が急上昇！（30ダメージ）",
                mechanism = "複合攻撃の足がかりとして、境界ルータの処理能力を枯渇させるL3フラグメント攻撃。",
                defensePrinciple = "境界機器でのICMPドロップおよびパケットサイズ検査。",
                examTip = "多層防御（Defense in Depth）の重要性。"
            ),
            ThreatAttack(
                name = "第2波: L4 TCP SYNフラッド飽和攻撃",
                damage = 35,
                layer = OsiLayer.L4_TRANSPORT,
                piercesShield = false,
                warningMessage = "【警報】未完了TCPハンドシェイクが殺到！トランスポート層（ト: L4）を防護せよ！",
                executedMessage = "サーバのTCP接続ポートが埋め尽くされた！（35ダメージ）",
                mechanism = "接続リソースの枯渇を狙う代表的なL4攻撃。",
                defensePrinciple = "SYN Cookiesおよび接続レートリミットによる防御。",
                examTip = "トランスポート層での接続維持コストを逆手に取ったDoS。"
            ),
            ThreatAttack(
                name = "第3波: L5 不正セッション強奪アタック",
                damage = 35,
                layer = OsiLayer.L5_SESSION,
                piercesShield = true,
                warningMessage = "【警報】認証済みクッキーが不正複製された！セッション層（セ: L5）を死守せよ！",
                executedMessage = "社内管理権限のセッションが盗まれ、重要設定が改ざんされた！（貫通35ダメージ）",
                mechanism = "セッションIDの盗用による権限昇格。",
                defensePrinciple = "HttpOnly, Secure CookieおよびIPバインド検証。",
                examTip = "セッション管理における対話保護の原則。"
            ),
            ThreatAttack(
                name = "第4波: L7 データベース全破壊SQLインジェクション",
                damage = 45,
                layer = OsiLayer.L7_APPLICATION,
                piercesShield = true,
                warningMessage = "【緊急警報】データベース直結の悪意あるSQLが注入寸前！アプリケーション層（ア: L7）を塞げ！",
                executedMessage = "基幹DBのテーブルが破壊され、壊滅的な損害が発生！（貫通45ダメージ）",
                mechanism = "アプリケーションの最深部に直接打撃を与えるL7コード注入攻撃。",
                defensePrinciple = "プレースホルダーによるSQL文と値の完全分離。",
                examTip = "情報セキュリティ最高難度インシデント。最上位層の堅牢化。"
            )
        ),
        educationalDetail = "【アプセトネデブ多層防衛】現代の高度標的型攻撃（APT）は、ひとつの脆弱性だけでなくネットワーク層からアプリ層まで複数の階層を連鎖的に攻撃します。各層ごとの役割に応じた「多層防御」の構築が必須です。"
    )
}
