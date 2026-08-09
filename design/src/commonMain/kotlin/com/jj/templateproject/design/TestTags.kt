package com.jj.templateproject.design

/**
 * Stable `testTag` values for design-system components, so UI tests target nodes by a constant
 * instead of by display text (which changes with copy and locale). Reference these from both the
 * components and the tests that assert on them.
 */
object TestTags {
    const val LOADING_STATE = "loading_state"
    const val ERROR_STATE = "error_state"
    const val ERROR_RETRY = "error_retry"
    const val EMPTY_STATE = "empty_state"
    const val EMPTY_ACTION = "empty_action"
}
