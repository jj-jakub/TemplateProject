package com.jj.templateproject.presentation

import com.jj.templateproject.domain.review.ReviewPromptStore

class FakeReviewPromptStore(
    private var count: Int = 0,
    private var hasPrompted: Boolean = false,
) : ReviewPromptStore {
    override fun readSatisfyingMomentCount() = count
    override fun writeSatisfyingMomentCount(count: Int) {
        this.count = count
    }

    override fun readHasPrompted() = hasPrompted
    override fun writeHasPrompted(hasPrompted: Boolean) {
        this.hasPrompted = hasPrompted
    }
}
