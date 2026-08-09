package com.jj.templateproject.core.data.game

/**
 * Raw text file I/O, scoped to a per-app private directory (Android's `filesDir`, iOS's
 * `Documents`). Internal to `:core` — [DefaultGameStateStorage] is the only consumer; nothing above
 * this layer needs to know save data is backed by a raw file rather than, say, a database.
 *
 * A plain interface with one implementation per platform, not an `expect`/`actual` class: the
 * Android implementation genuinely needs a `Context` its iOS counterpart has no equivalent of, and
 * every other platform-diverging capability in this codebase (`Clock`, `DeviceInfo`, `ContentSharer`)
 * already takes this same shape rather than a parameterized `expect` constructor.
 */
interface FileTextStore {
    suspend fun writeText(fileName: String, content: String)
    suspend fun readText(fileName: String): String?
    suspend fun delete(fileName: String)
}
