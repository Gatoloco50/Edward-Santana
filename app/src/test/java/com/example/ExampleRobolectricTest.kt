package com.example

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testAppLaunchesAndInteractionsWork() {
        composeTestRule.onNodeWithTag("tab_quests").assertExists()
        composeTestRule.onNodeWithTag("tab_hero").performClick()
        composeTestRule.onNodeWithTag("hero_profile_card").assertExists()
    }
}
