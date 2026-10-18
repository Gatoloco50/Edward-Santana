package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Enemy
import com.example.model.Hero
import com.example.model.ItemRarity
import com.example.model.ShopItem
import com.example.model.ShopItemType
import com.example.ui.components.CyberStatBar
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.EasyGreen
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EpicGold
import com.example.ui.theme.HardPurple
import com.example.ui.theme.MediumBlue
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay

@Composable
fun HeroScreen(
    hero: Hero,
    enemies: List<Enemy>,
    shopItems: List<ShopItem>,
    onEquipItem: (ShopItem) -> Unit,
    onReviveWithAd: () -> Unit,
    onActivateShieldWithAd: () -> Unit,
    onAttackEnemy: () -> Unit,
    onRespawnEnemies: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSimulatingReviveAd by remember { mutableStateOf(false) }
    var isSimulatingShieldAd by remember { mutableStateOf(false) }
    var selectedInventoryFilter by remember { mutableIntStateOf(0) }

    val activeEnemy = enemies.firstOrNull { !it.isDefeated }
    val inventoryGear = shopItems.filter { it.isPurchased && it.isEquippableGear }

    val filterTabs = listOf(
        "Todo (${inventoryGear.size})",
        "Botín (${inventoryGear.count { it.origin == "Botín de Misión" }})",
        "Armas (${inventoryGear.count { it.type == ShopItemType.WEAPON }})",
        "Armaduras (${inventoryGear.count { it.type == ShopItemType.ARMOR }})",
        "Reliquias (${inventoryGear.count { it.type == ShopItemType.ACCESSORY }})"
    )

    val filteredInventory = when (selectedInventoryFilter) {
        1 -> inventoryGear.filter { it.origin == "Botín de Misión" }
        2 -> inventoryGear.filter { it.type == ShopItemType.WEAPON }
        3 -> inventoryGear.filter { it.type == ShopItemType.ARMOR }
        4 -> inventoryGear.filter { it.type == ShopItemType.ACCESSORY }
        else -> inventoryGear
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hero_pulse")
    val criticalPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "critical_pulse"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ALERTA 1: SI EL HÉROE ESTÁ MUERTO (0 HP)
        if (hero.isDead) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(criticalPulse)
                        .testTag("hero_dead_alert_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF330912)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, CyberCrimson)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dangerous,
                            contentDescription = "Héroe Caído",
                            tint = CyberCrimson,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "💀 ¡TU HÉROE HA MUERTO! (0 HP)",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Las tareas postergadas y los enemigos acechantes han drenado toda tu vitalidad. ¡Tu deber sagrado es mantenerlo con vida!",
                            fontSize = 11.sp,
                            color = Color(0xFFFFB4C0),
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { isSimulatingReviveAd = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("revive_with_ad_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = CyberBg,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REVIVIR CON ANUNCIO (100% HP)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberBg
                            )
                        }
                    }
                }
            }
        } else if (hero.hp < 30) {
            // ALERTA 2: VITALIDAD CRÍTICA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF38131B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCrimson)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = CyberCrimson,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "¡Vitalidad Crítica! (${hero.hp} HP restantes)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Si fallas más tareas el héroe morirá. Equipa mejor armadura o activa el Escudo de Anuncio.",
                                fontSize = 10.sp,
                                color = Color(0xFFFFB4C0)
                            )
                        }
                    }
                }
            }
        }

        // Hero Profile & Vitality/Stats Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_profile_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (hero.isDead) CyberCrimson else ElectricPurple
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Icon
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(20.dp, CircleShape, spotColor = if (hero.isDead) CyberCrimson else NeonEmerald)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    if (hero.isDead) {
                                        listOf(CyberCrimson.copy(alpha = 0.5f), Color(0xFF22050D))
                                    } else {
                                        listOf(ElectricPurple, Color(0xFF1E1035), CyberSurface)
                                    }
                                )
                            )
                            .border(
                                2.5.dp,
                                if (hero.isDead) CyberCrimson else NeonEmerald,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hero.isDead) Icons.Default.Dangerous else Icons.Default.Shield,
                            contentDescription = "Avatar del Héroe",
                            tint = if (hero.isDead) CyberCrimson else CyberGold,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = hero.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )

                    Text(
                        text = "${hero.title} • Nivel ${hero.level}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (hero.isDead) CyberCrimson else NeonEmerald
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dual Bars: HP & XP
                    CyberStatBar(
                        label = if (hero.isDead) "HÉROE CAÍDO" else "Vitalidad del Héroe (HP)",
                        current = hero.hp,
                        max = hero.maxHp,
                        colorStart = CyberCrimson,
                        colorEnd = Color(0xFFFF6B8B),
                        icon = Icons.Default.Favorite,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    CyberStatBar(
                        label = "Experiencia (XP)",
                        current = hero.xp,
                        max = hero.maxXp,
                        colorStart = NeonEmerald,
                        colorEnd = Color(0xFF69F0AE),
                        icon = Icons.Default.FlashOn,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Gold & Divine Shield badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF2F240A))
                                .border(1.dp, CyberGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = CyberGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${hero.gold} Oro",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberGold
                            )
                        }

                        if (hero.hasDivineShield) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF0F2C2A))
                                    .border(1.dp, CyberCyan, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Escudo Divino Activo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // PANEL DE ESTADÍSTICAS DE ATAQUE Y DEFENSA (CON BONIFICADORES DE EQUIPO Y MISIONES)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("combat_stats_row"),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tarjeta de Ataque (ATK)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("attack_scaling_widget"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberGold.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = CyberGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "ATAQUE TOTAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "⚔️ ${hero.totalAttackPower} ATK",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberGold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "• Base: ${hero.strength}\n• Misiones: +${hero.attackBonusFromMissions}\n• Equipo: +${hero.equipmentAttackBonus}",
                            fontSize = 10.sp,
                            color = TextWhite,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Tarjeta de Defensa (DEF)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("defense_scaling_widget"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCyan.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "DEFENSA TOTAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "🛡️ ${hero.totalDefensePower} DEF",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberCyan
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "• Base: ${hero.defense}\n• Equipo: +${hero.equipmentDefenseBonus}\n• Mitigación: -${hero.damageReductionFromDefense} HP",
                            fontSize = 10.sp,
                            color = TextWhite,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // RANURAS DE EQUIPAMIENTO ACTIVO (ARMA, ARMADURA, RELIQUIA)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_loadout_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Ranuras de Equipamiento Activo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }

                        Text(
                            text = "+${hero.equipmentAttackBonus} ATK | +${hero.equipmentDefenseBonus} DEF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonEmerald
                        )
                    }

                    EquipSlotRow(
                        slot = "Arma Principal",
                        name = hero.equippedWeapon,
                        color = NeonEmerald
                    )
                    EquipSlotRow(
                        slot = "Armadura Corporal",
                        name = hero.equippedArmor,
                        color = ElectricPurple
                    )
                    EquipSlotRow(
                        slot = "Reliquia / Accesorio",
                        name = hero.equippedAccessory,
                        color = CyberCyan
                    )
                }
            }
        }

        // INVENTARIO DE HÉROE (EQUIPAR ÍTEMS GANADOS EN TAREAS Y FORJA)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Inventario de Botín y Equipo (${inventoryGear.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )
                        Text(
                            text = "Completa tareas para ganar nuevos ítems y equípalos para subir tu ATK o DEF",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }

                // Pestañas de filtro del Inventario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filterTabs.forEachIndexed { index, label ->
                        val isSelected = selectedInventoryFilter == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF1E1438) else CyberSurface)
                                .border(
                                    1.dp,
                                    if (isSelected) NeonEmerald else CyberCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedInventoryFilter = index }
                                .padding(vertical = 7.dp, horizontal = 2.dp)
                                .testTag("inventory_filter_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (isSelected) NeonEmerald else TextGray,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        if (filteredInventory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No tienes ítems en esta categoría aún",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "¡Completa misiones en la pestaña 'Misiones' para recibir botín de equipamiento!",
                            fontSize = 11.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredInventory, key = { it.id }) { gearItem ->
                InventoryGearCard(
                    item = gearItem,
                    onEquipToggle = { onEquipItem(gearItem) }
                )
            }
        }

        // ENEMIGOS EN ACECHO (COMBATE & SUPERVIVENCIA)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enemigos en Acecho (Supervivencia)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                if (activeEnemy == null) {
                    OutlinedButton(
                        onClick = onRespawnEnemies,
                        modifier = Modifier.height(32.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonEmerald),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nueva Oleada", fontSize = 10.sp)
                    }
                }
            }
        }

        if (activeEnemy != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_enemy_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1D1426)),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, ElectricPurple)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = activeEnemy.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "${activeEnemy.title} • Amenaza: ${activeEnemy.threatLevel}",
                                    fontSize = 11.sp,
                                    color = ElectricPurple
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF38121E))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Ataque: -${activeEnemy.attackDamage} HP",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCrimson
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        CyberStatBar(
                            label = "Salud del Enemigo",
                            current = activeEnemy.currentHp,
                            max = activeEnemy.maxHp,
                            colorStart = ElectricPurple,
                            colorEnd = Color(0xFFC084FC),
                            icon = Icons.Default.Dangerous,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = activeEnemy.description,
                            fontSize = 11.sp,
                            color = TextGray,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onAttackEnemy,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("attack_enemy_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ATACAR CON TU DAÑO (+${hero.totalAttackPower} ATK)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261B)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🛡️ ¡ÁREA DESPEJADA DE ENEMIGOS!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Has derrotado a todas las sombras de acecho. ¡Tu héroe está a salvo por ahora!",
                            fontSize = 11.sp,
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // ESPACIO DE MONETIZACIÓN & ANUNCIOS DE APOYO AL HÉROE
        item {
            Text(
                text = "Santuario de Supervivencia (Anuncios Recompensados)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_ad_support_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberGold)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ANUNCIO RECOMPENSADO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberBg
                            )
                        }

                        Text(
                            text = "Ayuda en Combate",
                            fontSize = 10.sp,
                            color = TextGray
                        )
                    }

                    Text(
                        text = "Recupera a tu héroe cuando esté en peligro de muerte o protégete de fallos:",
                        fontSize = 11.sp,
                        color = TextWhite
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isSimulatingReviveAd = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("hero_ad_heal_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCrimson),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Healing,
                                contentDescription = null,
                                tint = TextWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% HP (Ver Ad)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { isSimulatingShieldAd = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("hero_ad_shield_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CyberBg,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Escudo (Ver Ad)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberBg
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal de Anuncio para Revivir / Curación 100%
    if (isSimulatingReviveAd) {
        HeroSimulatedAdModal(
            title = "✨ Poción de Fénix Restauradora",
            rewardDesc = "Restaura la vitalidad del héroe al 100% (${hero.maxHp} HP) y activa el Escudo Divino",
            onComplete = {
                isSimulatingReviveAd = false
                onReviveWithAd()
            },
            onDismiss = { isSimulatingReviveAd = false }
        )
    }

    // Modal de Anuncio para Escudo Divino
    if (isSimulatingShieldAd) {
        HeroSimulatedAdModal(
            title = "🛡️ Escudo Divino de Supervivencia",
            rewardDesc = "Inmunidad total: Si fallas una misión, el escudo absorberá el 100% del daño",
            onComplete = {
                isSimulatingShieldAd = false
                onActivateShieldWithAd()
            },
            onDismiss = { isSimulatingShieldAd = false }
        )
    }
}

@Composable
fun InventoryGearCard(
    item: ShopItem,
    onEquipToggle: () -> Unit
) {
    val typeIcon = when (item.type) {
        ShopItemType.WEAPON -> Icons.Default.Shield
        ShopItemType.ARMOR -> Icons.Default.Security
        else -> Icons.Default.Star
    }

    val typeColor = when (item.type) {
        ShopItemType.WEAPON -> NeonEmerald
        ShopItemType.ARMOR -> ElectricPurple
        else -> CyberCyan
    }

    val rarityColor = when (item.rarity) {
        ItemRarity.COMMON -> EasyGreen
        ItemRarity.RARE -> MediumBlue
        ItemRarity.EPIC -> HardPurple
        ItemRarity.LEGENDARY -> EpicGold
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isEquipped) Color(0xFF0E211C) else CyberSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (item.isEquipped) 1.5.dp else 1.dp,
            color = if (item.isEquipped) NeonEmerald else CyberCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono de ranura
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(typeColor.copy(alpha = 0.15f))
                    .border(1.dp, typeColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = typeIcon,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Etiqueta de Rareza
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(rarityColor.copy(alpha = 0.18f))
                            .border(0.7.dp, rarityColor.copy(alpha = 0.6f), RoundedCornerShape(5.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = item.rarity.label.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = rarityColor
                        )
                    }

                    // Etiqueta de Origen (ej. Botín de Misión)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                if (item.origin == "Botín de Misión") Color(0xFF2B2108) else CyberSurfaceVariant
                            )
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (item.origin == "Botín de Misión") "🎁 BOTÍN DE MISIÓN" else item.type.label.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.origin == "Botín de Misión") CyberGold else TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                // Bonificadores de ATK y DEF
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.attackBonus > 0) {
                        Text(
                            text = "⚔️ +${item.attackBonus} ATK",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberGold
                        )
                    }
                    if (item.defenseBonus > 0) {
                        Text(
                            text = "🛡️ +${item.defenseBonus} DEF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberCyan
                        )
                    }
                }

                Text(
                    text = item.description,
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 13.sp
                )
            }

            // Botón de Equipar / Equipado
            if (item.isEquipped) {
                OutlinedButton(
                    onClick = onEquipToggle,
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("equip_button_${item.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonEmerald),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonEmerald),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Equipado",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            } else {
                Button(
                    onClick = onEquipToggle,
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("equip_button_${item.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = "Equipar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                }
            }
        }
    }
}

@Composable
fun HeroSimulatedAdModal(
    title: String,
    rewardDesc: String,
    onComplete: () -> Unit,
    onDismiss: () -> Unit
) {
    var timer by remember { mutableIntStateOf(4) }
    var adFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timer > 0) {
            delay(1000L)
            timer--
        }
        adFinished = true
    }

    Dialog(onDismissRequest = { if (adFinished) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("hero_ad_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberGold)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "ANUNCIO RECOMPENSADO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberBg
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2B1238), Color(0xFF0F172A))
                            )
                        )
                        .border(1.dp, ElectricPurple, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "⚡ Nano-Tecnología Médica ⚡",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = if (!adFinished) "Reproduciendo anuncio ($timer seg)..." else "¡Anuncio finalizado!",
                            fontSize = 11.sp,
                            color = CyberGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = rewardDesc,
                    fontSize = 11.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!adFinished) {
                    LinearProgressIndicator(
                        progress = { (4 - timer) / 4f },
                        modifier = Modifier.fillMaxWidth(0.85f),
                        color = CyberGold,
                        trackColor = CyberCardBorder
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            timer = 0
                            adFinished = true
                        },
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Acelerar Anuncio", fontSize = 11.sp, color = CyberGold)
                    }
                } else {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("claim_hero_ad_reward_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "¡RECLAMAR RECOMPENSA!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberBg
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipSlotRow(
    slot: String,
    name: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = slot,
            fontSize = 12.sp,
            color = TextGray
        )
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
