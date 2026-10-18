package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Quest
import com.example.model.QuestDifficulty
import com.example.model.QuestType
import com.example.ui.components.QuestCardParticleEffect
import com.example.ui.components.TaskParticleBurst
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
import com.example.ui.theme.NeonEmeraldGlow
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuestsScreen(
    quests: List<Quest>,
    onCompleteQuest: (Quest) -> Unit,
    onFailHabit: (Quest) -> Unit,
    onDeleteQuest: (String) -> Unit,
    onOpenAddTask: () -> Unit,
    onResetDaily: () -> Unit,
    modifier: Modifier = Modifier
) {
    var filterIndex by remember { mutableIntStateOf(0) }
    val filters = listOf(
        "Todas (${quests.size})",
        "Hábitos (${quests.count { it.type == QuestType.HABIT }})",
        "Misiones (${quests.count { it.type == QuestType.ONCE }})"
    )

    val filteredQuests = when (filterIndex) {
        1 -> quests.filter { it.type == QuestType.HABIT }
        2 -> quests.filter { it.type == QuestType.ONCE }
        else -> quests
    }

    val completedCount = quests.count { it.isCompleted }
    val totalCount = quests.size
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Daily Progress Banner Card
        item {
            DailyProgressWidget(
                completed = completedCount,
                total = totalCount,
                fraction = progressFraction
            )
        }

        // Action Row: Add Task & Reset Daily cycle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpenAddTask,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = NeonEmerald)
                        .testTag("add_quest_fab"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = CyberBg,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NUEVA MISIÓN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberBg
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedButton(
                    onClick = onResetDaily,
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("reset_habits_button"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberGold),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberGold.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Nuevo Día",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEachIndexed { index, filterLabel ->
                    val isSelected = filterIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF1E1438) else CyberSurface)
                            .border(
                                1.dp,
                                if (isSelected) ElectricPurple else CyberCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { filterIndex = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = filterLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ElectricPurple else TextGray
                        )
                    }
                }
            }
        }

        // Quests List
        if (filteredQuests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay misiones registradas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "¡Crea tu primera misión para subir de nivel!",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }
            }
        } else {
            items(filteredQuests, key = { it.id }) { quest ->
                InteractiveQuestCard(
                    quest = quest,
                    onComplete = { onCompleteQuest(quest) },
                    onFail = { onFailHabit(quest) },
                    onDelete = { onDeleteQuest(quest.id) }
                )
            }
        }
    }
}

@Composable
fun DailyProgressWidget(
    completed: Int,
    total: Int,
    fraction: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_progress_widget"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
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
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (fraction == 1f) CyberGold else NeonEmerald)
                    )
                    Text(
                        text = "Progreso Diario de Hábitos",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Text(
                    text = "$completed / $total (${(fraction * 100).toInt()}%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (fraction == 1f) CyberGold else NeonEmerald
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Glowing Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0F1420))
                    .border(0.8.dp, CyberCardBorder, RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth(fraction)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (fraction == 1f) {
                                    listOf(CyberGold, Color(0xFFFFE082))
                                } else {
                                    listOf(NeonEmerald, NeonEmeraldGlow)
                                }
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val motivation = when {
                total == 0 -> "Comienza tu día creando misiones legendarias."
                fraction == 1f -> "🏆 ¡Día Legendario! Todas las misiones completadas. ¡El Gólem tiembla!"
                fraction >= 0.5f -> "⚔️ ¡Gran ímpetu de batalla! Continúa tu racha de hábitos."
                else -> "🛡️ El Gólem acecha. ¡Completa tus hábitos para defender tu honor!"
            }

            Text(
                text = motivation,
                fontSize = 10.sp,
                color = TextGray
            )
        }
    }
}

@Composable
fun InteractiveQuestCard(
    quest: Quest,
    onComplete: () -> Unit,
    onFail: () -> Unit,
    onDelete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var particleTrigger by remember { mutableLongStateOf(0L) }
    var isCheckBouncing by remember { mutableStateOf(false) }

    val checkScale by animateFloatAsState(
        targetValue = if (isCheckBouncing) 1.28f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "check_bounce"
    )

    val (diffColor, diffGlyph) = when (quest.difficulty) {
        QuestDifficulty.EASY -> Pair(EasyGreen, "◆◇◇◇")
        QuestDifficulty.MEDIUM -> Pair(MediumBlue, "◆◆◇◇")
        QuestDifficulty.HARD -> Pair(HardPurple, "◆◆◆◇")
        QuestDifficulty.EPIC -> Pair(EpicGold, "◆◆◆◆")
    }

    val categoryIcon = when (quest.category.lowercase()) {
        "fitness", "deporte" -> Icons.Default.FitnessCenter
        "mente", "estudio", "lectura" -> Icons.Default.MenuBook
        "trabajo", "proyectos" -> Icons.Default.Work
        "hogar", "limpieza" -> Icons.Default.Home
        "finanzas" -> Icons.Default.AccountBalance
        else -> Icons.Default.Favorite
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quest_card_${quest.id}")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (quest.isCompleted) Color(0xFF0C1415) else CyberSurface
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = if (quest.isCompleted) 1.dp else 1.2.dp,
                color = if (quest.isCompleted) NeonEmerald.copy(alpha = 0.5f) else CyberCardBorder
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header: Type + Difficulty Badge + Category + Delete Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Type Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E1736))
                                .border(0.8.dp, ElectricPurple.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (quest.type == QuestType.HABIT) "HÁBITO" else "MISIÓN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (quest.type == QuestType.HABIT) ElectricPurple else CyberCyan
                            )
                        }

                        // Difficulty Tag with Crystals
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(diffColor.copy(alpha = 0.15f))
                                .border(0.8.dp, diffColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = quest.difficulty.label.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = diffColor
                                )
                                Text(
                                    text = diffGlyph,
                                    fontSize = 8.sp,
                                    color = diffColor
                                )
                            }
                        }

                        // Category with Icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberSurfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = quest.category,
                                fontSize = 9.sp,
                                color = TextGray
                            )
                        }
                    }

                    // Delete Action
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar misión",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Middle: Interactive Checkbox & Title/Description
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !quest.isCompleted) {
                            isCheckBouncing = true
                            particleTrigger = System.currentTimeMillis()
                            onComplete()
                            coroutineScope.launch {
                                delay(250)
                                isCheckBouncing = false
                            }
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Checkbox with spring bounce & particle trigger
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .scale(checkScale)
                            .shadow(
                                elevation = if (quest.isCompleted) 10.dp else 2.dp,
                                shape = CircleShape,
                                spotColor = if (quest.isCompleted) NeonEmerald else diffColor
                            )
                            .clip(CircleShape)
                            .background(
                                if (quest.isCompleted) {
                                    Brush.linearGradient(listOf(NeonEmerald, Color(0xFF00B060)))
                                } else {
                                    Brush.linearGradient(listOf(CyberSurfaceVariant, Color(0xFF0F1524)))
                                }
                            )
                            .border(
                                width = 1.8.dp,
                                color = if (quest.isCompleted) NeonEmeraldGlow else diffColor,
                                shape = CircleShape
                            )
                            .clickable(enabled = !quest.isCompleted) {
                                isCheckBouncing = true
                                particleTrigger = System.currentTimeMillis()
                                onComplete()
                                coroutineScope.launch {
                                    delay(250)
                                    isCheckBouncing = false
                                }
                            }
                            .testTag("complete_quest_${quest.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (quest.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completada",
                                tint = CyberBg,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = quest.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quest.isCompleted) TextGray else TextWhite,
                            textDecoration = if (quest.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        if (quest.description.isNotBlank()) {
                            Text(
                                text = quest.description,
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer: Rewards (+XP, +Oro), Boss Damage, Streak & Habit Fail Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewards
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // XP Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF062A17))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "+${quest.difficulty.xp} XP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonEmerald
                            )
                        }

                        // Gold Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2E2407))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = CyberGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "+${quest.difficulty.gold}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberGold
                            )
                        }

                        // Boss Damage Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF22113A))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = ElectricPurple,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "-${quest.damageDealtToBoss} HP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricPurple
                            )
                        }

                        // Equippable Loot Reward Tag
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0A2630))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🎁 +Botín",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                        }
                    }

                    // Habit streak or failure button
                    if (quest.type == QuestType.HABIT) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (quest.streak > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF33161D))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = null,
                                        tint = CyberGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${quest.streak}d",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberGold
                                    )
                                }
                            }

                            if (!quest.isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF2B0C15))
                                        .border(0.8.dp, CyberCrimson.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .clickable { onFail() }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                        .testTag("fail_habit_${quest.id}")
                                ) {
                                    Text(
                                        text = "Fallar (-${quest.difficulty.hpPenalty} HP)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberCrimson
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animación de partículas sobre la tarjeta que se dispara al cambiar el estado a completada
        QuestCardParticleEffect(
            isCompleted = quest.isCompleted,
            modifier = Modifier.matchParentSize()
        )
    }
}
