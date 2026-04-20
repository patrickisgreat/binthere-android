package app.binthere.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import app.binthere.ui.bins.BIN_LIST_TEST_TAG
import app.binthere.ui.scanner.QR_SCANNER_TEST_TAG
import app.binthere.ui.search.SEARCH_TEST_TAG
import app.binthere.ui.settings.SETTINGS_TEST_TAG
import app.binthere.ui.theme.BinthereTheme
import app.binthere.ui.zones.ZONES_GRID_TEST_TAG
import org.junit.Rule
import org.junit.Test

class BinthereRootInstrumentedTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun bottomNav_showsAllFiveDestinations() {
        composeRule.setContent { BinthereTheme { BinthereRoot() } }
        listOf("Search", "Bins", "Zones", "Scan", "Settings").forEach { label ->
            composeRule.onNodeWithText(label).assertIsDisplayed()
        }
    }

    @Test
    fun bottomNav_startsOnBinsAndSwitchesToScanner() {
        composeRule.setContent { BinthereTheme { BinthereRoot() } }
        composeRule.onNodeWithTag(BIN_LIST_TEST_TAG).assertIsDisplayed()

        composeRule.onNodeWithText("Scan").performClick()
        composeRule.onNodeWithTag(QR_SCANNER_TEST_TAG).assertIsDisplayed()

        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithTag(SEARCH_TEST_TAG).assertIsDisplayed()

        composeRule.onNodeWithText("Zones").performClick()
        composeRule.onNodeWithTag(ZONES_GRID_TEST_TAG).assertIsDisplayed()

        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithTag(SETTINGS_TEST_TAG).assertIsDisplayed()
    }
}
