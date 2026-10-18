package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Hero
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldGlow
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CyberTopBar(
    hero: Hero,
    onWheelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Detect XP or Level increases to trigger XP visual feedback animation
    var previousXp by remember { mutableIntStateOf(hero.xp) }
    var previousLevel by remember { mutableIntStateOf(hero.level) }
    var xpGainDelta by remember { mutableIntStateOf(0) }
    var showXpBadge by remember { mutableStateOf(false) }
    val xpBarFlash = remember { Animatable(0f) }
    val levelBadgeBounce = remember { Animatable(1f) }

    LaunchedEffect(hero.xp, hero.level) {
        val gainedXp = when {
            hero.level > previousLevel -> (hero.xp + (hero.maxXp * 0.7f).toInt() - previousXp).coerceAtLeast(25)
            hero.xp > previousXp -> hero.xp - previousXp
            else -> 0
        }
        previousXp = hero.xp
        previousLevel = hero.level

        if (gainedXp > 0) {
            xpGainDelta = gainedXp
            showXpBadge = true
            launch {
                xpBarFlash.snapTo(1f)
                xpBarFlash.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
                )
            }
            launch {
                levelBadgeBounce.snapTo(1.25f)
                levelBadgeBounce.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            delay(1350L)
            showXpBadge = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(CyberSurface, CyberBg)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        if (xpBarFlash.value > 0.1f) NeonEmerald else CyberCardBorder,
                        Color.Transparent
                    )
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Level badge, Hero Name, Streak, Gold, Wheel button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hero info & Level
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Level Badge with bounce when XP is gained
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .scale(levelBadgeBounce.value)
                            .shadow(
                                elevation = if (xpBarFlash.value > 0f) 14.dp else 8.dp,
                                shape = CircleShape,
                                spotColor = NeonEmerald
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(ElectricPurple, CyberSurface)
                                )
                            )
                            .border(
                                width = if (xpBarFlash.value > 0f) 2.2.dp else 1.5.dp,
                                color = if (xpBarFlash.value > 0f) CyberGold else NeonEmerald,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "NIV",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                            Text(
                                text = "${hero.level}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite
                            )
                        }
                    }

                    Column {
                        Text(
                            text = hero.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = hero.title,
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }

                // Currency & Streak & Lucky Wheel
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Streak Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF381520))
                            .border(1.dp, CyberCrimson.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Racha",
                            tint = CyberGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "${hero.streakDays}d",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    // Gold Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF332A10))
                            .border(1.dp, CyberGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Oro",
                            tint = CyberGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${hero.gold}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberGold
                        )
                    }

                    // Lucky Wheel Button
                    IconButton(
                        onClick = onWheelClick,
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(6.dp, CircleShape, spotColor = ElectricPurple)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ElectricPurple, Color(0xFF6B21A8))
                                )
                            )
                            .border(1.5.dp, CyberGold, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Ruleta de la Suerte",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Bars: HP & XP in a compact dual row with XP gain animation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // HP Bar
                CyberStatBar(
                    label = "HP",
                    current = hero.hp,
                    max = hero.maxHp,
                    colorStart = CyberCrimson,
                    colorEnd = Color(0xFFFF6B8B),
                    icon = Icons.Default.Favorite,
                    modifier = Modifier.weight(1f)
                )

                // XP Bar with animated glow & floating +XP popup
                Box(modifier = Modifier.weight(1f)) {
                    CyberStatBar(
                        label = "XP",
                        current = hero.xp,
                        max = hero.maxXp,
                        colorStart = NeonEmerald,
                        colorEnd = Color(0xFF69F0AE),
                        icon = Icons.Default.Star,
                        highlightIntensity = xpBarFlash.value,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("top_xp_bar")
                    )

                    // Floating "+XP" pop-up badge right above the XP bar
                    this@Row.AnimatedVisibility(
                        visible = showXpBadge,
                        enter = fadeIn(tween(180)) + slideInVertically(initialOffsetY = { it / 2 }),
                        exit = fadeOut(tween(300)) + slideOutVertically(targetOffsetY = { -it }),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = (-4).dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .shadow(8.dp, RoundedCornerShape(8.dp), spotColor = NeonEmerald)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf( Color(0xFF053821), Color(0xFF0A4F30))
                                    )
                                )
                                .border(1.dp, NeonEmeraldGlow, RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                                .testTag("xp_gain_floating_badge")
                        ) {
                            Text(
                                text = "+$xpGainDelta XP ⚡",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonEmeraldGlow
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CyberStatBar(
    label: String,
    current: Int,
    max: Int,
    colorStart: Color,
    colorEnd: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    highlightIntensity: Float = 0f
) {
    val progressFraction = (current.toFloat() / max.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressFraction,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val iconScale = 1f + (highlightIntensity * 0.35f)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (highlightIntensity > 0.2f) CyberGold else colorStart,
                    modifier = Modifier
                        .size(12.dp)
                        .scale(iconScale)
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (highlightIntensity > 0.2f) NeonEmeraldGlow else colorStart
                )
            }
            Text(
                text = "$current / $max",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (highlightIntensity > 0.2f) NeonEmeraldGlow else TextWhite
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Progress track with dynamic shimmer wave on XP gain
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (highlightIntensity > 0.1f) 10.dp else 8.dp)
                .shadow(
                    elevation = (highlightIntensity * 12f).dp,
                    shape = RoundedCornerShape(5.dp),
                    spotColor = colorStart
                )
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF0F1420))
                .border(
                    width = if (highlightIntensity > 0.1f) 1.4.dp else 0.8.dp,
                    color = if (highlightIntensity > 0.1f) NeonEmeraldGlow else CyberCardBorder,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        Brush.horizontalGradient(
                            if (highlightIntensity > 0.1f) {
                                listOf(colorStart, CyberCyan, Color.White, colorEnd)
                            } else {
                                listOf(colorStart, colorEnd)
                            }
                        )
                    )
            )

            // Animated energy sweep across the bar when XP is gained
            if (highlightIntensity > 0.02f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val sweepProgress = 1f - highlightIntensity
                    val sweepX = size.width * animatedProgress * sweepProgress
                    drawCircle(
                        color = Color.White.copy(alpha = highlightIntensity * 0.85f),
                        radius = size.height * 1.3f,
                        center = Offset(sweepX, size.height / 2f)
                    )
                }
            }
        }
    }
}
