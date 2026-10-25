# Cyber Defender (新人情シス奮闘記 〜アプセトネデブ防衛戦〜)

Windows XP風UI & CRT（ブラウン管）走査線エフェクトで贈る、サイバーセキュリティ学習バトルゲーム。

## プロジェクト概要
- **技術スタック**: Kotlin 2.2+, Jetpack Compose, Material3, Navigation3, Android SDK 36 (Java 21)
- **パッケージ**: `com.example.cyberdefender`
- **対象プラットフォーム**: Android (スマートフォン・タブレット)

## ゲームコンセプト
- **世界観**: 中堅企業「帝都物産」の新人情シスとして、社内サーバを狙うサイバー攻撃を迎撃する。
- **バトルシステム**: OSI基本参照モデル7層（アプセトネデブ: L7アプリ〜L1物理）の敵に対し、適切な層の防護コマンドを講じるターン制バトル。
- **過剰防衛ペナルティ**: 全社光回線切断などの過剰な措置をとるとクリアにはなるが、社員から激怒の内線が殺到しボーナスが大幅減額される。

## ビルドコマンド
- テスト実行: `./gradlew test`
- APKビルド: `./gradlew assembleDebug`
- エミュレータ実行: `android run --apk="app/build/outputs/apk/debug/app-debug.apk"`
