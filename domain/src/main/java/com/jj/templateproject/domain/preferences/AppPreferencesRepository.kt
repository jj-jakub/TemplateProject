package com.jj.templateproject.domain.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Persistent app preferences, exposed as reactive [Flow]s with suspending setters. The interface
 * lives in `:domain` and is free of any storage technology; the implementation (DataStore) lives
 * in the data layer.
 *
 * Add new preferences here as the app grows (each a `Flow` getter + a suspending setter).
 */
interface AppPreferencesRepository {

    /** Whether the user has completed the first-run/onboarding flow. */
    val onboardingCompleted: Flow<Boolean>

    suspend fun setOnboardingCompleted(completed: Boolean)
}
