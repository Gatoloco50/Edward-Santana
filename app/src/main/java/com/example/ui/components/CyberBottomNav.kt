package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCrimson
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.TextGray
import com.example.viewmodel.BottomNavTab

@Composable
fun CyberBottomNav(
    activeTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(CyberSurface, CyberBg)
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(Color.Transparent, CyberCardBorder, Color.Transparent)
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavTab.entries.forEach { tab ->
                val isSelected = activeTab == tab
                val icon = when (tab) {
                    BottomNavTab.QUESTS -> Icons.Default.FormatListBulleted
                    BottomNavTab.HERO -> Icons.Default.Person
                    BottomNavTab.SHOP -> Icons.Default.ShoppingBag
                    BottomNavTab.BOSS -> Icons.Default.Dangerous
                }

                val activeColor = when (tab) {
                    BottomNavTab.QUESTS -> NeonEmerald
                    BottomNavTab.HERO -> ElectricPurple
                    BottomNavTab.SHOP -> CyberGold
                    BottomNavTab.BOSS -> CyberCrimson
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) activeColor else TextGray,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = tab.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) activeColor else TextGray
                        )
                    }
                }
            }
        }
    }
}
