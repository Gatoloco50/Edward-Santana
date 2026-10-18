package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.ElectricPurpleDark
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class WheelPrize(
    val label: String,
    val color: Color
)

@Composable
fun LuckyWheelDialog(
    freeSpins: Int,
    onDismiss: () -> Unit,
    onSpinComplete: (prizeIndex: Int, isAdSpin: Boolean) -> Unit
) {
    val prizes = remember {
        listOf(
            WheelPrize("+200 Oro", Color(0xFFD97706)),
            WheelPrize("+150 XP", Color(0xFF059669)),
            WheelPrize("Poción 100%", Color(0xFFDC2626)),
            WheelPrize("+500 Oro", Color(0xFFEAB308)),
            WheelPrize("+300 XP", Color(0xFF7C3AED)),
            WheelPrize("+100 Oro", Color(0xFF2563EB))
        )
    }

    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(false) }
    var isWatchingAd by remember { mutableStateOf(false) }
    var adTimer by remember { mutableIntStateOf(3) }
    var lastPrizeWon by remember { mutableStateOf<String?>(null) }

    fun startSpin(isAd: Boolean) {
        if (isSpinning) return
        isSpinning = true
        lastPrizeWon = null

        scope.launch {
            val targetPrizeIndex = Random.nextInt(prizes.size)
            // Each segment is 60 degrees.
            // Pointer is at the top (270 degrees).
            val segmentAngle = 360f / prizes.size
            val targetAngle = 360f - (targetPrizeIndex * segmentAngle + segmentAngle / 2f) + 270f
            val fullRotations = 5 * 360f
            val finalRotation = rotation.value + fullRotations + (targetAngle % 360f)

            rotation.animateTo(
                targetValue = finalRotation,
                animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing)
            )

            isSpinning = false
            lastPrizeWon = prizes[targetPrizeIndex].label
            onSpinComplete(targetPrizeIndex, isAd)
        }
    }

    Dialog(onDismissRequest = { if (!isSpinning && !isWatchingAd) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("lucky_wheel_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricPurple)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Ruleta de la Suerte",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSpinning && !isWatchingAd,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ad playing screen if watching ad
                if (isWatchingAd) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberSurfaceVariant)
                            .border(1.dp, CyberGold, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = CyberGold,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Viendo Anuncio Recompensado...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Giro extra desbloqueado en: ${adTimer}s",
                                fontSize = 12.sp,
                                color = CyberGold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { (3 - adTimer) / 3f },
                                modifier = Modifier.fillMaxWidth(0.8f),
                                color = CyberGold,
                                trackColor = CyberCardBorder
                            )
                        }
                    }
                } else {
                    // Wheel Canvas with top indicator
                    Box(
                        modifier = Modifier
                            .size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // The rotating wheel canvas
                        Canvas(
                            modifier = Modifier
                                .size(210.dp)
                                .rotate(rotation.value)
                        ) {
                            val canvasSize = size.minDimension
                            val radius = canvasSize / 2f
                            val center = Offset(radius, radius)
                            val segmentSweep = 360f / prizes.size

                            prizes.forEachIndexed { index, prize ->
                                val startAngle = index * segmentSweep
                                drawArc(
                                    color = prize.color,
                                    startAngle = startAngle,
                                    sweepAngle = segmentSweep,
                                    useCenter = true,
                                    size = Size(canvasSize, canvasSize),
                                    topLeft = Offset.Zero
                                )

                                drawArc(
                                    color = Color(0x33000000),
                                    startAngle = startAngle,
                                    sweepAngle = segmentSweep,
                                    useCenter = true,
                                    size = Size(canvasSize, canvasSize),
                                    topLeft = Offset.Zero,
                                    style = Stroke(width = 2f)
                                )

                                // Text label
                                val midAngleRad = Math.toRadians((startAngle + segmentSweep / 2.0))
                                val textRadius = radius * 0.65f
                                val textX = center.x + (textRadius * cos(midAngleRad)).toFloat()
                                val textY = center.y + (textRadius * sin(midAngleRad)).toFloat()

                                drawContext.canvas.nativeCanvas.apply {
                                    val paint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.WHITE
                                        textSize = 24f
                                        isFakeBoldText = true
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                                    }
                                    drawText(prize.label, textX, textY, paint)
                                }
                            }

                            // Outer glowing border
                            drawCircle(
                                color = ElectricPurple,
                                radius = radius,
                                center = center,
                                style = Stroke(width = 6f)
                            )
                        }

                        // Center Hub
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(6.dp, CircleShape)
                                .clip(CircleShape)
                                .background(CyberGold)
                                .border(2.dp, TextWhite, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RotateRight,
                                contentDescription = null,
                                tint = CyberBg,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Top Pointer Needle
                        Canvas(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopCenter)
                        ) {
                            val path = Path().apply {
                                moveTo(size.width / 2f, size.height)
                                lineTo(0f, 0f)
                                lineTo(size.width, 0f)
                                close()
                            }
                            drawPath(path, color = CyberGold)
                            drawPath(path, color = TextWhite, style = Stroke(width = 2f))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (lastPrizeWon != null) {
                        Text(
                            text = "🎉 ¡Premio Ganado: $lastPrizeWon!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyberGold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Spin Actions
                    if (freeSpins > 0) {
                        Button(
                            onClick = { startSpin(isAd = false) },
                            enabled = !isSpinning,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("free_spin_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = null,
                                tint = CyberBg,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "¡GIRO GRATIS DIARIO! ($freeSpins)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberBg
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                isWatchingAd = true
                                adTimer = 3
                                scope.launch {
                                    while (adTimer > 0) {
                                        delay(1000L)
                                        adTimer--
                                    }
                                    isWatchingAd = false
                                    startSpin(isAd = true)
                                }
                            },
                            enabled = !isSpinning,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("ad_spin_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = null,
                                tint = CyberBg,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VER ANUNCIO PARA GIRAR DE NUEVO",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CyberBg
                            )
                        }
                    }
                }
            }
        }
    }
}
