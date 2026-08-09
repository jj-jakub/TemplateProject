package com.jj.templateproject.core.data.game

import com.jj.templateproject.domain.game.GameStateStorage
import com.jj.templateproject.domain.game.SavedGameState
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * [GameStateStorage] over a [FileTextStore]: each slot becomes its own JSON file. Shared across
 * both platforms — only the raw file I/O behind [FileTextStore] differs.
 *
 * A slot that fails to parse (a corrupted or foreign-format file) reads as `null` rather than
 * throwing, the same resilience `DataStoreAppPreferencesRepository` applies to a failed read: a
 * broken save should degrade to "nothing saved", not crash whatever screen asked for it.
 */
class DefaultGameStateStorage(
    private val fileTextStore: FileTextStore,
) : GameStateStorage {

    override suspend fun save(slot: String, state: SavedGameState) {
        fileTextStore.writeText(fileNameFor(slot), Json.encodeToString(state))
    }

    override suspend fun load(slot: String): SavedGameState? {
        val text = fileTextStore.readText(fileNameFor(slot)) ?: return null
        return runCatching { Json.decodeFromString<SavedGameState>(text) }.getOrNull()
    }

    override suspend fun delete(slot: String) {
        fileTextStore.delete(fileNameFor(slot))
    }

    private fun fileNameFor(slot: String) = "gamestate_$slot.json"
}
