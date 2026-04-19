package app.binthere.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.binthere.ui.bins.BinListScreen
import app.binthere.ui.navigation.TopLevelDestination
import app.binthere.ui.navigation.TopLevelRoute
import app.binthere.ui.scanner.QrScannerScreen
import app.binthere.ui.search.SearchScreen
import app.binthere.ui.settings.SettingsScreen
import app.binthere.ui.zones.ZonesGridScreen

@Composable
fun BinthereRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                TopLevelDestination.entries.forEach { dest ->
                    val selected = currentDestination.isOn(dest)
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = null) },
                        label = { Text(stringResource(dest.labelRes)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelRoute.Bins,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<TopLevelRoute.Search> { SearchScreen() }
            composable<TopLevelRoute.Bins> { BinListScreen() }
            composable<TopLevelRoute.Zones> { ZonesGridScreen() }
            composable<TopLevelRoute.Scanner> { QrScannerScreen() }
            composable<TopLevelRoute.Settings> { SettingsScreen() }
        }
    }
}

private fun NavDestination?.isOn(dest: TopLevelDestination): Boolean = when (dest) {
    TopLevelDestination.SEARCH -> this?.hasRoute<TopLevelRoute.Search>() == true
    TopLevelDestination.BINS -> this?.hasRoute<TopLevelRoute.Bins>() == true
    TopLevelDestination.ZONES -> this?.hasRoute<TopLevelRoute.Zones>() == true
    TopLevelDestination.SCANNER -> this?.hasRoute<TopLevelRoute.Scanner>() == true
    TopLevelDestination.SETTINGS -> this?.hasRoute<TopLevelRoute.Settings>() == true
}
