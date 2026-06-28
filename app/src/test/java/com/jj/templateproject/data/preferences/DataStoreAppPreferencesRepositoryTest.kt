package com.jj.templateproject.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class DataStoreAppPreferencesRepositoryTest {

    private fun repository(scope: CoroutineScope, dir: File) =
        DataStoreAppPreferencesRepository(
            PreferenceDataStoreFactory.create(
                scope = scope,
                produceFile = { File(dir, "test_prefs.preferences_pb") },
            )
        )

    @Test
    fun `onboardingCompleted defaults to false`(@TempDir dir: File) = runTest {
        val repository = repository(backgroundScope, dir)

        assertFalse(repository.onboardingCompleted.first())
    }

    @Test
    fun `setOnboardingCompleted persists and is observable`(@TempDir dir: File) = runTest {
        val repository = repository(backgroundScope, dir)

        repository.setOnboardingCompleted(true)

        assertTrue(repository.onboardingCompleted.first())
    }
}
