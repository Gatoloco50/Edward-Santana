package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldGlow
import com.example.ui.theme.TextWhite
import com.example.viewmodel.FloatingReward
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

enum class ParticleShape {
    CIRCLE,
    DIAMOND,
    STAR
}

data class CardParticle(
    val angle: Float,
    val speed: Float,
    val color: Color,
    val size: Float,
    val maxDistance: Float,
    val shape: ParticleShape,
    val rotationSpeed: Float
)

data class RisingXpOrb(
    val startXFraction: Float,
    val startYFraction: Float,
    val targetXFraction: Float,
    val speedFactor: Float,
    val radiusDp: Float,
    val color: Color,
    val wavePhase: Float
)

/**
 * Visual feedback animation triggered whenever the hero gains Experience Points (XP)
 * after completing a task. Renders ascending neon XP orbs and a floating "+XP" energy badge
 * that travels upward toward the hero's XP bar.
 */
@Composable
fun XpGainFeedbackOverlay(
    reward: FloatingReward?,
    modifier: Modifier = Modifier
) {
    val xpGain = reward?.xpChange ?: 0
    val triggerId = if (xpGain > 0) reward?.id ?: 0L else 0L

    val orbProgress = remember { Animatable(1f) }
    val badgeScale = remember { Animatable(0.5f) }
    var activeXpAmount by remember { mutableIntStateOf(0) }

    val orbs = remember(triggerId) {
        if (triggerId == 0L) emptyList()
        else {
            val colors = listOf(NeonEmerald, NeonEmeraldGlow, CyberGold, CyberCyan, ElectricPurple)
            List(22) { idx ->
                RisingXpOrb(
                    startXFraction = 0.2f + Random.nextFloat() * 0.6f,
                    startYFraction = 0.52f + Random.nextFloat() * 0.25f,
                    targetXFraction = 0.68f + (Random.nextFloat() - 0.5f) * 0.22f, // converges toward top-right XP bar
                    speedFactor = 0.75f + Random.nextFloat() * 0.35f,
                    radiusDp = 3f + Random.nextFloat() * 4.5f,
                    color = colors[idx % colors.size],
                    wavePhase = Random.nextFloat() * 6.28f
                )
            }
        }
    }

    LaunchedEffect(triggerId) {
        if (triggerId != 0L && xpGain > 0) {
            activeXpAmount = xpGain
            launch {
                badgeScale.snapTo(0.6f)
                badgeScale.animateTo(
                    targetValue = 1.08f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            orbProgress.snapTo(0f)
            orbProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1250, easing = FastOutSlowInEasing)
            )
        }
    }

    if (orbProgress.value < 1f && activeXpAmount > 0) {
        val p = orbProgress.value
        val fadeAlpha = (1f - (p * 0.95f)).coerceIn(0f, 1f)

        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("xp_gain_feedback_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // 1. Ascending Neon XP Energy Orbs with glowing trails
            Canvas(modifier = Modifier.fillMaxSize()) {
                orbs.forEach { orb ->
                    val localP = (p * orb.speedFactor).coerceIn(0f, 1f)
                    val sway = sin((localP * 8f) + orb.wavePhase) * 18.dp.toPx() * (1f - localP)
                    val currentX = (size.width * (orb.startXFraction + (orb.targetXFraction - orb.startXFraction) * localP)) + sway
                    val currentY = size.height * (orb.startYFraction * (1f - localP) + 0.04f * localP)
                    val orbAlpha = (1f - localP).coerceIn(0f, 1f)
                    val r = orb.radiusDp.dp.toPx() * (1f - localP * 0.4f)

                    // Outer aura
                    drawCircle(
                        color = orb.color.copy(alpha = orbAlpha * 0.35f),
                        radius = r * 2.4f,
                        center = Offset(currentX, currentY)
                    )
                    // Core orb
                    drawCircle(
                        color = orb.color.copy(alpha = orbAlpha),
                        radius = r,
                        center = Offset(currentX, currentY)
                    )
                }
            }

            // 2. Floating "+XP" Cyber Hologram Badge rising upward
            Box(
                modifier = Modifier
                    .offset(y = (-90 * p).dp)
                    .scale(badgeScale.value)
                    .alpha(fadeAlpha)
                    .shadow(16.dp, RoundedCornerShape(20.dp), spotColor = NeonEmerald)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF072E1D), Color(0xFF141836), Color(0xFF072E1D))
                        )
                    )
                    .border(
                        width = 1.8.dp,
                        brush = Brush.horizontalGradient(
                            listOf(NeonEmeraldGlow, CyberGold, CyberCyan)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 8.dp)
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
                        text = "+$activeXpAmount XP GANADOS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonEmeraldGlow
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = NeonEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Función de animación en Jetpack Compose que reacciona cuando una tarjeta de misión
 * cambia su estado a completada (isCompleted == true) y dispara una explosión de partículas
 * cibernéticas con anillo expansivo de energía y destellos sobre la tarjeta.
 */
@Composable
fun QuestCardParticleEffect(
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
    originFraction: Offset = Offset(0.08f, 0.48f)
) {
    var wasCompleted by remember { mutableStateOf(isCompleted) }
    val progress = remember { Animatable(1f) }
    var burstId by remember { mutableStateOf(0L) }

    val palette = remember {
        listOf(
            NeonEmerald,
            NeonEmeraldGlow,
            CyberGold,
            ElectricPurple,
            CyberCyan,
            Color.White
        )
    }

    val particles = remember(burstId) {
        if (burstId == 0L) emptyList()
        else {
            List(32) { index ->
                val angle = (index * (360f / 32f) + Random.nextFloat() * 20f) * (Math.PI.toFloat() / 180f)
                val speed = Random.nextFloat() * 0.5f + 0.5f
                val color = palette[index % palette.size]
                val pSize = Random.nextFloat() * 3.5f + 2f
                val maxDistance = Random.nextFloat() * 85f + 45f
                val shape = when (index % 3) {
                    0 -> ParticleShape.CIRCLE
                    1 -> ParticleShape.DIAMOND
                    else -> ParticleShape.STAR
                }
                val rotationSpeed = (Random.nextFloat() * 360f) * (if (index % 2 == 0) 1 else -1)
                CardParticle(angle, speed, color, pSize, maxDistance, shape, rotationSpeed)
            }
        }
    }

    LaunchedEffect(isCompleted) {
        if (!wasCompleted && isCompleted) {
            burstId = System.currentTimeMillis()
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
            )
        }
        wasCompleted = isCompleted
    }

    if (progress.value < 1f && particles.isNotEmpty()) {
        Canvas(modifier = modifier) {
            val p = progress.value
            val originX = size.width * originFraction.x
            val originY = size.height * originFraction.y
            val center = Offset(originX, originY)
            val alpha = (1f - p).coerceIn(0f, 1f)

            // 1. Shockwave expansivo de energía esmeralda/dorada
            val waveRadius = p * (size.width * 0.65f)
            drawCircle(
                color = NeonEmeraldGlow.copy(alpha = (1f - p) * 0.6f),
                radius = waveRadius,
                center = center,
                style = Stroke(width = ((1f - p) * 4f).dp.toPx())
            )

            // 2. Partículas cinéticas (círculos, diamantes y estrellas)
            particles.forEach { particle ->
                val distance = particle.maxDistance * p * particle.speed
                val px = center.x + (distance * cos(particle.angle))
                val py = center.y + (distance * sin(particle.angle)) - (p * 14f)
                val particleRadius = (particle.size * (1f - p * 0.7f)).dp.toPx()

                when (particle.shape) {
                    ParticleShape.CIRCLE -> {
                        drawCircle(
                            color = particle.color.copy(alpha = alpha),
                            radius = particleRadius,
                            center = Offset(px, py)
                        )
                    }
                    ParticleShape.DIAMOND -> {
                        rotate(degrees = particle.rotationSpeed * p, pivot = Offset(px, py)) {
                            val path = Path().apply {
                                moveTo(px, py - particleRadius * 1.5f)
                                lineTo(px + particleRadius, py)
                                lineTo(px, py + particleRadius * 1.5f)
                                lineTo(px - particleRadius, py)
                                close()
                            }
                            drawPath(path, color = particle.color.copy(alpha = alpha))
                        }
                    }
                    ParticleShape.STAR -> {
                        rotate(degrees = particle.rotationSpeed * p, pivot = Offset(px, py)) {
                            val path = Path().apply {
                                val outer = particleRadius * 1.8f
                                val inner = outer * 0.4f
                                moveTo(px, py - outer)
                                lineTo(px + inner, py - inner)
                                lineTo(px + outer, py)
                                lineTo(px + inner, py + inner)
                                lineTo(px, py + outer)
                                lineTo(px - inner, py + inner)
                                lineTo(px - outer, py)
                                lineTo(px - inner, py - inner)
                                close()
                            }
                            drawPath(path, color = particle.color.copy(alpha = alpha))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskParticleBurst(
    triggerKey: Long,
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    if (triggerKey == 0L) return

    val progress = remember(triggerKey) { Animatable(0f) }

    val particles = remember(triggerKey) {
        val palette = listOf(
            NeonEmerald,
            NeonEmeraldGlow,
            CyberGold,
            ElectricPurple,
            CyberCyan,
            Color.White
        )
        List(24) { index ->
            val angle = (index * (360f / 24f) + Random.nextFloat() * 15f) * (Math.PI.toFloat() / 180f)
            val speed = Random.nextFloat() * 0.4f + 0.6f
            val color = palette[index % palette.size]
            val baseRadius = Random.nextFloat() * 3.5f + 2f
            val distanceMax = Random.nextFloat() * 45f + 40f
            CardParticle(
                angle = angle,
                speed = speed,
                color = color,
                size = baseRadius,
                maxDistance = distanceMax,
                shape = ParticleShape.CIRCLE,
                rotationSpeed = 0f
            )
        }
    }

    LaunchedEffect(triggerKey) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
        )
        onAnimationEnd()
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val currentProgress = progress.value
            val alpha = (1f - currentProgress).coerceIn(0f, 1f)

            particles.forEach { p ->
                val distance = p.maxDistance * currentProgress * p.speed
                val x = center.x + (distance * cos(p.angle))
                val y = center.y + (distance * sin(p.angle)) - (currentProgress * 8f)
                val particleRadius = (p.size * (1f - currentProgress * 0.7f)).dp.toPx()

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = particleRadius,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
fun FloatingRewardBanner(
    reward: FloatingReward?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(reward?.id) {
        if (reward != null) {
            delay(2600L)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = reward != null,
        enter = fadeIn(tween(200)) + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (reward != null) {
            val bgColor = if (reward.isPositive) Color(0xFF132A1C) else Color(0xFF3B101E)
            val borderColor = if (reward.isPositive) NeonEmerald else CyberCrimson

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = borderColor)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor)
                    .border(1.2.dp, borderColor, RoundedCornerShape(14.dp))
                    .clickable { onDismiss() }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (reward.isPositive) Icons.Default.AutoAwesome else Icons.Default.Star,
                    contentDescription = null,
                    tint = if (reward.isPositive) NeonEmerald else CyberCrimson,
                    modifier = Modifier.size(22.dp)
                )

                Text(
                    text = reward.text,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun LevelUpDialog(
    newLevel: Int,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "levelup_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lvl_scale"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(2.dp, CyberGold)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val colors = listOf(NeonEmerald, ElectricPurple, CyberGold)
                    for (i in 0..25) {
                        val x = (i * 37) % size.width
                        val y = (i * 53) % size.height
                        val c = colors[i % colors.size].copy(alpha = 0.35f)
                        drawCircle(c, radius = (3 + (i % 4)).dp.toPx(), center = Offset(x, y))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚡ ¡SUBISTE DE NIVEL! ⚡",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberGold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .scale(pulseScale)
                            .shadow(20.dp, CircleShape, spotColor = NeonEmerald)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(ElectricPurple, CyberSurface)
                                )
                            )
                            .border(3.dp, NeonEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "NIVEL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                            Text(
                                text = "$newLevel",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Tus hábitos han forjado un poder superior!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F1728))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("• Vida Máxima: +15 HP", fontSize = 12.sp, color = NeonEmerald)
                        Text("• Fuerza: +2 Puntos", fontSize = 12.sp, color = CyberGold)
                        Text("• Defensa & Agilidad: +2 Puntos", fontSize = 12.sp, color = ElectricPurple)
                        Text("• HP Restaurado al 100%", fontSize = 12.sp, color = TextWhite)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "¡CONTINUAR LA GESTA!",
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
