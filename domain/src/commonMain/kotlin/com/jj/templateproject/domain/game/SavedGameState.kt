package com.jj.templateproject.domain.game

import kotlinx.serialization.Serializable

/**
 * A stand-in for whatever a real game would persist between sessions. Deliberately minimal — this
 * template has no gameplay of its own — but shaped like a real save (a numeric score, a fractional
 * progress value, a timestamp) so a branching game can extend it without restructuring the seam
 * around it.
 */
@Serializable
data class SavedGameState(
    val score: Int,
    val progress: Float,
    val savedAtEpochMillis: Long,
)
