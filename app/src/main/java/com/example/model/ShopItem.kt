package com.example.model

enum class ShopItemType(val label: String) {
    WEAPON("Armamento"),
    ARMOR("Armadura"),
    ACCESSORY("Reliquia"),
    POTION("Pociones & Consumibles"),
    CUSTOM_REWARD("Recompensa de Vida Real")
}

enum class ItemRarity(val label: String) {
    COMMON("Común"),
    RARE("Raro"),
    EPIC("Épico"),
    LEGENDARY("Legendario")
}

data class ShopItem(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    val type: ShopItemType,
    val statBoost: String = "",
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val rarity: ItemRarity = ItemRarity.COMMON,
    val origin: String = "Tienda",
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false,
    val isCustom: Boolean = false
) {
    val isEquippableGear: Boolean
        get() = type == ShopItemType.WEAPON || type == ShopItemType.ARMOR || type == ShopItemType.ACCESSORY
}
