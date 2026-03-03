package com.workoutlogpro.data

/**
 * 1日のトレーニングルーティンのテンプレート。
 * 複数のメニュー名をまとめて1日分のプランとして提供。
 */
data class RoutineTemplate(
    val name: String,
    val description: String,
    val menuNames: List<String>
)

object RoutineTemplates {

    val all: List<RoutineTemplate> = listOf(
        // ── 筋肥大系 ──
        RoutineTemplate(
            name = "筋肥大（上半身）",
            description = "胸・背中・肩・腕を集中的に鍛える",
            menuNames = listOf("ベンチプレス", "ダンベルフライ", "ショルダープレス", "ラットプルダウン", "バーベルカール")
        ),
        RoutineTemplate(
            name = "筋肥大（下半身）",
            description = "脚・臀部を集中的に鍛える",
            menuNames = listOf("バーベルスクワット", "レッグプレス", "レッグカール", "デッドリフト", "カーフレイズ")
        ),
        RoutineTemplate(
            name = "筋肥大（全身）",
            description = "全身をバランスよく鍛えるフルボディ",
            menuNames = listOf("ベンチプレス", "バーベルスクワット", "ラットプルダウン", "ショルダープレス", "クランチ")
        ),

        // ── 有酸素系 ──
        RoutineTemplate(
            name = "有酸素メイン",
            description = "脂肪燃焼・心肺機能向上に特化",
            menuNames = listOf("ランニング(30分)", "エアロバイク(20分)", "縄跳び(10分)")
        ),
        RoutineTemplate(
            name = "有酸素＋体幹",
            description = "有酸素で燃焼しつつ体幹を引き締める",
            menuNames = listOf("ランニング(30分)", "プランク", "マウンテンクライマー", "クランチ")
        ),

        // ── 体幹強化 ──
        RoutineTemplate(
            name = "体幹強化",
            description = "インナーマッスルを徹底的に鍛える",
            menuNames = listOf("プランク", "サイドプランク", "クランチ", "レッグレイズ", "マウンテンクライマー")
        ),

        // ── 初心者向け ──
        RoutineTemplate(
            name = "初心者（全身・軽め）",
            description = "自重中心で無理なく始める",
            menuNames = listOf("腕立て伏せ", "ランジ", "クランチ", "プランク")
        ),
        RoutineTemplate(
            name = "初心者（有酸素＋筋トレ）",
            description = "軽い有酸素と基本筋トレの組み合わせ",
            menuNames = listOf("エアロバイク(20分)", "腕立て伏せ", "ランジ", "プランク")
        ),

        // ── HIIT系 ──
        RoutineTemplate(
            name = "HIIT（高強度インターバル）",
            description = "短時間で最大効果を狙うHIIT",
            menuNames = listOf("バーピージャンプ", "マウンテンクライマー", "縄跳び(10分)", "プランク")
        ),

        // ── Push/Pull/Legs ──
        RoutineTemplate(
            name = "Push Day（押す動作）",
            description = "胸・肩・三頭筋を集中",
            menuNames = listOf("ベンチプレス", "ダンベルフライ", "ショルダープレス", "腕立て伏せ")
        ),
        RoutineTemplate(
            name = "Pull Day（引く動作）",
            description = "背中・二頭筋を集中",
            menuNames = listOf("ラットプルダウン", "デッドリフト", "バーベルカール")
        ),
        RoutineTemplate(
            name = "Leg Day（脚の日）",
            description = "下半身を徹底的に追い込む",
            menuNames = listOf("バーベルスクワット", "レッグプレス", "レッグカール", "ランジ", "カーフレイズ")
        ),
    )
}
