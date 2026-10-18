package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.HabitQuestRepository
import com.example.model.Boss
import com.example.model.Enemy
import com.example.model.Hero
import com.example.model.ItemRarity
import com.example.model.Quest
import com.example.model.QuestDifficulty
import com.example.model.QuestType
import com.example.model.ShopItem
import com.example.model.ShopItemType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class BottomNavTab(val title: String) {
    QUESTS("Misiones"),
    HERO("Héroe"),
    SHOP("Tienda"),
    BOSS("Jefes")
}

data class FloatingReward(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val isPositive: Boolean = true,
    val goldChange: Int = 0,
    val xpChange: Int = 0,
    val hpChange: Int = 0
)

enum class ChestRewardType {
    DOUBLE_GOLD,
    FULL_HEAL
}

class HabitQuestViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HabitQuestRepository(application)

    private val _hero = MutableStateFlow(repository.loadHero())
    val hero: StateFlow<Hero> = _hero.asStateFlow()

    private val _enemies = MutableStateFlow(repository.loadEnemies())
    val enemies: StateFlow<List<Enemy>> = _enemies.asStateFlow()

    private val _quests = MutableStateFlow(repository.loadQuests())
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    private val _boss = MutableStateFlow(repository.loadBoss())
    val boss: StateFlow<Boss> = _boss.asStateFlow()

    private val _shopItems = MutableStateFlow(repository.loadShopItems())
    val shopItems: StateFlow<List<ShopItem>> = _shopItems.asStateFlow()

    init {
        _hero.value = recalculateHeroEquipment(_hero.value, _shopItems.value)
    }

    private fun recalculateHeroEquipment(hero: Hero, items: List<ShopItem>): Hero {
        val equippedGear = items.filter { it.isPurchased && it.isEquipped && it.isEquippableGear }
        val totalEquipAtk = equippedGear.sumOf { it.attackBonus }
        val totalEquipDef = equippedGear.sumOf { it.defenseBonus }

        val weaponName = equippedGear.firstOrNull { it.type == ShopItemType.WEAPON }?.name ?: "Sin arma equipada"
        val armorName = equippedGear.firstOrNull { it.type == ShopItemType.ARMOR }?.name ?: "Sin armadura equipada"
        val accessoryName = equippedGear.firstOrNull { it.type == ShopItemType.ACCESSORY }?.name ?: "Sin reliquia equipada"

        return hero.copy(
            equippedWeapon = weaponName,
            equippedArmor = armorName,
            equippedAccessory = accessoryName,
            equipmentAttackBonus = totalEquipAtk,
            equipmentDefenseBonus = totalEquipDef
        )
    }

    private val _currentTab = MutableStateFlow(BottomNavTab.QUESTS)
    val currentTab: StateFlow<BottomNavTab> = _currentTab.asStateFlow()

    private val _freeSpins = MutableStateFlow(repository.loadFreeSpins())
    val freeSpins: StateFlow<Int> = _freeSpins.asStateFlow()

    private val _floatingReward = MutableStateFlow<FloatingReward?>(null)
    val floatingReward: StateFlow<FloatingReward?> = _floatingReward.asStateFlow()

    private val _levelUpCelebration = MutableStateFlow<Int?>(null)
    val levelUpCelebration: StateFlow<Int?> = _levelUpCelebration.asStateFlow()

    // Modals
    private val _isChestModalOpen = MutableStateFlow(false)
    val isChestModalOpen: StateFlow<Boolean> = _isChestModalOpen.asStateFlow()

    private val _isWheelModalOpen = MutableStateFlow(false)
    val isWheelModalOpen: StateFlow<Boolean> = _isWheelModalOpen.asStateFlow()

    private val _isAddTaskModalOpen = MutableStateFlow(false)
    val isAddTaskModalOpen: StateFlow<Boolean> = _isAddTaskModalOpen.asStateFlow()

    private val _isAddRewardModalOpen = MutableStateFlow(false)
    val isAddRewardModalOpen: StateFlow<Boolean> = _isAddRewardModalOpen.asStateFlow()

    fun selectTab(tab: BottomNavTab) {
        _currentTab.value = tab
    }

    fun openChestModal() {
        _isChestModalOpen.value = true
    }

    fun closeChestModal() {
        _isChestModalOpen.value = false
    }

    fun openWheelModal() {
        _isWheelModalOpen.value = true
    }

    fun closeWheelModal() {
        _isWheelModalOpen.value = false
    }

    fun openAddTaskModal() {
        _isAddTaskModalOpen.value = true
    }

    fun closeAddTaskModal() {
        _isAddTaskModalOpen.value = false
    }

    fun openAddRewardModal() {
        _isAddRewardModalOpen.value = true
    }

    fun closeAddRewardModal() {
        _isAddRewardModalOpen.value = false
    }

    fun clearFloatingReward() {
        _floatingReward.value = null
    }

    fun clearLevelUp() {
        _levelUpCelebration.value = null
    }

    fun completeQuest(quest: Quest) {
        if (quest.isCompleted) return

        val currentHero = _hero.value
        val xpGain = quest.difficulty.xp
        val goldGain = quest.difficulty.gold
        val dmg = quest.damageDealtToBoss + (currentHero.strength * 4)

        var newXp = currentHero.xp + xpGain
        var newLevel = currentHero.level
        var newMaxXp = currentHero.maxXp
        var newHp = currentHero.hp
        var newMaxHp = currentHero.maxHp
        var newStr = currentHero.strength
        var newDef = currentHero.defense
        var newAgi = currentHero.agility
        var didLevelUp = false

        while (newXp >= newMaxXp) {
            newXp -= newMaxXp
            newLevel += 1
            newMaxXp = (newMaxXp * 1.35).toInt()
            newMaxHp += 15
            newHp = newMaxHp
            newStr += 2
            newDef += 2
            newAgi += 1
            didLevelUp = true
        }

        val updatedMissionsCount = currentHero.missionsCompletedCount + 1
        val updatedAttackBonus = currentHero.attackBonusFromMissions + 3

        val updatedHero = currentHero.copy(
            level = newLevel,
            xp = newXp,
            maxXp = newMaxXp,
            hp = newHp,
            maxHp = newMaxHp,
            gold = currentHero.gold + goldGain,
            streakDays = currentHero.streakDays + 1,
            strength = newStr,
            defense = newDef,
            agility = newAgi,
            missionsCompletedCount = updatedMissionsCount,
            attackBonusFromMissions = updatedAttackBonus
        )

        // Attack active enemy
        val currentEnemies = _enemies.value
        var enemyDefeatedMsg = ""
        var enemyGoldBonus = 0
        var enemyXpBonus = 0
        val updatedEnemies = currentEnemies.map { enemy ->
            if (!enemy.isDefeated && enemyDefeatedMsg.isEmpty()) {
                val heroDamage = updatedHero.totalAttackPower
                val enemyHpRemaining = (enemy.currentHp - heroDamage).coerceAtLeast(0)
                if (enemyHpRemaining == 0) {
                    enemyDefeatedMsg = " • 💥 ¡Eliminaste a ${enemy.name} (+${enemy.rewardGold} Oro, +${enemy.rewardXp} XP)!"
                    enemyGoldBonus = enemy.rewardGold
                    enemyXpBonus = enemy.rewardXp
                    enemy.copy(currentHp = 0, isDefeated = true)
                } else {
                    enemyDefeatedMsg = " • 🗡️ -$heroDamage HP a ${enemy.name}"
                    enemy.copy(currentHp = enemyHpRemaining)
                }
            } else enemy
        }

        // Damage boss
        val currentBoss = _boss.value
        val newBossHp = (currentBoss.currentHp - dmg).coerceAtLeast(0)
        val bossDefeated = newBossHp == 0 && !currentBoss.defeated
        val updatedBoss = currentBoss.copy(
            currentHp = newBossHp,
            defeated = if (bossDefeated) true else currentBoss.defeated
        )

        // Update quest list
        val updatedQuests = _quests.value.map {
            if (it.id == quest.id) {
                it.copy(isCompleted = true, streak = it.streak + 1)
            } else it
        }

        // Award an equippable Loot Item on quest completion
        val currentShop = _shopItems.value
        val ownedIds = currentShop.map { it.id }.toSet()
        val targetRarity = when (quest.difficulty) {
            QuestDifficulty.EASY -> ItemRarity.RARE
            QuestDifficulty.MEDIUM -> ItemRarity.RARE
            QuestDifficulty.HARD -> ItemRarity.EPIC
            QuestDifficulty.EPIC -> ItemRarity.LEGENDARY
        }
        val pool = repository.getQuestLootRewardsPool().filter { it.id !in ownedIds }
        val droppedItem = pool.firstOrNull { it.rarity == targetRarity }
            ?: pool.firstOrNull()
            ?: run {
                val atk = (quest.difficulty.xp / 5) + 8
                val def = (quest.difficulty.gold / 5) + 6
                val slotType = when (updatedMissionsCount % 3) {
                    0 -> ShopItemType.WEAPON
                    1 -> ShopItemType.ARMOR
                    else -> ShopItemType.ACCESSORY
                }
                ShopItem(
                    id = "loot_forged_" + UUID.randomUUID().toString().take(6),
                    name = "${slotType.label} de ${quest.category} Nv.$newLevel",
                    description = "Forjado al completar la misión '${quest.title}'.",
                    cost = 400,
                    type = slotType,
                    statBoost = "+$atk ATK, +$def DEF",
                    attackBonus = atk,
                    defenseBonus = def,
                    rarity = targetRarity,
                    origin = "Botín de Misión",
                    isPurchased = true,
                    isEquipped = false
                )
            }

        val updatedShop = currentShop + droppedItem
        val lootMsg = " • 🎁 Botín ganado: ${droppedItem.name} (${droppedItem.statBoost})"

        // Apply bonus if boss defeated or enemy defeated
        val finalHero = updatedHero.copy(
            gold = updatedHero.gold + (if (bossDefeated) currentBoss.rewardGold else 0) + enemyGoldBonus,
            xp = updatedHero.xp + (if (bossDefeated) currentBoss.rewardXp else 0) + enemyXpBonus
        )

        _hero.value = finalHero
        _boss.value = updatedBoss
        _quests.value = updatedQuests
        _enemies.value = updatedEnemies
        _shopItems.value = updatedShop

        viewModelScope.launch {
            repository.saveHero(finalHero)
            repository.saveBoss(updatedBoss)
            repository.saveQuests(updatedQuests)
            repository.saveEnemies(updatedEnemies)
            repository.saveShopItems(updatedShop)
        }

        val rewardMsg = if (bossDefeated) {
            "¡JEFE DERROTADO! +${xpGain + currentBoss.rewardXp} XP, +${goldGain + currentBoss.rewardGold} Oro!$lootMsg"
        } else {
            "+$xpGain XP  +$goldGain Oro  ⚔️ Daño +3 (ATK: ${finalHero.totalAttackPower})$enemyDefeatedMsg$lootMsg"
        }

        _floatingReward.value = FloatingReward(
            text = rewardMsg,
            isPositive = true,
            goldChange = goldGain + (if (bossDefeated) currentBoss.rewardGold else 0) + enemyGoldBonus,
            xpChange = xpGain + (if (bossDefeated) currentBoss.rewardXp else 0) + enemyXpBonus
        )

        if (didLevelUp) {
            _levelUpCelebration.value = newLevel
        }
    }

    fun failHabit(quest: Quest) {
        val currentHero = _hero.value

        // Check Divine Shield
        if (currentHero.hasDivineShield) {
            val unshieldedHero = currentHero.copy(hasDivineShield = false)
            _hero.value = unshieldedHero
            viewModelScope.launch {
                repository.saveHero(unshieldedHero)
            }
            _floatingReward.value = FloatingReward(
                text = "🛡️ ¡Tu Escudo Divino de Anuncio bloqueó el ataque enemigo y protegió tu HP!",
                isPositive = true
            )
            return
        }

        // Active enemy attacks hero, mitigated by totalDefensePower
        val activeEnemy = _enemies.value.firstOrNull { !it.isDefeated }
        val enemyExtra = if (activeEnemy != null) activeEnemy.attackDamage / 2 else 0
        val rawPenalty = quest.difficulty.hpPenalty + enemyExtra
        val defenseReduction = currentHero.damageReductionFromDefense
        val totalPenalty = (rawPenalty - defenseReduction).coerceAtLeast(2)

        val newHp = (currentHero.hp - totalPenalty).coerceAtLeast(0)
        val updatedHero = currentHero.copy(hp = newHp)

        _hero.value = updatedHero
        viewModelScope.launch {
            repository.saveHero(updatedHero)
        }

        val msg = if (newHp == 0) {
            "💀 ¡TU HÉROE HA CAÍDO! Los enemigos han drenado su vida. ¡Usa 'Revivir con Anuncio' para resucitarlo!"
        } else {
            "Hábito fallido: -$totalPenalty HP (🛡️ -$defenseReduction absorbido por Defensa ${currentHero.totalDefensePower})"
        }

        _floatingReward.value = FloatingReward(
            text = msg,
            isPositive = false,
            hpChange = -totalPenalty
        )
    }

    fun reviveHeroWithAd() {
        val currentHero = _hero.value
        val resurrected = currentHero.copy(
            hp = currentHero.maxHp,
            hasDivineShield = true
        )
        _hero.value = resurrected
        viewModelScope.launch {
            repository.saveHero(resurrected)
        }
        _floatingReward.value = FloatingReward(
            text = "✨ ¡POCIÓN DE FÉNIX PATROCINADA! Héroe resucitado al 100% de HP con Escudo Divino.",
            isPositive = true
        )
    }

    fun activateDivineShieldWithAd() {
        val currentHero = _hero.value
        val shielded = currentHero.copy(hasDivineShield = true)
        _hero.value = shielded
        viewModelScope.launch {
            repository.saveHero(shielded)
        }
        _floatingReward.value = FloatingReward(
            text = "🛡️ ¡Escudo Divino de Supervivencia activo! Inmunidad total ante el próximo fallo.",
            isPositive = true
        )
    }

    fun attackEnemyDirect() {
        val currentHero = _hero.value
        val currentEnemies = _enemies.value
        val activeEnemy = currentEnemies.firstOrNull { !it.isDefeated } ?: return

        val dmg = currentHero.totalAttackPower
        val remainingHp = (activeEnemy.currentHp - dmg).coerceAtLeast(0)
        val isKilled = remainingHp == 0

        val updatedEnemies = currentEnemies.map {
            if (it.id == activeEnemy.id) {
                it.copy(currentHp = remainingHp, isDefeated = isKilled)
            } else it
        }

        val finalHero = if (isKilled) {
            currentHero.copy(
                gold = currentHero.gold + activeEnemy.rewardGold,
                xp = currentHero.xp + activeEnemy.rewardXp
            )
        } else currentHero

        _enemies.value = updatedEnemies
        _hero.value = finalHero

        viewModelScope.launch {
            repository.saveHero(finalHero)
            repository.saveEnemies(updatedEnemies)
        }

        val msg = if (isKilled) {
            "💥 ¡Eliminaste a ${activeEnemy.name}! Ganaste +${activeEnemy.rewardGold} Oro y +${activeEnemy.rewardXp} XP."
        } else {
            "⚔️ Asestaste -$dmg HP a ${activeEnemy.name} (Salud restante: $remainingHp/${activeEnemy.maxHp})."
        }
        _floatingReward.value = FloatingReward(text = msg, isPositive = true)
    }

    fun respawnEnemies() {
        val refreshed = repository.getDefaultEnemies()
        _enemies.value = refreshed
        viewModelScope.launch {
            repository.saveEnemies(refreshed)
        }
        _floatingReward.value = FloatingReward(
            text = "👹 ¡Una nueva horda de enemigos acecha en el abismo!",
            isPositive = true
        )
    }

    fun resetDailyHabits() {
        val updatedQuests = _quests.value.map {
            if (it.type == QuestType.HABIT) it.copy(isCompleted = false) else it
        }
        _quests.value = updatedQuests
        viewModelScope.launch {
            repository.saveQuests(updatedQuests)
        }
        _floatingReward.value = FloatingReward(
            text = "¡Nuevo Día! Hábitos diarios reactivados para la batalla.",
            isPositive = true
        )
    }

    fun createQuest(
        title: String,
        description: String,
        type: QuestType,
        difficulty: QuestDifficulty,
        category: String
    ) {
        val newQuest = Quest(
            id = "q_" + UUID.randomUUID().toString().take(8),
            title = title,
            description = description,
            type = type,
            difficulty = difficulty,
            category = category.ifBlank { "Personal" },
            isCompleted = false,
            streak = 0,
            damageDealtToBoss = difficulty.xp * 2
        )
        val updated = listOf(newQuest) + _quests.value
        _quests.value = updated
        viewModelScope.launch {
            repository.saveQuests(updated)
        }
        closeAddTaskModal()
        _floatingReward.value = FloatingReward(
            text = "¡Misión añadida al Grimorio de Hábitos!",
            isPositive = true
        )
    }

    fun deleteQuest(questId: String) {
        val updated = _quests.value.filter { it.id != questId }
        _quests.value = updated
        viewModelScope.launch {
            repository.saveQuests(updated)
        }
    }

    fun purchaseShopItem(item: ShopItem) {
        val currentHero = _hero.value
        if (currentHero.gold < item.cost) {
            _floatingReward.value = FloatingReward(
                text = "¡Oro insuficiente! Necesitas ${item.cost} de oro.",
                isPositive = false
            )
            return
        }

        val newGold = currentHero.gold - item.cost
        var updatedHero = currentHero.copy(gold = newGold)

        when (item.type) {
            ShopItemType.POTION -> {
                val healAmount = if (item.id.contains("full")) updatedHero.maxHp else 50
                val healedHp = (updatedHero.hp + healAmount).coerceAtMost(updatedHero.maxHp)
                updatedHero = updatedHero.copy(hp = healedHp)
                _floatingReward.value = FloatingReward(
                    text = "¡Poción consumida! Vida restaurada a $healedHp/${updatedHero.maxHp}",
                    isPositive = true
                )
            }
            ShopItemType.CUSTOM_REWARD -> {
                _floatingReward.value = FloatingReward(
                    text = "¡Recompensa Canjeada! Disfruta de: ${item.name}",
                    isPositive = true
                )
            }
            ShopItemType.WEAPON, ShopItemType.ARMOR, ShopItemType.ACCESSORY -> {
                val updatedShop = _shopItems.value.map {
                    if (it.id == item.id) it.copy(isPurchased = true, isEquipped = true)
                    else if (it.type == item.type) it.copy(isEquipped = false)
                    else it
                }
                _shopItems.value = updatedShop
                updatedHero = recalculateHeroEquipment(updatedHero, updatedShop)
                viewModelScope.launch { repository.saveShopItems(updatedShop) }
                _floatingReward.value = FloatingReward(
                    text = "¡${item.name} comprada y equipada! (ATK: ${updatedHero.totalAttackPower} | DEF: ${updatedHero.totalDefensePower})",
                    isPositive = true
                )
            }
        }

        _hero.value = updatedHero
        viewModelScope.launch {
            repository.saveHero(updatedHero)
        }
    }

    fun equipShopItem(item: ShopItem) {
        val currentHero = _hero.value
        val willEquip = !item.isEquipped
        val updatedShop = _shopItems.value.map {
            if (it.id == item.id) {
                it.copy(isEquipped = willEquip)
            } else if (willEquip && it.type == item.type) {
                it.copy(isEquipped = false)
            } else {
                it
            }
        }
        _shopItems.value = updatedShop
        val updatedHero = recalculateHeroEquipment(currentHero, updatedShop)
        _hero.value = updatedHero
        viewModelScope.launch {
            repository.saveHero(updatedHero)
            repository.saveShopItems(updatedShop)
        }

        val feedbackMsg = if (willEquip) {
            "⚔️ Equipado: ${item.name} (${item.statBoost}) • ATK Total: ${updatedHero.totalAttackPower} | DEF Total: ${updatedHero.totalDefensePower}"
        } else {
            "🛡️ Desequipado: ${item.name} • ATK Total: ${updatedHero.totalAttackPower} | DEF Total: ${updatedHero.totalDefensePower}"
        }
        _floatingReward.value = FloatingReward(
            text = feedbackMsg,
            isPositive = true
        )
    }

    fun addCustomReward(name: String, description: String, cost: Int) {
        val newItem = ShopItem(
            id = "custom_" + UUID.randomUUID().toString().take(8),
            name = name,
            description = description.ifBlank { "Recompensa personalizada" },
            cost = cost.coerceAtLeast(10),
            type = ShopItemType.CUSTOM_REWARD,
            statBoost = "Recompensa Propia",
            isPurchased = false,
            isCustom = true
        )
        val updated = _shopItems.value + newItem
        _shopItems.value = updated
        viewModelScope.launch {
            repository.saveShopItems(updated)
        }
        closeAddRewardModal()
        _floatingReward.value = FloatingReward(
            text = "¡Recompensa \"$name\" agregada a la Tienda!",
            isPositive = true
        )
    }

    fun claimChestReward(rewardType: ChestRewardType) {
        val currentHero = _hero.value
        val updatedHero = when (rewardType) {
            ChestRewardType.DOUBLE_GOLD -> {
                val bonus = currentHero.gold.coerceAtLeast(150)
                currentHero.copy(gold = currentHero.gold + bonus)
            }
            ChestRewardType.FULL_HEAL -> {
                currentHero.copy(hp = currentHero.maxHp)
            }
        }
        _hero.value = updatedHero
        viewModelScope.launch {
            repository.saveHero(updatedHero)
        }
        closeChestModal()

        val text = if (rewardType == ChestRewardType.DOUBLE_GOLD) {
            "¡Cofre Misterioso Abierto! Oro del día duplicado (+${currentHero.gold} Oro)."
        } else {
            "¡Cofre Misterioso Abierto! Vida (HP) restaurada al 100% (${currentHero.maxHp} HP)."
        }
        _floatingReward.value = FloatingReward(text = text, isPositive = true)
    }

    fun applySpinPrize(prizeIndex: Int, isAdSpin: Boolean) {
        val currentHero = _hero.value
        var updatedHero = currentHero
        var prizeText = ""

        when (prizeIndex) {
            0 -> {
                val gold = 200
                updatedHero = updatedHero.copy(gold = updatedHero.gold + gold)
                prizeText = "+$gold Oro"
            }
            1 -> {
                val xp = 150
                updatedHero = updatedHero.copy(xp = updatedHero.xp + xp)
                prizeText = "+$xp XP"
            }
            2 -> {
                val heal = updatedHero.maxHp
                updatedHero = updatedHero.copy(hp = heal)
                prizeText = "Poción Divina (100% HP)"
            }
            3 -> {
                val gold = 500
                updatedHero = updatedHero.copy(gold = updatedHero.gold + gold)
                prizeText = "¡BOTE DE ORO! +$gold Oro"
            }
            4 -> {
                val xp = 300
                updatedHero = updatedHero.copy(xp = updatedHero.xp + xp)
                prizeText = "¡Savia Cósmica! +$xp XP"
            }
            else -> {
                val gold = 100
                updatedHero = updatedHero.copy(gold = updatedHero.gold + gold)
                prizeText = "+$gold Oro"
            }
        }

        if (!isAdSpin && _freeSpins.value > 0) {
            val remaining = _freeSpins.value - 1
            _freeSpins.value = remaining
            viewModelScope.launch { repository.saveFreeSpins(remaining) }
        }

        _hero.value = updatedHero
        viewModelScope.launch {
            repository.saveHero(updatedHero)
        }
        _floatingReward.value = FloatingReward(
            text = "¡Ruleta de la Suerte: Has ganado $prizeText!",
            isPositive = true
        )
    }

    fun attackBossSpecial() {
        val currentBoss = _boss.value
        val currentHero = _hero.value
        val dmg = 350 + (currentHero.strength * 6)
        val newHp = (currentBoss.currentHp - dmg).coerceAtLeast(0)
        val bossDefeated = newHp == 0 && !currentBoss.defeated
        val updatedBoss = currentBoss.copy(
            currentHp = newHp,
            defeated = if (bossDefeated) true else currentBoss.defeated
        )

        val finalHero = if (bossDefeated) {
            currentHero.copy(
                gold = currentHero.gold + currentBoss.rewardGold,
                xp = currentHero.xp + currentBoss.rewardXp
            )
        } else currentHero

        _boss.value = updatedBoss
        _hero.value = finalHero
        viewModelScope.launch {
            repository.saveBoss(updatedBoss)
            repository.saveHero(finalHero)
        }

        val msg = if (bossDefeated) {
            "¡GOLPE DEFINITIVO! ¡Jefe eliminado (+${currentBoss.rewardGold} Oro, +${currentBoss.rewardXp} XP)!"
        } else {
            "¡Corte de Plasma Crítico! -$dmg HP al Gólem."
        }
        _floatingReward.value = FloatingReward(text = msg, isPositive = true)
    }

    fun respawnBoss() {
        val fresh = Boss(
            id = "boss_" + UUID.randomUUID().toString().take(6),
            name = "Ciber-Dragón del Caos",
            title = "Jefe Semanal - Nivel 6",
            currentHp = 4000,
            maxHp = 4000,
            rewardGold = 800,
            rewardXp = 600,
            defeated = false,
            description = "Un dragón cibernético que incita a la dispersión mental. Requiere una racha implacable para derribarlo.",
            daysRemaining = 7
        )
        _boss.value = fresh
        viewModelScope.launch { repository.saveBoss(fresh) }
        _floatingReward.value = FloatingReward(
            text = "¡Un nuevo Jefe Semanal ha emergido en el portal!",
            isPositive = true
        )
    }
}
