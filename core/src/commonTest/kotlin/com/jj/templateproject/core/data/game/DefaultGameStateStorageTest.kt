package com.jj.templateproject.core.data.game

import com.jj.templateproject.domain.game.SavedGameState
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

class DefaultGameStateStorageTest {

    private class FakeFileTextStore : FileTextStore {
        val files = mutableMapOf<String, String>()

        override suspend fun writeText(fileName: String, content: String) {
            files[fileName] = content
        }

        override suspend fun readText(fileName: String): String? = files[fileName]

        override suspend fun delete(fileName: String) {
            files.remove(fileName)
        }
    }

    @Test
    fun `a slot that was never saved reads as null`() = runTest {
        val storage = DefaultGameStateStorage(FakeFileTextStore())

        assertNull(storage.load("slot_1"))
    }

    @Test
    fun `a saved slot reads back the same state`() = runTest {
        val storage = DefaultGameStateStorage(FakeFileTextStore())
        val state = SavedGameState(score = 42, progress = 0.5f, savedAtEpochMillis = 1_000L)

        storage.save("slot_1", state)

        assertEquals(state, storage.load("slot_1"))
    }

    @Test
    fun `saving overwrites the previous state in the same slot`() = runTest {
        val storage = DefaultGameStateStorage(FakeFileTextStore())
        storage.save("slot_1", SavedGameState(score = 1, progress = 0f, savedAtEpochMillis = 1L))

        val latest = SavedGameState(score = 2, progress = 1f, savedAtEpochMillis = 2L)
        storage.save("slot_1", latest)

        assertEquals(latest, storage.load("slot_1"))
    }

    @Test
    fun `different slots do not overwrite each other`() = runTest {
        val storage = DefaultGameStateStorage(FakeFileTextStore())
        val first = SavedGameState(score = 1, progress = 0f, savedAtEpochMillis = 1L)
        val second = SavedGameState(score = 2, progress = 1f, savedAtEpochMillis = 2L)

        storage.save("slot_1", first)
        storage.save("slot_2", second)

        assertEquals(first, storage.load("slot_1"))
        assertEquals(second, storage.load("slot_2"))
    }

    @Test
    fun `deleting a slot clears it`() = runTest {
        val storage = DefaultGameStateStorage(FakeFileTextStore())
        storage.save("slot_1", SavedGameState(score = 1, progress = 0f, savedAtEpochMillis = 1L))

        storage.delete("slot_1")

        assertNull(storage.load("slot_1"))
    }

    @Test
    fun `a corrupted file reads as null rather than throwing`() = runTest {
        val fileTextStore = FakeFileTextStore()
        val storage = DefaultGameStateStorage(fileTextStore)
        fileTextStore.files["gamestate_slot_1.json"] = "not valid json"

        assertNull(storage.load("slot_1"))
    }
}
