package com.example.model

enum class QuestType(val displayName: String) {
    HABIT("Hábito Diario"),
    ONCE("Misión Única")
}

enum class QuestDifficulty(
    val label: String,
    val xp: Int,
    val gold: Int,
    val hpPenalty: Int
) {
    EASY("Fácil", 25, 20, 8),
    MEDIUM("Media", 50, 45, 15),
    HARD("Difícil", 100, 90, 25),
    EPIC("Épica", 200, 180, 40)
}

data class Quest(
    val id: String,
    val title: String,
    val description: String = "",
    val type: QuestType = QuestType.HABIT,
    val difficulty: QuestDifficulty = QuestDifficulty.MEDIUM,
    val category: String = "Salud",
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val damageDealtToBoss: Int = 120
)
