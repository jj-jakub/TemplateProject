package com.jj.templateproject.presentation

import com.jj.templateproject.domain.game.GameStateStorage
import com.jj.templateproject.domain.game.SavedGameState

class FakeGameStateStorage : GameStateStorage {

    private val slots = mutableMapOf<String, SavedGameState>()

    override suspend fun save(slot: String, state: SavedGameState) {
        slots[slot] = state
    }

    override suspend fun load(slot: String): SavedGameState? = slots[slot]

    override suspend fun delete(slot: String) {
        slots.remove(slot)
    }
}
