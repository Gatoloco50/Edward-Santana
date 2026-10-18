package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Boss
import com.example.model.Enemy
import com.example.model.Hero
import com.example.model.ItemRarity
import com.example.model.Quest
import com.example.model.QuestDifficulty
import com.example.model.QuestType
import com.example.model.ShopItem
import com.example.model.ShopItemType
import org.json.JSONArray
import org.json.JSONObject

class HabitQuestRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("habitquest_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_HERO = "hq_hero"
        private const val KEY_QUESTS = "hq_quests"
        private const val KEY_BOSS = "hq_boss"
        private const val KEY_SHOP = "hq_shop_v2"
        private const val KEY_FREE_SPINS = "hq_free_spins"
    }

    fun loadHero(): Hero {
        val jsonStr = prefs.getString(KEY_HERO, null) ?: return Hero()
        return try {
            val obj = JSONObject(jsonStr)
            Hero(
                name = obj.optString("name", "Ciber-Paladín"),
                title = obj.optString("title", "Cazador de Hábitos"),
                level = obj.optInt("level", 3),
                xp = obj.optInt("xp", 320),
                maxXp = obj.optInt("maxXp", 500),
                hp = obj.optInt("hp", 85),
                maxHp = obj.optInt("maxHp", 100),
                gold = obj.optInt("gold", 450),
                streakDays = obj.optInt("streakDays", 5),
                strength = obj.optInt("strength", 18),
                defense = obj.optInt("defense", 14),
                agility = obj.optInt("agility", 12),
                equippedWeapon = obj.optString("equippedWeapon", "Espada de Plasma Esmeralda"),
                equippedArmor = obj.optString("equippedArmor", "Coraza de Nanofibra"),
                equippedAccessory = obj.optString("equippedAccessory", "Anillo de Enfoque Neón"),
                avatarClass = obj.optString("avatarClass", "CYBER_PALADIN"),
                missionsCompletedCount = obj.optInt("missionsCompletedCount", 8),
                attackBonusFromMissions = obj.optInt("attackBonusFromMissions", 24),
                equipmentAttackBonus = obj.optInt("equipmentAttackBonus", 20),
                equipmentDefenseBonus = obj.optInt("equipmentDefenseBonus", 19),
                hasDivineShield = obj.optBoolean("hasDivineShield", false)
            )
        } catch (_: Exception) {
            Hero()
        }
    }

    fun saveHero(hero: Hero) {
        val obj = JSONObject().apply {
            put("name", hero.name)
            put("title", hero.title)
            put("level", hero.level)
            put("xp", hero.xp)
            put("maxXp", hero.maxXp)
            put("hp", hero.hp)
            put("maxHp", hero.maxHp)
            put("gold", hero.gold)
            put("streakDays", hero.streakDays)
            put("strength", hero.strength)
            put("defense", hero.defense)
            put("agility", hero.agility)
            put("equippedWeapon", hero.equippedWeapon)
            put("equippedArmor", hero.equippedArmor)
            put("equippedAccessory", hero.equippedAccessory)
            put("avatarClass", hero.avatarClass)
            put("missionsCompletedCount", hero.missionsCompletedCount)
            put("attackBonusFromMissions", hero.attackBonusFromMissions)
            put("equipmentAttackBonus", hero.equipmentAttackBonus)
            put("equipmentDefenseBonus", hero.equipmentDefenseBonus)
            put("hasDivineShield", hero.hasDivineShield)
        }
        prefs.edit().putString(KEY_HERO, obj.toString()).apply()
    }

    fun loadQuests(): List<Quest> {
        val jsonStr = prefs.getString(KEY_QUESTS, null)
        if (jsonStr == null) {
            return getDefaultQuests()
        }
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Quest>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val typeStr = obj.optString("type", "HABIT")
                val diffStr = obj.optString("difficulty", "MEDIUM")
                list.add(
                    Quest(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        description = obj.optString("description", ""),
                        type = runCatching { QuestType.valueOf(typeStr) }.getOrDefault(QuestType.HABIT),
                        difficulty = runCatching { QuestDifficulty.valueOf(diffStr) }.getOrDefault(QuestDifficulty.MEDIUM),
                        category = obj.optString("category", "General"),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        streak = obj.optInt("streak", 0),
                        damageDealtToBoss = obj.optInt("damageDealtToBoss", 100)
                    )
                )
            }
            if (list.isEmpty()) getDefaultQuests() else list
        } catch (_: Exception) {
            getDefaultQuests()
        }
    }

    fun saveQuests(quests: List<Quest>) {
        val array = JSONArray()
        for (q in quests) {
            val obj = JSONObject().apply {
                put("id", q.id)
                put("title", q.title)
                put("description", q.description)
                put("type", q.type.name)
                put("difficulty", q.difficulty.name)
                put("category", q.category)
                put("isCompleted", q.isCompleted)
                put("streak", q.streak)
                put("damageDealtToBoss", q.damageDealtToBoss)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_QUESTS, array.toString()).apply()
    }

    fun loadBoss(): Boss {
        val jsonStr = prefs.getString(KEY_BOSS, null) ?: return Boss()
        return try {
            val obj = JSONObject(jsonStr)
            Boss(
                id = obj.optString("id", "boss_procrastination_golem"),
                name = obj.optString("name", "Gólem de la Procrastinación"),
                title = obj.optString("title", "Jefe Semanal - Nivel 5"),
                currentHp = obj.optInt("currentHp", 1850),
                maxHp = obj.optInt("maxHp", 3000),
                rewardGold = obj.optInt("rewardGold", 500),
                rewardXp = obj.optInt("rewardXp", 400),
                defeated = obj.optBoolean("defeated", false),
                description = obj.optString("description", "Una mole de pereza y distracciones digitales."),
                daysRemaining = obj.optInt("daysRemaining", 3),
                specialAttack = obj.optString("specialAttack", "Onda de Somnolencia"),
                elementalWeakness = obj.optString("elementalWeakness", "Enfoque Esmeralda")
            )
        } catch (_: Exception) {
            Boss()
        }
    }

    fun saveBoss(boss: Boss) {
        val obj = JSONObject().apply {
            put("id", boss.id)
            put("name", boss.name)
            put("title", boss.title)
            put("currentHp", boss.currentHp)
            put("maxHp", boss.maxHp)
            put("rewardGold", boss.rewardGold)
            put("rewardXp", boss.rewardXp)
            put("defeated", boss.defeated)
            put("description", boss.description)
            put("daysRemaining", boss.daysRemaining)
            put("specialAttack", boss.specialAttack)
            put("elementalWeakness", boss.elementalWeakness)
        }
        prefs.edit().putString(KEY_BOSS, obj.toString()).apply()
    }

    fun loadShopItems(): List<ShopItem> {
        val jsonStr = prefs.getString(KEY_SHOP, null) ?: return getDefaultShopItems()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<ShopItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val typeStr = obj.optString("type", "WEAPON")
                val rarityStr = obj.optString("rarity", "COMMON")
                list.add(
                    ShopItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        description = obj.optString("description", ""),
                        cost = obj.getInt("cost"),
                        type = runCatching { ShopItemType.valueOf(typeStr) }.getOrDefault(ShopItemType.WEAPON),
                        statBoost = obj.optString("statBoost", ""),
                        attackBonus = obj.optInt("attackBonus", 0),
                        defenseBonus = obj.optInt("defenseBonus", 0),
                        rarity = runCatching { ItemRarity.valueOf(rarityStr) }.getOrDefault(ItemRarity.COMMON),
                        origin = obj.optString("origin", "Tienda"),
                        isPurchased = obj.optBoolean("isPurchased", false),
                        isEquipped = obj.optBoolean("isEquipped", false),
                        isCustom = obj.optBoolean("isCustom", false)
                    )
                )
            }
            if (list.isEmpty()) getDefaultShopItems() else list
        } catch (_: Exception) {
            getDefaultShopItems()
        }
    }

    fun saveShopItems(items: List<ShopItem>) {
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("description", item.description)
                put("cost", item.cost)
                put("type", item.type.name)
                put("statBoost", item.statBoost)
                put("attackBonus", item.attackBonus)
                put("defenseBonus", item.defenseBonus)
                put("rarity", item.rarity.name)
                put("origin", item.origin)
                put("isPurchased", item.isPurchased)
                put("isEquipped", item.isEquipped)
                put("isCustom", item.isCustom)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_SHOP, array.toString()).apply()
    }

    fun loadFreeSpins(): Int = prefs.getInt(KEY_FREE_SPINS, 1)

    fun saveFreeSpins(count: Int) {
        prefs.edit().putInt(KEY_FREE_SPINS, count).apply()
    }

    fun loadEnemies(): List<Enemy> {
        val jsonStr = prefs.getString("hq_enemies", null) ?: return getDefaultEnemies()
        return try {
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Enemy>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Enemy(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        title = obj.getString("title"),
                        currentHp = obj.getInt("currentHp"),
                        maxHp = obj.getInt("maxHp"),
                        attackDamage = obj.getInt("attackDamage"),
                        threatLevel = obj.optString("threatLevel", "Media"),
                        rewardGold = obj.optInt("rewardGold", 40),
                        rewardXp = obj.optInt("rewardXp", 50),
                        isDefeated = obj.optBoolean("isDefeated", false),
                        description = obj.optString("description", "")
                    )
                )
            }
            if (list.isEmpty()) getDefaultEnemies() else list
        } catch (_: Exception) {
            getDefaultEnemies()
        }
    }

    fun saveEnemies(enemies: List<Enemy>) {
        val array = JSONArray()
        for (e in enemies) {
            val obj = JSONObject().apply {
                put("id", e.id)
                put("name", e.name)
                put("title", e.title)
                put("currentHp", e.currentHp)
                put("maxHp", e.maxHp)
                put("attackDamage", e.attackDamage)
                put("threatLevel", e.threatLevel)
                put("rewardGold", e.rewardGold)
                put("rewardXp", e.rewardXp)
                put("isDefeated", e.isDefeated)
                put("description", e.description)
            }
            array.put(obj)
        }
        prefs.edit().putString("hq_enemies", array.toString()).apply()
    }

    fun getDefaultEnemies(): List<Enemy> = listOf(
        Enemy(
            id = "enemy_distraction",
            name = "Sombra de la Distracción",
            title = "Enemigo de Acecho • Nivel 2",
            currentHp = 120,
            maxHp = 120,
            attackDamage = 14,
            threatLevel = "Baja",
            rewardGold = 45,
            rewardXp = 40,
            description = "Se alimenta del scroll infinito en redes y ataca cuando evitas tus hábitos."
        ),
        Enemy(
            id = "enemy_sloth",
            name = "Espectro del Desánimo",
            title = "Enemigo de Acecho • Nivel 3",
            currentHp = 220,
            maxHp = 220,
            attackDamage = 20,
            threatLevel = "Media",
            rewardGold = 80,
            rewardXp = 75,
            description = "Genera fatiga mental y debilita la voluntad de tu héroe con cada hábito postergado."
        ),
        Enemy(
            id = "enemy_chaos",
            name = "Ciber-Gárgola del Sedentarismo",
            title = "Enemigo de Acecho • Nivel 4",
            currentHp = 350,
            maxHp = 350,
            attackDamage = 28,
            threatLevel = "Peligro Crítico",
            rewardGold = 130,
            rewardXp = 120,
            description = "Monstruo acorazado que golpea con fuerza letal. Requiere varias misiones para ser destruido."
        )
    )

    private fun getDefaultQuests(): List<Quest> = listOf(
        Quest(
            id = "q_water",
            title = "Hidratación: Beber 2L de agua",
            description = "Mantén la energía en altos niveles consumiendo agua fresca.",
            type = QuestType.HABIT,
            difficulty = QuestDifficulty.EASY,
            category = "Salud",
            streak = 4,
            damageDealtToBoss = 80
        ),
        Quest(
            id = "q_workout",
            title = "Entrenamiento: 30 min de ejercicio",
            description = "Fuerza y resistencia para aumentar estadísticas del héroe.",
            type = QuestType.HABIT,
            difficulty = QuestDifficulty.MEDIUM,
            category = "Fitness",
            streak = 5,
            damageDealtToBoss = 140
        ),
        Quest(
            id = "q_study",
            title = "Enfoque: Estudiar programación 45 min",
            description = "Aprender nuevas técnicas de desarrollo y arquitectura.",
            type = QuestType.HABIT,
            difficulty = QuestDifficulty.HARD,
            category = "Mente",
            streak = 3,
            damageDealtToBoss = 220
        ),
        Quest(
            id = "q_read",
            title = "Sabiduría: Leer 15 páginas de un libro",
            description = "Lectura sin distracciones de pantallas o notificaciones.",
            type = QuestType.HABIT,
            difficulty = QuestDifficulty.EASY,
            category = "Mente",
            streak = 2,
            damageDealtToBoss = 75
        ),
        Quest(
            id = "q_desk",
            title = "Purificar Estación de Batalla (Escritorio)",
            description = "Ordenar cables, monitor y espacio de trabajo.",
            type = QuestType.ONCE,
            difficulty = QuestDifficulty.MEDIUM,
            category = "Hogar",
            isCompleted = false,
            damageDealtToBoss = 150
        ),
        Quest(
            id = "q_project",
            title = "Completar Despliegue de Proyecto Épico",
            description = "Finalizar la entrega del sprint sin procrastinar.",
            type = QuestType.ONCE,
            difficulty = QuestDifficulty.EPIC,
            category = "Trabajo",
            isCompleted = false,
            damageDealtToBoss = 350
        )
    )

    private fun getDefaultShopItems(): List<ShopItem> = listOf(
        // Equipamiento inicial activo
        ShopItem(
            id = "item_sword_emerald",
            name = "Espada de Plasma Esmeralda",
            description = "Emite una hoja de luz ionizada que atraviesa cualquier distracción.",
            cost = 250,
            type = ShopItemType.WEAPON,
            statBoost = "+15 ATK",
            attackBonus = 15,
            defenseBonus = 0,
            rarity = ItemRarity.COMMON,
            origin = "Forja Inicial",
            isPurchased = true,
            isEquipped = true
        ),
        ShopItem(
            id = "item_armor_nano",
            name = "Coraza de Nanofibra",
            description = "Reduce el daño de fallas de hábitos y amortigua el cansancio.",
            cost = 200,
            type = ShopItemType.ARMOR,
            statBoost = "+14 DEF",
            attackBonus = 0,
            defenseBonus = 14,
            rarity = ItemRarity.COMMON,
            origin = "Forja Inicial",
            isPurchased = true,
            isEquipped = true
        ),
        ShopItem(
            id = "item_ring_neon",
            name = "Anillo de Enfoque Neón",
            description = "Reliquia cibernética que estabiliza el pulso de combate y la concentración.",
            cost = 180,
            type = ShopItemType.ACCESSORY,
            statBoost = "+5 ATK, +5 DEF",
            attackBonus = 5,
            defenseBonus = 5,
            rarity = ItemRarity.COMMON,
            origin = "Forja Inicial",
            isPurchased = true,
            isEquipped = true
        ),
        // Botines de Misiones ganados previamente (listos para equipar y probar en el inventario)
        ShopItem(
            id = "loot_dagger_photon",
            name = "Daga de Cristal Fotónico",
            description = "Obtenida al cumplir hábitos matutinos. Ligera pero letal con escudo integrado.",
            cost = 380,
            type = ShopItemType.WEAPON,
            statBoost = "+22 ATK, +4 DEF",
            attackBonus = 22,
            defenseBonus = 4,
            rarity = ItemRarity.RARE,
            origin = "Botín de Misión",
            isPurchased = true,
            isEquipped = false
        ),
        ShopItem(
            id = "loot_shield_graphene",
            name = "Placas Térmicas de Grafeno",
            description = "Recompensa por racha de ejercicio. Refuerza la defensa contra espectros.",
            cost = 420,
            type = ShopItemType.ARMOR,
            statBoost = "+6 ATK, +24 DEF",
            attackBonus = 6,
            defenseBonus = 24,
            rarity = ItemRarity.RARE,
            origin = "Botín de Misión",
            isPurchased = true,
            isEquipped = false
        ),
        ShopItem(
            id = "loot_amulet_focus",
            name = "Amuleto de la Constancia",
            description = "Reliquia épica ganada en sesiones de enfoque profundo.",
            cost = 500,
            type = ShopItemType.ACCESSORY,
            statBoost = "+14 ATK, +12 DEF",
            attackBonus = 14,
            defenseBonus = 12,
            rarity = ItemRarity.EPIC,
            origin = "Botín de Misión",
            isPurchased = true,
            isEquipped = false
        ),
        // Arsenal en la Tienda y posibles recompensas épicas
        ShopItem(
            id = "item_katana_purple",
            name = "Katana Ciber-Púrpura Élite",
            description = "Forjada con núcleos de nanitas. Golpes críticos demoledores contra jefes.",
            cost = 550,
            type = ShopItemType.WEAPON,
            statBoost = "+34 ATK, +6 DEF",
            attackBonus = 34,
            defenseBonus = 6,
            rarity = ItemRarity.EPIC,
            origin = "Tienda",
            isPurchased = false,
            isEquipped = false
        ),
        ShopItem(
            id = "item_blaster_legend",
            name = "Blaster Cósmico de Neutrones",
            description = "Arma definitiva de los campeones del hábito.",
            cost = 1100,
            type = ShopItemType.WEAPON,
            statBoost = "+65 ATK, +15 DEF",
            attackBonus = 65,
            defenseBonus = 15,
            rarity = ItemRarity.LEGENDARY,
            origin = "Tienda",
            isPurchased = false,
            isEquipped = false
        ),
        ShopItem(
            id = "item_armor_quantum",
            name = "Armadura Cuántica Titánica",
            description = "Aura reflectora que previene el agotamiento mental y bloquea daño severo.",
            cost = 750,
            type = ShopItemType.ARMOR,
            statBoost = "+10 ATK, +42 DEF",
            attackBonus = 10,
            defenseBonus = 42,
            rarity = ItemRarity.EPIC,
            origin = "Tienda",
            isPurchased = false,
            isEquipped = false
        ),
        ShopItem(
            id = "item_core_aether",
            name = "Núcleo de Éter Estelar",
            description = "Reliquia ancestral que potencia drásticamente el ataque y la defensa.",
            cost = 900,
            type = ShopItemType.ACCESSORY,
            statBoost = "+28 ATK, +28 DEF",
            attackBonus = 28,
            defenseBonus = 28,
            rarity = ItemRarity.LEGENDARY,
            origin = "Tienda",
            isPurchased = false,
            isEquipped = false
        ),
        // Pociones
        ShopItem(
            id = "item_potion_health",
            name = "Poción de Ciber-Vida (+50 HP)",
            description = "Nano-inyección que revitaliza tus puntos de vida.",
            cost = 80,
            type = ShopItemType.POTION,
            statBoost = "+50 HP",
            rarity = ItemRarity.COMMON,
            origin = "Tienda",
            isPurchased = false,
            isEquipped = false
        ),
        ShopItem(
            id = "item_potion_full",
            name = "Elixir Divino Restaurador (100% HP)",
            description = "Regenera toda tu vitalidad y otorga bendición de resistencia.",
            cost = 150,
            type = ShopItemType.POTION,
            statBoost = "Restaura 100% HP",
            rarity = ItemRarity.RARE,
            origin = "Tienda",
            isPurchased = false,
            isEquipped = false
        ),
        // Recompensas personalizadas
        ShopItem(
            id = "reward_gaming",
            name = "1 Hora de Videojuegos",
            description = "Disfruta de tu juego favorito sin culpa tras cumplir tus metas.",
            cost = 200,
            type = ShopItemType.CUSTOM_REWARD,
            statBoost = "Diversión & Ocio",
            isPurchased = false,
            isCustom = true
        ),
        ShopItem(
            id = "reward_movie",
            name = "Noche de Película o Serie",
            description = "Episodios o filme con palomitas tras vencer las misiones del día.",
            cost = 250,
            type = ShopItemType.CUSTOM_REWARD,
            statBoost = "Descanso Mental",
            isPurchased = false,
            isCustom = true
        ),
        ShopItem(
            id = "reward_cheatmeal",
            name = "Comida Trampa / Postre Favorito",
            description = "Un gusto culinario merecido con tu botín de oro.",
            cost = 350,
            type = ShopItemType.CUSTOM_REWARD,
            statBoost = "Placer Culinario",
            isPurchased = false,
            isCustom = true
        ),
        ShopItem(
            id = "reward_coffee",
            name = "Café de Especialidad / Postre",
            description = "Un café latte o té helado premium en tu cafetería preferida.",
            cost = 100,
            type = ShopItemType.CUSTOM_REWARD,
            statBoost = "Impulso de Energía",
            isPurchased = false,
            isCustom = true
        )
    )

    /**
     * Tabla de botín de equipamiento que el héroe puede ganar como recompensa directa
     * al completar misiones y hábitos según su dificultad.
     */
    fun getQuestLootRewardsPool(): List<ShopItem> = listOf(
        ShopItem(
            id = "loot_blade_cyber",
            name = "Sable de Impulso Voltaico",
            description = "Recompensa forjada al completar tareas diarias con disciplina.",
            cost = 320,
            type = ShopItemType.WEAPON,
            statBoost = "+26 ATK, +5 DEF",
            attackBonus = 26,
            defenseBonus = 5,
            rarity = ItemRarity.RARE,
            origin = "Botín de Misión",
            isPurchased = true
        ),
        ShopItem(
            id = "loot_aegis_prism",
            name = "Égida Prismática de Neón",
            description = "Armadura ganada al superar misiones exigentes. Desvía ataques sombríos.",
            cost = 480,
            type = ShopItemType.ARMOR,
            statBoost = "+8 ATK, +30 DEF",
            attackBonus = 8,
            defenseBonus = 30,
            rarity = ItemRarity.EPIC,
            origin = "Botín de Misión",
            isPurchased = true
        ),
        ShopItem(
            id = "loot_visorgoggles",
            name = "Visor Táctico de Hiper-Foco",
            description = "Reliquia de precisión otorgada por completar misiones intelectuales.",
            cost = 410,
            type = ShopItemType.ACCESSORY,
            statBoost = "+18 ATK, +16 DEF",
            attackBonus = 18,
            defenseBonus = 16,
            rarity = ItemRarity.EPIC,
            origin = "Botín de Misión",
            isPurchased = true
        ),
        ShopItem(
            id = "loot_halberd_void",
            name = "Alabarda Cuántica del Vacío",
            description = "Botín legendario de misión épica. Desata ondas de energía devastadoras.",
            cost = 950,
            type = ShopItemType.WEAPON,
            statBoost = "+52 ATK, +14 DEF",
            attackBonus = 52,
            defenseBonus = 14,
            rarity = ItemRarity.LEGENDARY,
            origin = "Botín de Misión",
            isPurchased = true
        ),
        ShopItem(
            id = "loot_mantle_valkyrie",
            name = "Manto Ciber-Valquiria",
            description = "Coraza suprema ganada como recompensa de gesta heroica.",
            cost = 920,
            type = ShopItemType.ARMOR,
            statBoost = "+16 ATK, +48 DEF",
            attackBonus = 16,
            defenseBonus = 48,
            rarity = ItemRarity.LEGENDARY,
            origin = "Botín de Misión",
            isPurchased = true
        ),
        ShopItem(
            id = "loot_chrono_signet",
            name = "Sello Cronos de Inmortalidad",
            description = "Reliquia legendaria que domina el tiempo y multiplica tus defensas.",
            cost = 880,
            type = ShopItemType.ACCESSORY,
            statBoost = "+24 ATK, +26 DEF",
            attackBonus = 24,
            defenseBonus = 26,
            rarity = ItemRarity.LEGENDARY,
            origin = "Botín de Misión",
            isPurchased = true
        )
    )
}
