package com.example.cyberdefender.model

data class StoryDialogChoice(
    val text: String,
    val nextStepId: Int
)

data class StoryDialogStep(
    val stepId: Int,
    val speaker: String,
    val message: String,
    val choices: List<StoryDialogChoice>,
    val isFinalStep: Boolean = false
)

object StoryCatalog {
    val prologueSteps = listOf(
        StoryDialogStep(
            stepId = 1,
            speaker = "情シス課長",
            message = "「おはよう！今日から我が帝都物産の情報システム部に配属された新人君だな。社内インフラの防衛へようこそ。」",
            choices = listOf(
                StoryDialogChoice("「よろしくお願いします！全力で自社サーバを死守します！」", 2),
                StoryDialogChoice("「サーバ室の鍵と予備のLANケーブルはどこにありますか？」", 3)
            )
        ),
        StoryDialogStep(
            stepId = 2,
            speaker = "情シス課長",
            message = "「うむ、頼もしいな！実は最近、我が社の社内ネットワークを狙う不審なクラッカーの攻撃が急増していてな…」",
            choices = listOf(
                StoryDialogChoice("「OSI基本参照モデル（アプセトネデブ）の知識でピンポイント迎撃ですね！」", 4),
                StoryDialogChoice("「危なくなったら全社ブレーカー落としてPC全台消せば解決ですか？」", 5)
            )
        ),
        StoryDialogStep(
            stepId = 3,
            speaker = "情シス課長",
            message = "「ははは、形から入るタイプか。だが物理管理の意識が高いのは良いことだ。実は早速、社内LANで不穏な動きがあってな…」",
            choices = listOf(
                StoryDialogChoice("「アプセトネデブの各層を意識して調査します！」", 4),
                StoryDialogChoice("「全社の光回線を引っこ抜いて通信を遮断しましょうか？」", 5)
            )
        ),
        StoryDialogStep(
            stepId = 4,
            speaker = "情シス課長",
            message = "「その通りだ！敵が攻めてきた層（アプセトネデブ）を見極め、適切なコマンドで防護しろ。防衛に成功すれば部署予算が降りるから、設備を強化していくんだ。」",
            choices = listOf(
                StoryDialogChoice("「了解しました！第1話の防衛コンソールを起動します！」", 6)
            )
        ),
        StoryDialogStep(
            stepId = 5,
            speaker = "情シス課長",
            message = "「バカモン！そんな過剰防衛をしたら営業部や経理部から怒りの内線が殺到してボーナス全額カットだぞ！適切な層を見極めてピンポイントで防護しろ！」",
            choices = listOf(
                StoryDialogChoice("「肝に銘じます！第1話の防衛コンソールを起動します！」", 6)
            )
        ),
        StoryDialogStep(
            stepId = 6,
            speaker = "情シス課長",
            message = "「よし、最初の任務は第1話『物理層(L1)』だ。営業部長が駐車場で拾ったUSBをPCに挿して大騒ぎしている。出撃して物理接触を断て！」",
            choices = listOf(
                StoryDialogChoice("「出撃します！」", 0)
            ),
            isFinalStep = true
        )
    )
}
