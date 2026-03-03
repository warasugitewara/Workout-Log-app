# Workout Log App

筋トレ管理Androidアプリ

## 技術スタック
- **言語**: Kotlin
- **UI**: Jetpack Compose + Material3
- **DB**: Room (SQLite)
- **通知**: WorkManager
- **グラフ**: MPAndroidChart
- **アーキテクチャ**: MVVM

## ビルド
```
./gradlew assembleDebug
```

## 機能 (Phase 1 - MVP)
- ✅ トレーニングメニュー管理 (CRUD)
- ✅ トレーニングログ記録
- ✅ 体重推移グラフ
- ✅ リマインド通知 (WorkManager)
- ✅ ダッシュボード (統計サマリー)
- ✅ ユーザー情報管理 (BMI/基礎代謝自動計算)
