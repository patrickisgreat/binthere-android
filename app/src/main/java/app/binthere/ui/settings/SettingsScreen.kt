package app.binthere.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.binthere.ui.components.PlaceholderScreen
import app.binthere.ui.theme.BinthereTheme

const val SettingsTestTag = "screen.settings"

@Composable
fun SettingsScreen() {
    PlaceholderScreen(
        title = "Settings",
        subtitle = "Account, household, notifications, API keys.",
        testTag = SettingsTestTag,
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    BinthereTheme { SettingsScreen() }
}
