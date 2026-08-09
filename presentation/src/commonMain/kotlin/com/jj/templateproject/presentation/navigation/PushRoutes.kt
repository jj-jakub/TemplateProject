package com.jj.templateproject.presentation.navigation

import com.jj.templateproject.domain.push.PushDestination
import com.jj.templateproject.presentation.navigation.model.Route

/**
 * Maps a [PushDestination] onto the screen that shows it.
 *
 * The one place the two vocabularies meet: the domain names destinations without knowing this app's
 * navigation graph, and the graph does not know about push. Keeping the mapping here (and pure)
 * means a new destination is a compile error until a screen is named for it, rather than a silent
 * no-op at runtime.
 */
object PushRoutes {

    /** The route a destination opens, or null for one that never enters the app at all. */
    fun routeFor(destination: PushDestination): Route? = when (destination) {
        PushDestination.Home -> Route.MainScreen
        PushDestination.Settings -> Route.SettingsScreen
        // Opened straight from the notification as a view intent, so it never reaches navigation.
        is PushDestination.PlayStoreApp -> null
    }
}
