package app.binthere.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.binthere.ui.components.PlaceholderScreen
import app.binthere.ui.theme.BinthereTheme

const val SEARCH_TEST_TAG = "screen.search"

@Composable
fun SearchScreen() {
    PlaceholderScreen(
        title = "Search",
        subtitle = "Find any item across every bin.",
        testTag = SEARCH_TEST_TAG,
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    BinthereTheme { SearchScreen() }
}
