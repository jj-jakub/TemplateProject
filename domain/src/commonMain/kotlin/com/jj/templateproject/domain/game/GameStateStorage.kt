package com.jj.templateproject.domain.game

/**
 * Save-slot persistence for [SavedGameState]. Slots are caller-chosen names (e.g. `"autosave"`,
 * `"slot_1"`) rather than a fixed single save, so a branching game can offer multiple save files
 * without a second interface.
 *
 * A missing or corrupted slot reads as `null` from [load] rather than throwing — the same
 * resilience `AppPreferencesRepository`'s DataStore implementation applies to a failed read —
 * because a save file existing at all is inherently optional (a brand-new install has none) and a
 * corrupted one should degrade to "nothing saved", not crash whatever screen asked.
 */
interface GameStateStorage {
    suspend fun save(slot: String, state: SavedGameState)
    suspend fun load(slot: String): SavedGameState?
    suspend fun delete(slot: String)
}
