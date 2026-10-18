package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Hero
import com.example.model.ShopItem
import com.example.model.ShopItemType
import com.example.ui.components.NativeBannerAd
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
fun ShopScreen(
    hero: Hero,
    shopItems: List<ShopItem>,
    onBuyItem: (ShopItem) -> Unit,
    onOpenAddReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Todas", "Arsenal", "Pociones", "Recompensas Reales")

    val shopVisibleItems = shopItems.filter { it.origin != "Botín de Misión" }
    val filteredItems = when (selectedTab) {
        1 -> shopVisibleItems.filter { it.isEquippableGear }
        2 -> shopVisibleItems.filter { it.type == ShopItemType.POTION }
        3 -> shopVisibleItems.filter { it.type == ShopItemType.CUSTOM_REWARD }
        else -> shopVisibleItems
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Native Banner Ad at the top as requested
        item {
            NativeBannerAd()
        }

        // Top bar with Gold balance & Add Custom Reward Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Oro",
                        tint = CyberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Oro: ${hero.gold}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberGold
                    )
                }

                Button(
                    onClick = onOpenAddReward,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("add_custom_reward_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = CyberBg,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+ Crear Recompensa",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberBg
                    )
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF1E1438) else CyberSurface)
                            .border(
                                1.dp,
                                if (isSelected) ElectricPurple else CyberCardBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ElectricPurple else TextGray
                        )
                    }
                }
            }
        }

        // Shop Items list
        items(filteredItems, key = { it.id }) { item ->
            ShopItemCard(
                item = item,
                canAfford = hero.gold >= item.cost,
                onBuy = { onBuyItem(item) }
            )
        }
    }
}

@Composable
fun ShopItemCard(
    item: ShopItem,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    val icon = when (item.type) {
        ShopItemType.WEAPON -> Icons.Default.Shield
        ShopItemType.ARMOR -> Icons.Default.Security
        ShopItemType.ACCESSORY -> Icons.Default.Star
        ShopItemType.POTION -> Icons.Default.LocalDrink
        ShopItemType.CUSTOM_REWARD -> Icons.Default.SportsEsports
    }

    val iconColor = when (item.type) {
        ShopItemType.WEAPON -> NeonEmerald
        ShopItemType.ARMOR -> ElectricPurple
        ShopItemType.ACCESSORY -> Color(0xFF00E5FF)
        ShopItemType.POTION -> CyberCrimson
        ShopItemType.CUSTOM_REWARD -> CyberGold
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shop_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f))
                    .border(1.dp, iconColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                if (item.statBoost.isNotBlank()) {
                    Text(
                        text = item.statBoost,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = iconColor
                    )
                }

                Text(
                    text = item.description,
                    fontSize = 10.sp,
                    color = TextGray,
                    lineHeight = 13.sp
                )
            }

            // Purchase Button or Status
            if (item.isPurchased && item.isEquippableGear) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF00381C))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "En Inventario",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonEmerald
                    )
                }
            } else {
                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("buy_button_${item.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.type == ShopItemType.CUSTOM_REWARD) CyberGold else ElectricPurple,
                        disabledContainerColor = CyberSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = if (canAfford) CyberBg else TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.cost}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (canAfford) CyberBg else TextMuted
                    )
                }
            }
        }
    }
}
