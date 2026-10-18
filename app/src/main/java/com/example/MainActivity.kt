package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CyberBottomNav
import com.example.ui.components.CyberTopBar
import com.example.ui.components.FloatingMysteryChest
import com.example.ui.components.FloatingRewardBanner
import com.example.ui.components.LevelUpDialog
import com.example.ui.components.LuckyWheelDialog
import com.example.ui.components.RewardedAdDialog
import com.example.ui.dialogs.AddRewardDialog
import com.example.ui.dialogs.AddTaskDialog
import com.example.ui.screens.BossScreen
import com.example.ui.screens.HeroScreen
import com.example.ui.screens.QuestsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.CyberBg
import com.example.ui.theme.HabitQuestTheme
import com.example.viewmodel.BottomNavTab
import com.example.viewmodel.HabitQuestViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HabitQuestTheme {
                HabitQuestApp()
            }
        }
    }
}

@Composable
fun HabitQuestApp(
    viewModel: HabitQuestViewModel = viewModel()
) {
    val hero by viewModel.hero.collectAsStateWithLifecycle()
    val enemies by viewModel.enemies.collectAsStateWithLifecycle()
    val quests by viewModel.quests.collectAsStateWithLifecycle()
    val boss by viewModel.boss.collectAsStateWithLifecycle()
    val shopItems by viewModel.shopItems.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val freeSpins by viewModel.freeSpins.collectAsStateWithLifecycle()
    val floatingReward by viewModel.floatingReward.collectAsStateWithLifecycle()
    val levelUpCelebration by viewModel.levelUpCelebration.collectAsStateWithLifecycle()

    val isChestModalOpen by viewModel.isChestModalOpen.collectAsStateWithLifecycle()
    val isWheelModalOpen by viewModel.isWheelModalOpen.collectAsStateWithLifecycle()
    val isAddTaskModalOpen by viewModel.isAddTaskModalOpen.collectAsStateWithLifecycle()
    val isAddRewardModalOpen by viewModel.isAddRewardModalOpen.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBg),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CyberTopBar(
                hero = hero,
                onWheelClick = viewModel::openWheelModal,
                modifier = Modifier.statusBarsPadding()
            )
        },
        bottomBar = {
            CyberBottomNav(
                activeTab = currentTab,
                onTabSelected = viewModel::selectTab
            )
        },
        floatingActionButton = {
            FloatingMysteryChest(
                onClick = viewModel::openChestModal,
                modifier = Modifier.padding(bottom = 4.dp, end = 4.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CyberBg)
        ) {
            when (currentTab) {
                BottomNavTab.QUESTS -> {
                    QuestsScreen(
                        quests = quests,
                        onCompleteQuest = viewModel::completeQuest,
                        onFailHabit = viewModel::failHabit,
                        onDeleteQuest = viewModel::deleteQuest,
                        onOpenAddTask = viewModel::openAddTaskModal,
                        onResetDaily = viewModel::resetDailyHabits
                    )
                }

                BottomNavTab.HERO -> {
                    HeroScreen(
                        hero = hero,
                        enemies = enemies,
                        shopItems = shopItems,
                        onEquipItem = viewModel::equipShopItem,
                        onReviveWithAd = viewModel::reviveHeroWithAd,
                        onActivateShieldWithAd = viewModel::activateDivineShieldWithAd,
                        onAttackEnemy = viewModel::attackEnemyDirect,
                        onRespawnEnemies = viewModel::respawnEnemies
                    )
                }

                BottomNavTab.SHOP -> {
                    ShopScreen(
                        hero = hero,
                        shopItems = shopItems,
                        onBuyItem = viewModel::purchaseShopItem,
                        onOpenAddReward = viewModel::openAddRewardModal
                    )
                }

                BottomNavTab.BOSS -> {
                    BossScreen(
                        boss = boss,
                        hero = hero,
                        onSpecialAttack = viewModel::attackBossSpecial,
                        onRespawnBoss = viewModel::respawnBoss
                    )
                }
            }

            // Visual XP Gain Feedback Animation (ascending XP orbs + floating +XP hologram badge)
            com.example.ui.components.XpGainFeedbackOverlay(
                reward = floatingReward
            )

            // Floating Reward Banner overlay at top of content
            FloatingRewardBanner(
                reward = floatingReward,
                onDismiss = viewModel::clearFloatingReward,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    // Dialogs
    if (isChestModalOpen) {
        RewardedAdDialog(
            hero = hero,
            onDismiss = viewModel::closeChestModal,
            onClaimReward = viewModel::claimChestReward
        )
    }

    if (isWheelModalOpen) {
        LuckyWheelDialog(
            freeSpins = freeSpins,
            onDismiss = viewModel::closeWheelModal,
            onSpinComplete = viewModel::applySpinPrize
        )
    }

    if (isAddTaskModalOpen) {
        AddTaskDialog(
            onDismiss = viewModel::closeAddTaskModal,
            onCreateQuest = viewModel::createQuest
        )
    }

    if (isAddRewardModalOpen) {
        AddRewardDialog(
            onDismiss = viewModel::closeAddRewardModal,
            onSaveReward = viewModel::addCustomReward
        )
    }

    levelUpCelebration?.let { level ->
        LevelUpDialog(
            newLevel = level,
            onDismiss = viewModel::clearLevelUp
        )
    }
}
