package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGoldDark
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextWhite

@Composable
fun FloatingMysteryChest(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chest_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_pulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier
            .testTag("mystery_chest_button")
            .scale(scale)
            .clickable { onClick() }
    ) {
        // Glowing Outer Halo
        Box(
            modifier = Modifier
                .size(62.dp)
                .shadow(16.dp, CircleShape, spotColor = CyberGold)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(CyberGold.copy(alpha = glowAlpha), Color.Transparent)
                    )
                )
        )

        // Chest Body
        Box(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(CyberGoldDark, Color(0xFF78350F), Color(0xFF1E1035))
                    )
                )
                .border(2.dp, CyberGold, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = "Cofre Misterioso",
                    tint = CyberGold,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Rewarded Video Tag / Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-2).dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberCrimson)
                .border(1.dp, TextWhite, RoundedCornerShape(8.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = "AD 2x",
                fontSize = 8.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextWhite
            )
        }
    }
}
