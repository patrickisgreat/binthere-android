package app.binthere.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TopLevelDestinationTest {

    @Test
    fun `exposes exactly five top level destinations`() {
        assertEquals(5, TopLevelDestination.entries.size)
    }

    @Test
    fun `every destination has a unique route`() {
        val routes = TopLevelDestination.entries.map { it.route::class }
        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun `routes match the iOS tab bar ordering`() {
        val orderedRoutes = TopLevelDestination.entries.map { it.route }
        assertEquals(
            listOf(
                TopLevelRoute.Search,
                TopLevelRoute.Bins,
                TopLevelRoute.Zones,
                TopLevelRoute.Scanner,
                TopLevelRoute.Settings,
            ),
            orderedRoutes,
        )
    }

    @Test
    fun `every destination has a non-zero label resource`() {
        assertTrue(TopLevelDestination.entries.all { it.labelRes != 0 })
    }
}
