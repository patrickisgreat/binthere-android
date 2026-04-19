package app.binthere.ui.zones

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.binthere.ui.components.PlaceholderScreen
import app.binthere.ui.theme.BinthereTheme

const val ZonesGridTestTag = "screen.zones"

@Composable
fun ZonesGridScreen() {
    PlaceholderScreen(
        title = "Zones",
        subtitle = "Group bins by location: garage, kitchen, closet.",
        testTag = ZonesGridTestTag,
    )
}

@Preview
@Composable
private fun ZonesGridScreenPreview() {
    BinthereTheme { ZonesGridScreen() }
}
