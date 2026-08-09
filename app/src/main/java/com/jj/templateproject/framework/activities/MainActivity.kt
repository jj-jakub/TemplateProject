package com.jj.templateproject.framework.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import com.jj.templateproject.core.data.notifications.PushIntents
import com.jj.templateproject.domain.push.PushDeepLink
import com.jj.templateproject.domain.push.PushDestination
import com.jj.templateproject.domain.reliability.LaunchStability
import com.jj.templateproject.presentation.MainRoot
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val launchStability: LaunchStability by inject()

    /**
     * Where a notification tap (or a `templateproject://` link) asked to go, until the composition
     * has acted on it. Held as state rather than read once, because this activity is `singleTop`:
     * a tap that arrives while the app is already open comes through [onNewIntent], long after
     * [onCreate] read the original intent.
     */
    private val pushDestination = mutableStateOf<PushDestination?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        pushDestination.value = destinationOf(intent)
        setContent {
            val navHostController = rememberNavController()
            MainRoot(
                navController = navHostController,
                viewModel = koinViewModel(),
                pushDestination = pushDestination.value,
                onPushDestinationHandled = { pushDestination.value = null },
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // The app got as far as showing something, so whatever it restored did not kill it. Marking
        // stability here rather than in onCreate is the point: a crash while composing the first
        // screen still counts as a failed launch.
        launchStability.markStable()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Keeps getIntent() in step with what actually arrived, so anything else reading it later
        // does not see the intent this activity was originally launched with.
        setIntent(intent)
        destinationOf(intent)?.let { pushDestination.value = it }
    }

    /**
     * Both routes in are untrusted: a notification we built carries the destination as an extra, and
     * a browsable deep link can be sent by anything at all. Either way an unrecognised value parses
     * to null and nothing happens.
     */
    private fun destinationOf(intent: Intent?): PushDestination? =
        PushIntents.destinationOf(intent) ?: PushDeepLink.parse(intent?.data?.toString())
}
