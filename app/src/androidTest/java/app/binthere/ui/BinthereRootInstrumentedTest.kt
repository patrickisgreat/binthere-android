package app.binthere.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import app.binthere.ui.bins.BinListTestTag
import app.binthere.ui.scanner.QrScannerTestTag
import app.binthere.ui.search.SearchTestTag
import app.binthere.ui.settings.SettingsTestTag
import app.binthere.ui.theme.BinthereTheme
import app.binthere.ui.zones.ZonesGridTestTag
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
        composeRule.onNodeWithTag(BinListTestTag).assertIsDisplayed()

        composeRule.onNodeWithText("Scan").performClick()
        composeRule.onNodeWithTag(QrScannerTestTag).assertIsDisplayed()

        composeRule.onNodeWithText("Search").performClick()
        composeRule.onNodeWithTag(SearchTestTag).assertIsDisplayed()

        composeRule.onNodeWithText("Zones").performClick()
        composeRule.onNodeWithTag(ZonesGridTestTag).assertIsDisplayed()

        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithTag(SettingsTestTag).assertIsDisplayed()
    }
}
