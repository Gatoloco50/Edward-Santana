package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Hero
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.viewmodel.ChestRewardType
import kotlinx.coroutines.delay

@Composable
fun RewardedAdDialog(
    hero: Hero,
    onDismiss: () -> Unit,
    onClaimReward: (ChestRewardType) -> Unit
) {
    var secondsRemaining by remember { mutableIntStateOf(5) }
    var isAdCompleted by remember { mutableStateOf(false) }
    var selectedReward by remember {
        mutableStateOf(if (hero.hp < 40) ChestRewardType.FULL_HEAL else ChestRewardType.DOUBLE_GOLD)
    }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
        isAdCompleted = true
    }

    Dialog(
        onDismissRequest = {
            if (isAdCompleted) onDismiss()
        },
        properties = DialogProperties(
            dismissOnBackPress = isAdCompleted,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("rewarded_ad_dialog"),
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
                // Header with simulated Ad tag
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
                    }

                    if (isAdCompleted) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = TextGray
                            )
                        }
                    } else {
                        // Countdown timer
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberSurfaceVariant)
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Recompensa en: ${secondsRemaining}s",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated Ad Content Video Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E113B), Color(0xFF0F172A))
                            )
                        )
                        .border(1.dp, ElectricPurple.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚡ Ciber-Elixir Energético ⚡",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "¡Alcanza tu máximo rendimiento y rompe tus límites diarios!",
                            fontSize = 11.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Bottom ad progress
                    if (!isAdCompleted) {
                        LinearProgressIndicator(
                            progress = { (5 - secondsRemaining) / 5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .align(Alignment.BottomCenter),
                            color = CyberGold,
                            trackColor = CyberSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isAdCompleted) {
                    Text(
                        text = "Viendo anuncio patrocinado para abrir el Cofre Misterioso...",
                        fontSize = 12.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick skip button for easy developer/reviewer test
                    OutlinedButton(
                        onClick = {
                            secondsRemaining = 0
                            isAdCompleted = true
                        },
                        modifier = Modifier.testTag("skip_ad_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberGold),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Acelerar Demostración", fontSize = 11.sp)
                    }
                } else {
                    // Reward Selection
                    Text(
                        text = "🎉 ¡Anuncio Completado! Elige tu Recompensa:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberGold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Option 1: Double Gold
                    val doubleSelected = selectedReward == ChestRewardType.DOUBLE_GOLD
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (doubleSelected) Color(0xFF33270A) else CyberSurfaceVariant)
                            .border(
                                1.5.dp,
                                if (doubleSelected) CyberGold else CyberCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedReward = ChestRewardType.DOUBLE_GOLD }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Duplicar Oro del Día",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Recibe +${hero.gold.coerceAtLeast(150)} Monedas de Oro",
                                fontSize = 11.sp,
                                color = CyberGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 2: Full Heal
                    val healSelected = selectedReward == ChestRewardType.FULL_HEAL
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (healSelected) Color(0xFF35111B) else CyberSurfaceVariant)
                            .border(
                                1.5.dp,
                                if (healSelected) CyberCrimson else CyberCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedReward = ChestRewardType.FULL_HEAL }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = CyberCrimson,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Recuperar Toda la Vida (100% HP)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Restaura tu vitalidad a ${hero.maxHp}/${hero.maxHp} HP",
                                fontSize = 11.sp,
                                color = CyberCrimson
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onClaimReward(selectedReward) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("claim_chest_reward_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "¡RECLAMAR RECOMPENSA!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberBg
                        )
                    }
                }
            }
        }
    }
}
