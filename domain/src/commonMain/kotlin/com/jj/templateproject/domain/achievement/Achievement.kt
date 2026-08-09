package com.jj.templateproject.domain.achievement

/**
 * A stand-in achievement roster for this template's demo save flow — a branching game replaces
 * these with its own. [id] is the persisted key, stable across renames of the enum constant itself
 * (unlike [name], which would silently orphan an already-unlocked player's progress if the constant
 * were ever renamed).
 */
enum class Achievement(val id: String) {
    FIRST_SAVE("first_save"),
    FIVE_SAVES("five_saves"),
}
