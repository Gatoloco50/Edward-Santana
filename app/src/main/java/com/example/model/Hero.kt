package com.example.model

data class Hero(
    val name: String = "Ciber-Paladín",
    val title: String = "Cazador de Hábitos",
    val level: Int = 3,
    val xp: Int = 320,
    val maxXp: Int = 500,
    val hp: Int = 85,
    val maxHp: Int = 100,
    val gold: Int = 450,
    val streakDays: Int = 5,
    val strength: Int = 18,
    val defense: Int = 14,
    val agility: Int = 12,
    val equippedWeapon: String = "Espada de Plasma Esmeralda",
    val equippedArmor: String = "Coraza de Nanofibra",
    val equippedAccessory: String = "Anillo de Enfoque Neón",
    val avatarClass: String = "CYBER_PALADIN",
    val missionsCompletedCount: Int = 8,
    val attackBonusFromMissions: Int = 24,
    val equipmentAttackBonus: Int = 20,
    val equipmentDefenseBonus: Int = 19,
    val hasDivineShield: Boolean = false
) {
    val totalAttackPower: Int
        get() = strength + attackBonusFromMissions + equipmentAttackBonus

    val totalDefensePower: Int
        get() = defense + equipmentDefenseBonus

    val damageReductionFromDefense: Int
        get() = (totalDefensePower / 4).coerceAtLeast(1)

    val isDead: Boolean
        get() = hp <= 0
}
