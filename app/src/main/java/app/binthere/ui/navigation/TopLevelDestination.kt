package app.binthere.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import app.binthere.R
import kotlinx.serialization.Serializable

sealed interface TopLevelRoute {
    @Serializable data object Search : TopLevelRoute
    @Serializable data object Bins : TopLevelRoute
    @Serializable data object Zones : TopLevelRoute
    @Serializable data object Scanner : TopLevelRoute
    @Serializable data object Settings : TopLevelRoute
}

enum class TopLevelDestination(
    val route: TopLevelRoute,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
) {
    SEARCH(TopLevelRoute.Search, Icons.Filled.Search, R.string.nav_search),
    BINS(TopLevelRoute.Bins, Icons.Filled.Inventory2, R.string.nav_bins),
    ZONES(TopLevelRoute.Zones, Icons.AutoMirrored.Filled.List, R.string.nav_zones),
    SCANNER(TopLevelRoute.Scanner, Icons.Filled.QrCodeScanner, R.string.nav_scanner),
    SETTINGS(TopLevelRoute.Settings, Icons.Filled.Settings, R.string.nav_settings),
}
