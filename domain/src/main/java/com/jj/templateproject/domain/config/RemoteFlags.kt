package com.jj.templateproject.domain.config

/**
 * Values that can be changed without shipping a build.
 *
 * A rule written in Kotlin is frozen on every device carrying it until that device updates, which
 * for the users who most need a rule corrected is exactly the thing that is not happening. A value
 * read through here can be changed in an afternoon, and a kill switch does not have to wait for
 * adoption at all.
 *
 * Deliberately narrow. Only two kinds of value belong here:
 *
 * - Ones re-evaluated on every decision. A value that is written into persisted state the first time
 *   it is used is NOT one: changing it remotely would only ever reach fresh installs, which looks
 *   like a lever and is not one.
 * - Kill switches over a gate that already exists in exactly one place, so turning something off
 *   cannot leave the app in a state no code path expects.
 *
 * **Defaults are the in-code constants themselves, never a second copy.** A remote value is an
 * override and nothing else, so offline, first run and every unit test behave exactly as they did
 * before this existed, by construction rather than by keeping two lists of numbers in agreement.
 *
 * ```
 * val timeoutSeconds = remoteFlags.int("network_timeout_seconds", DEFAULT_TIMEOUT_SECONDS)
 * ```
 */
interface RemoteFlags {

    /** [key]'s published value, or [default] when nothing usable has been published for it. */
    fun int(key: String, default: Int): Int

    /** [key]'s published value, or [default] when nothing usable has been published for it. */
    fun bool(key: String, default: Boolean): Boolean

    /** [key]'s published value, or [default] when nothing usable has been published for it. */
    fun string(key: String, default: String): String
}

/**
 * Everything at its built-in default. The binding for a build with no remote config configured, and
 * the one every test gets, which is what makes a flag invisible to a test that does not care about
 * it.
 */
object NoOpRemoteFlags : RemoteFlags {
    override fun int(key: String, default: Int) = default
    override fun bool(key: String, default: Boolean) = default
    override fun string(key: String, default: String) = default
}
