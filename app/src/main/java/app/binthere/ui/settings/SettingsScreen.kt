package app.binthere.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.binthere.ui.components.PlaceholderScreen
import app.binthere.ui.theme.BinthereTheme

const val SETTINGS_TEST_TAG = "screen.settings"

@Composable
fun SettingsScreen() {
    PlaceholderScreen(
        title = "Settings",
        subtitle = "Account, household, notifications, API keys.",
        testTag = SETTINGS_TEST_TAG,
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    BinthereTheme { SettingsScreen() }
}
