package com.elow.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ElowSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun firstLaunchCanEnterAppAndOpenAdd() {
        if (composeRule.onAllNodesWithText("Let's get started").fetchSemanticsNodes().isNotEmpty()) {
            composeRule.onNodeWithText("Elow").assertIsDisplayed()
            composeRule.onNodeWithText("Let's get started").performClick()
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Today").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Today").assertIsDisplayed()
        composeRule.onNodeWithText("+").performClick()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Add Record").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Add Record").assertIsDisplayed()
    }
}
