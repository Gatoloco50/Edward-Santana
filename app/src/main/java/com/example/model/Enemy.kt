package com.example.model

data class Enemy(
    val id: String,
    val name: String,
    val title: String,
    val currentHp: Int,
    val maxHp: Int,
    val attackDamage: Int,
    val threatLevel: String = "Media",
    val rewardGold: Int = 40,
    val rewardXp: Int = 50,
    val isDefeated: Boolean = false,
    val description: String = "Acecha en las sombras esperando que rompas tus hábitos para drenar tu vitalidad."
)
