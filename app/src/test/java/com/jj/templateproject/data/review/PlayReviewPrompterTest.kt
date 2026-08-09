package com.jj.templateproject.data.review

import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.jj.templateproject.di.ActivityProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PlayReviewPrompterTest {

    private val reviewManager = mockk<ReviewManager>()
    private val activityProvider = mockk<ActivityProvider>()
    private val prompter = PlayReviewPrompter(reviewManager, activityProvider)

    private fun stubRequestOutcome(successful: Boolean, reviewInfo: ReviewInfo? = null) {
        val requestTask = mockk<Task<ReviewInfo>>()
        every { reviewManager.requestReviewFlow() } returns requestTask
        every { requestTask.isSuccessful } returns successful
        every { requestTask.result } returns reviewInfo
        every { requestTask.addOnCompleteListener(any()) } answers {
            firstArg<OnCompleteListener<ReviewInfo>>().onComplete(requestTask)
            requestTask
        }
    }

    @Test
    fun `a successful request launches the review flow when an activity is active`() {
        val reviewInfo = mockk<ReviewInfo>()
        val activity = mockk<Activity>()
        every { activityProvider.activeActivity } returns activity
        every { reviewManager.launchReviewFlow(activity, reviewInfo) } returns mockk(relaxed = true)
        stubRequestOutcome(successful = true, reviewInfo = reviewInfo)

        prompter.requestReview()

        verify { reviewManager.launchReviewFlow(activity, reviewInfo) }
    }

    @Test
    fun `a failed request does not attempt to launch the review flow`() {
        every { activityProvider.activeActivity } returns mockk()
        stubRequestOutcome(successful = false)

        prompter.requestReview()

        verify(exactly = 0) { reviewManager.launchReviewFlow(any(), any()) }
    }

    @Test
    fun `a successful request with no active activity does not attempt to launch the review flow`() {
        every { activityProvider.activeActivity } returns null
        stubRequestOutcome(successful = true, reviewInfo = mockk())

        prompter.requestReview()

        verify(exactly = 0) { reviewManager.launchReviewFlow(any(), any()) }
    }
}
