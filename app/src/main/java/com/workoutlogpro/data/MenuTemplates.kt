package com.workoutlogpro.data

import com.workoutlogpro.data.entity.WorkoutMenu

/**
 * 代表的な筋トレメニューのテンプレート集。
 * 初回起動時やメニューが空のときに一括登録できる。
 */
object MenuTemplates {

    val all: List<WorkoutMenu> = listOf(
        // ── 上半身 ──
        WorkoutMenu(name = "ベンチプレス",       category = "上半身", defaultReps = 10, defaultSets = 3, avgTimeSec = 60,  calorieEstimate = 50f,  memo = "大胸筋・三角筋・上腕三頭筋"),
        WorkoutMenu(name = "ダンベルフライ",     category = "上半身", defaultReps = 12, defaultSets = 3, avgTimeSec = 45,  calorieEstimate = 35f,  memo = "大胸筋のストレッチ種目"),
        WorkoutMenu(name = "ショルダープレス",   category = "上半身", defaultReps = 10, defaultSets = 3, avgTimeSec = 50,  calorieEstimate = 40f,  memo = "三角筋前部・中部"),
        WorkoutMenu(name = "ラットプルダウン",   category = "上半身", defaultReps = 10, defaultSets = 3, avgTimeSec = 50,  calorieEstimate = 40f,  memo = "広背筋・大円筋"),
        WorkoutMenu(name = "バーベルカール",     category = "上半身", defaultReps = 12, defaultSets = 3, avgTimeSec = 40,  calorieEstimate = 25f,  memo = "上腕二頭筋"),
        WorkoutMenu(name = "腕立て伏せ",         category = "上半身", defaultReps = 20, defaultSets = 3, avgTimeSec = 45,  calorieEstimate = 30f,  memo = "自重トレーニング・大胸筋"),

        // ── 下半身 ──
        WorkoutMenu(name = "バーベルスクワット", category = "下半身", defaultReps = 10, defaultSets = 3, avgTimeSec = 60,  calorieEstimate = 60f,  memo = "大腿四頭筋・臀筋群"),
        WorkoutMenu(name = "レッグプレス",       category = "下半身", defaultReps = 12, defaultSets = 3, avgTimeSec = 50,  calorieEstimate = 50f,  memo = "大腿四頭筋・ハムストリングス"),
        WorkoutMenu(name = "レッグカール",       category = "下半身", defaultReps = 12, defaultSets = 3, avgTimeSec = 40,  calorieEstimate = 30f,  memo = "ハムストリングス"),
        WorkoutMenu(name = "カーフレイズ",       category = "下半身", defaultReps = 20, defaultSets = 3, avgTimeSec = 30,  calorieEstimate = 20f,  memo = "ふくらはぎ（腓腹筋・ヒラメ筋）"),
        WorkoutMenu(name = "ランジ",             category = "下半身", defaultReps = 10, defaultSets = 3, avgTimeSec = 50,  calorieEstimate = 45f,  memo = "大腿四頭筋・臀筋・バランス"),
        WorkoutMenu(name = "デッドリフト",       category = "下半身", defaultReps = 8,  defaultSets = 3, avgTimeSec = 60,  calorieEstimate = 65f,  memo = "脊柱起立筋・臀筋・ハムストリングス"),

        // ── 有酸素 ──
        WorkoutMenu(name = "ランニング(30分)",   category = "有酸素", defaultReps = 1,  defaultSets = 1, avgTimeSec = 1800, calorieEstimate = 300f, memo = "中強度ランニング"),
        WorkoutMenu(name = "エアロバイク(20分)", category = "有酸素", defaultReps = 1,  defaultSets = 1, avgTimeSec = 1200, calorieEstimate = 200f, memo = "低〜中強度"),
        WorkoutMenu(name = "縄跳び(10分)",       category = "有酸素", defaultReps = 1,  defaultSets = 3, avgTimeSec = 200,  calorieEstimate = 120f, memo = "高強度インターバル向き"),
        WorkoutMenu(name = "バーピージャンプ",   category = "有酸素", defaultReps = 10, defaultSets = 3, avgTimeSec = 60,  calorieEstimate = 50f,  memo = "全身運動・HIIT"),

        // ── 体幹 ──
        WorkoutMenu(name = "プランク",           category = "体幹",   defaultReps = 1,  defaultSets = 3, avgTimeSec = 60,  calorieEstimate = 15f,  memo = "腹横筋・体幹安定"),
        WorkoutMenu(name = "クランチ",           category = "体幹",   defaultReps = 20, defaultSets = 3, avgTimeSec = 40,  calorieEstimate = 20f,  memo = "腹直筋上部"),
        WorkoutMenu(name = "レッグレイズ",       category = "体幹",   defaultReps = 15, defaultSets = 3, avgTimeSec = 40,  calorieEstimate = 20f,  memo = "腹直筋下部・腸腰筋"),
        WorkoutMenu(name = "サイドプランク",     category = "体幹",   defaultReps = 1,  defaultSets = 3, avgTimeSec = 30,  calorieEstimate = 10f,  memo = "腹斜筋・体幹側面"),
        WorkoutMenu(name = "マウンテンクライマー", category = "体幹", defaultReps = 20, defaultSets = 3, avgTimeSec = 45,  calorieEstimate = 30f,  memo = "体幹＋有酸素の複合"),
    )
}
