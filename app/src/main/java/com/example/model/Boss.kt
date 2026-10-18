package com.example.model

data class Boss(
    val id: String = "boss_procrastination_golem",
    val name: String = "Gólem de la Procrastinación",
    val title: String = "Jefe Semanal - Nivel 5",
    val currentHp: Int = 1850,
    val maxHp: Int = 3000,
    val rewardGold: Int = 500,
    val rewardXp: Int = 400,
    val defeated: Boolean = false,
    val description: String = "Una mole de pereza y distracciones digitales. Alimenta su poder cuando postergas tus tareas.",
    val daysRemaining: Int = 3,
    val specialAttack: String = "Onda de Somnolencia (Drena 10 HP al fallar hábitos)",
    val elementalWeakness: String = "Enfoque Esmeralda"
)
