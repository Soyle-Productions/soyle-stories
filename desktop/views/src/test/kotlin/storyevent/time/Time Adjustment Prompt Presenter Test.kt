package com.soyle.stories.desktop.view.storyevent.time

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.TimeAdjustmentPromptPresenter
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import com.soyle.stories.storyevent.time.reschedule.RescheduleStoryEventController
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.onChange

class `Time Adjustment Prompt Presenter Test` {

    private val storyEventId = StoryEvent.Id()

    private val threadTransformer = object : ThreadTransformer {

        override fun isGuiThread(): Boolean = _isGuiThread

        var _isGuiThread = true
            private set

        fun falseAsync(block: () -> Unit) {
            _isGuiThread = false
            block()
            _isGuiThread = true
        }

        override fun async(task: suspend CoroutineScope.() -> Unit): Job =
            fail("Should not be called by presenter")

        override fun gui(update: suspend CoroutineScope.() -> Unit) {
            _isGuiThread = true
            runBlocking { update() }
            _isGuiThread = false
        }
    }

    private val rescheduleStoryEventController = object : RescheduleStoryEventController {
        var requestedStoryEvent: StoryEvent.Id? = null
        var requestedTime: Long? = null

        private var job: CompletableJob = Job()

        fun completeJob(failure: Throwable? = null) {
            threadTransformer.falseAsync {
                if (failure != null) job.completeExceptionally(failure)
                else job.complete()
            }
        }

        override fun rescheduleStoryEvent(storyEventId: StoryEvent.Id, time: Long): Job {
            requestedStoryEvent = storyEventId
            requestedTime = time
            job = Job()
            return job
        }

        override fun requestToRescheduleStoryEvent(storyEventId: StoryEvent.Id, currentTime: Long) {
            fail("Should not be called from prompt")
        }
    }

    private val adjustStoryEventsTimeController = object : AdjustStoryEventsTimeController {
        var requestedStoryEventIds: Set<StoryEvent.Id>? = null
        var requestedAmount: Long? = null
        private var job: CompletableJob = Job()

        fun completeJob(failure: Throwable? = null) {
            threadTransformer.falseAsync {
                if (failure != null) job.completeExceptionally(failure)
                else job.complete()
            }
        }

        override fun adjustStoryEventsTime(storyEventIds: Set<StoryEvent.Id>, amount: Long): Job {
            requestedStoryEventIds = storyEventIds
            requestedAmount = amount
            job = Job()
            return job
        }

        override fun requestToAdjustStoryEventsTimes(storyEventIds: Set<StoryEvent.Id>) {
            fail("Should not be called from prompt")
        }
    }

    private fun timeAdjustmentPromptPresenter(storyEventIds: Set<StoryEvent.Id>) =
        TimeAdjustmentPromptPresenter(storyEventIds, rescheduleStoryEventController, adjustStoryEventsTimeController, threadTransformer)
            .apply(::enforceGUIUpdates)

    private fun timeAdjustmentPromptPresenter(time: Long) =
        TimeAdjustmentPromptPresenter(storyEventId, time, rescheduleStoryEventController, adjustStoryEventsTimeController, threadTransformer)
            .apply(::enforceGUIUpdates)

    private fun enforceGUIUpdates(presenter: TimeAdjustmentPromptPresenter) {
        presenter.apply {
            viewModel.submitting.onChange { if (! threadTransformer.isGuiThread()) fail("Not on GUI thread") }
            viewModel.isCompleted.onChange { if (! threadTransformer.isGuiThread()) fail("Not on GUI thread") }
        }
    }

    @Test
    fun `should provide current time to view model`() {
        timeAdjustmentPromptPresenter(9).let {
            assertThat(it.viewModel.time.value).isEqualTo("9")
        }
        timeAdjustmentPromptPresenter(setOf()).let {
            assertThat(it.viewModel.time.value).isEqualTo("")
        }
    }

    @Test
    fun `cannot submit if view model is not in valid state`() {
        val presenter = timeAdjustmentPromptPresenter(setOf())

        presenter.submit()

        assertThat(presenter.viewModel.submitting.value).isFalse
    }

    @Nested
    inner class `Given View Model is in Valid State` {

        @Test
        fun `submit should update view model`() {
            val presenter = timeAdjustmentPromptPresenter(7)

            presenter.viewModel.time.set("5")
            presenter.submit()

            assertThat(presenter.viewModel.submitting.value).isTrue
        }

        @Nested
        inner class `Given Rescheduling a Story Event` {

            val presenter = timeAdjustmentPromptPresenter(7)

            @Test
            fun `submitting should send request to reschedule controller`() {
                presenter.viewModel.time.set("5")
                presenter.submit()

                assertThat(rescheduleStoryEventController.requestedStoryEvent).isEqualTo(storyEventId)
                assertThat(rescheduleStoryEventController.requestedTime).isEqualTo(5L)
            }

            @Test
            fun `if only one story event provided - should still reschedule time`() {
                val presenter = timeAdjustmentPromptPresenter(9)

                presenter.viewModel.time.set("5")
                presenter.submit()

                assertThat(adjustStoryEventsTimeController.requestedStoryEventIds).isNull()
                assertThat(adjustStoryEventsTimeController.requestedAmount).isNull()

                assertThat(rescheduleStoryEventController.requestedStoryEvent).isEqualTo(storyEventId)
                assertThat(rescheduleStoryEventController.requestedTime).isEqualTo(5L)

            }

        }

        @Nested
        inner class `Given Adjust Time of Potentially Many Story Events` {

            private val storyEventIds = List(5) { StoryEvent.Id() }.toSet()
            private val presenter = timeAdjustmentPromptPresenter(storyEventIds)

            @Test
            fun `submitting should send request to adjustment controller`() {
                presenter.viewModel.time.set("5")
                presenter.submit()

                assertThat(adjustStoryEventsTimeController.requestedStoryEventIds).isEqualTo(storyEventIds)
                assertThat(adjustStoryEventsTimeController.requestedAmount).isEqualTo(5L)
            }

            @Test
            fun `if only one story event provided - should still adjust time`() {
                val presenter = timeAdjustmentPromptPresenter(setOf(storyEventId))

                presenter.viewModel.time.set("5")
                presenter.submit()

                assertThat(adjustStoryEventsTimeController.requestedStoryEventIds).isEqualTo(setOf(storyEventId))
                assertThat(adjustStoryEventsTimeController.requestedAmount).isEqualTo(5L)

                assertThat(rescheduleStoryEventController.requestedStoryEvent).isNull()
                assertThat(rescheduleStoryEventController.requestedTime).isNull()

            }

        }

    }

    @Nested
    inner class `Given Submitting` {

        private fun given(presenter: TimeAdjustmentPromptPresenter) {
            presenter.viewModel.time.set("5")
            presenter.submit()
        }

        @Nested
        inner class `Given Rescheduling a Story Event` {

            @Test
            fun `when submission fails - should notify view model`() {
                val presenter = timeAdjustmentPromptPresenter(9)
                given(presenter)

                rescheduleStoryEventController.completeJob(Error("Intentional Error"))

                assertThat(presenter.viewModel.submitting.value).isFalse
                assertThat(presenter.viewModel.isCompleted.value).isFalse
            }

            @Test
            fun `when submission succeeds - should notify view model`() {
                val presenter = timeAdjustmentPromptPresenter(9)
                given(presenter)

                rescheduleStoryEventController.completeJob()

                assertThat(presenter.viewModel.submitting.value).isFalse
                assertThat(presenter.viewModel.isCompleted.value).isTrue
            }

        }

        @Nested
        inner class `Given Adjust Time of Potentially Many Story Events` {

            @Test
            fun `when submission fails - should notify view model`() {
                val presenter = timeAdjustmentPromptPresenter(setOf())
                given(presenter)

                adjustStoryEventsTimeController.completeJob(Error("Intentional Error"))

                assertThat(presenter.viewModel.submitting.value).isFalse
                assertThat(presenter.viewModel.isCompleted.value).isFalse
            }

            @Test
            fun `when submission succeeds - should notify view model`() {
                val presenter = timeAdjustmentPromptPresenter(setOf())
                given(presenter)

                adjustStoryEventsTimeController.completeJob()

                assertThat(presenter.viewModel.submitting.value).isFalse
                assertThat(presenter.viewModel.isCompleted.value).isTrue
            }

        }

    }

    @Test
    fun `cancelling should complete the view model`() {
        val presenter = timeAdjustmentPromptPresenter(setOf())

        presenter.cancel()

        assertThat(presenter.viewModel.isCompleted.value).isTrue
    }


}