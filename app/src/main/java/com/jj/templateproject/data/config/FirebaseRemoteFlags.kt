package com.jj.templateproject.data.config

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.jj.templateproject.domain.config.NoOpRemoteFlags
import com.jj.templateproject.domain.config.RemoteFlags

/**
 * Reads [RemoteFlags] from Firebase Remote Config.
 *
 * Only a value that was actually **published** counts as an override: anything else falls through to
 * the caller's in-code default. That is why no defaults are registered with the SDK at all, and why
 * every read checks the value's source rather than trusting `getLong`/`getBoolean`, which return a
 * zero-shaped value for a key nobody has ever published.
 *
 * A malformed published value (text where a number was expected) also falls through to the default,
 * so a typo in the console degrades to the shipped behaviour instead of to zero.
 */
class FirebaseRemoteFlags(
    private val config: FirebaseRemoteConfig,
) : RemoteFlags {

    override fun int(key: String, default: Int): Int =
        published(key)?.let { runCatching { it.asLong().toInt() }.getOrNull() } ?: default

    override fun bool(key: String, default: Boolean): Boolean =
        published(key)?.let { runCatching { it.asBoolean() }.getOrNull() } ?: default

    override fun string(key: String, default: String): String =
        published(key)?.asString()?.takeIf(String::isNotEmpty) ?: default

    private fun published(key: String): FirebaseRemoteConfigValue? =
        config.getValue(key).takeIf { it.source == FirebaseRemoteConfig.VALUE_SOURCE_REMOTE }

    companion object {

        /**
         * The Remote Config binding for this build, or [NoOpRemoteFlags] when there is no Firebase
         * project to read from.
         *
         * The fetch is kicked off here and never waited on: values already on the device are used
         * immediately and a newly published one takes effect on a later launch. A flag that blocks
         * startup on a network call is a flag that can stop the app from opening, which no
         * configuration value is worth.
         */
        fun create(context: Context): RemoteFlags {
            if (FirebaseApp.getApps(context).isEmpty()) return NoOpRemoteFlags
            val config = FirebaseRemoteConfig.getInstance()
            config.fetchAndActivate()
            return FirebaseRemoteFlags(config)
        }
    }
}
