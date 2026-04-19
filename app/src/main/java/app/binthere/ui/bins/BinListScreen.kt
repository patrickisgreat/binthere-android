package app.binthere.ui.bins

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.binthere.ui.components.PlaceholderScreen
import app.binthere.ui.theme.BinthereTheme

const val BinListTestTag = "screen.bins"

@Composable
fun BinListScreen() {
    PlaceholderScreen(
        title = "Bins",
        subtitle = "Your bins will show up here.",
        testTag = BinListTestTag,
    )
}

@Preview
@Composable
private fun BinListScreenPreview() {
    BinthereTheme { BinListScreen() }
}
