package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.Boss
import com.example.model.Hero
import com.example.ui.components.CyberStatBar
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun BossScreen(
    boss: Boss,
    hero: Hero,
    onSpecialAttack: () -> Unit,
    onRespawnBoss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "boss_aura")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "boss_scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Boss Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("boss_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (boss.defeated) NeonEmerald else CyberCrimson
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Season / Time remaining badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2E1220))
                            .border(1.dp, CyberCrimson.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Temporada Semanal: ${boss.daysRemaining} días restantes",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Boss Creature Visual
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(if (boss.defeated) 1f else pulseScale)
                            .shadow(24.dp, CircleShape, spotColor = if (boss.defeated) NeonEmerald else CyberCrimson)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    if (boss.defeated) {
                                        listOf(NeonEmerald.copy(alpha = 0.4f), CyberSurface)
                                    } else {
                                        listOf(CyberCrimson, Color(0xFF3B0715), CyberSurface)
                                    }
                                )
                            )
                            .border(
                                2.5.dp,
                                if (boss.defeated) NeonEmerald else CyberCrimson,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (boss.defeated) Icons.Default.CardGiftcard else Icons.Default.Dangerous,
                            contentDescription = "Jefe Semanal",
                            tint = if (boss.defeated) CyberGold else TextWhite,
                            modifier = Modifier.size(58.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = boss.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = boss.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (boss.defeated) NeonEmerald else CyberCrimson
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = boss.description,
                        fontSize = 12.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Boss HP Bar
                    CyberStatBar(
                        label = if (boss.defeated) "¡JEFE DERROTADO!" else "Salud del Jefe Semanal",
                        current = boss.currentHp,
                        max = boss.maxHp,
                        colorStart = if (boss.defeated) NeonEmerald else CyberCrimson,
                        colorEnd = if (boss.defeated) Color(0xFF69F0AE) else Color(0xFFFF5252),
                        icon = Icons.Default.Shield,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Battle Mechanics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Mecánicas del Combate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Debilidad: ${boss.elementalWeakness} (Cada tarea cumplida resta de 75 a 350 HP)",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = CyberCrimson,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Ataque del Jefe: ${boss.specialAttack}",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }
            }
        }

        // Action Buttons
        item {
            if (!boss.defeated) {
                Button(
                    onClick = onSpecialAttack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("special_attack_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ASESTAR GOLPE DE PLASMA (ESPECIAL)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite
                    )
                }
            } else {
                Button(
                    onClick = onRespawnBoss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("respawn_boss_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = CyberBg,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "INVOCAR SIGUIENTE JEFE SEMANAL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBg
                    )
                }
            }
        }

        // Weekly Reward Loot
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF33270A))
                            .border(1.dp, CyberGold, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Botín Legendario al Vencer:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberGold
                        )
                        Text(
                            text = "+${boss.rewardGold} Monedas de Oro • +${boss.rewardXp} Puntos XP",
                            fontSize = 11.sp,
                            color = TextWhite
                        )
                    }
                }
            }
        }
    }
}
