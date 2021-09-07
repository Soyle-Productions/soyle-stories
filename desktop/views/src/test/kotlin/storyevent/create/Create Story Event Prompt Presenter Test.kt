package com.soyle.stories.desktop.view.storyevent.create

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.create.CreateStoryEventController
import com.soyle.stories.storyevent.create.CreateStoryEventPromptPresenter
import com.soyle.stories.storyevent.create.CreateStoryEventPromptViewModel
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.onChange

class `Create Story Event Prompt Presenter Test` {

    private val createStoryEventController = object : CreateStoryEventController {
        var requestedName: NonBlankString? = null
        var requestedTimeUnit: Long? = null
        var requestedRelative: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative? = null
        var job: CompletableJob = Job()
            private set

        override fun requestToCreateStoryEvent() {
            TODO("Not yet implemented")
        }

        override fun requestToCreateStoryEvent(relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative) {
            TODO("Not yet implemented")
        }

        override fun createStoryEvent(name: NonBlankString): Job {
            requestedName = name
            job = Job()
            return job
        }

        override fun createStoryEvent(name: NonBlankString, timeUnit: Long): Job {
            requestedName = name
            requestedTimeUnit = timeUnit
            job = Job()
            return job
        }

        override fun createStoryEvent(
            name: NonBlankString,
            relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative
        ): Job {
            requestedName = name
            requestedRelative = relativeTo
            job = Job()
            return job
        }

    }

    private val threadTransformer = object : ThreadTransformer {
        var isInGui = false
            private set

        override fun gui(update: suspend CoroutineScope.() -> Unit) {
            isInGui = true
            runBlocking { update() }
            isInGui = false
        }

        override fun async(task: suspend CoroutineScope.() -> Unit): Job {
            TODO("Not yet implemented")
        }
    }

    private fun createStoryEventPromptPresenter() =
        CreateStoryEventPromptPresenter(null, createStoryEventController, threadTransformer)

    private fun createStoryEventPromptPresenter(relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative) =
        CreateStoryEventPromptPresenter(relativeTo, createStoryEventController, threadTransformer)

    @Nested
    inner class `When Created without Relative Story Event` {

        val presenter = createStoryEventPromptPresenter()

        @Test
        fun `time should not already be specified`() {
            assertThat(presenter.viewModel.timeNotAlreadySpecified.value).isTrue()
        }

        @Nested
        inner class `Name and Valid or Empty Time is Required` {

            @Test
            fun `should be invalid initially`() {
                assertThat(presenter.viewModel.isValid.value).isFalse()
            }

            @Test
            fun `if just name is supplied, should be valid`() {
                presenter.viewModel.name.set("Standard Name")

                assertThat(presenter.viewModel.isValid.value).isTrue()
            }

            @Test
            fun `if name is supplied and an invalid time, should be invalid`() {
                presenter.viewModel.name.set("Standard Name")
                presenter.viewModel.timeText.set("herp")

                assertThat(presenter.viewModel.isValid.value).isFalse()
            }

            @Test
            fun `if name is supplied and an valid time, should be valid`() {
                presenter.viewModel.name.set("Standard Name")
                presenter.viewModel.timeText.set("18")

                assertThat(presenter.viewModel.isValid.value).isTrue()
            }

        }

    }

    @Nested
    inner class `When Created with Relative Story Event` {

        val presenter = createStoryEventPromptPresenter(
            CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(
                StoryEvent.Id(),
                0
            )
        )

        @Test
        fun `time should already be specified`() {
            assertThat(presenter.viewModel.timeNotAlreadySpecified.value).isFalse()
        }

        @Nested
        inner class `Only Name is Required` {

            @Test
            fun `should be invalid initially`() {
                assertThat(presenter.viewModel.isValid.value).isFalse()
            }

            @Test
            fun `if just name is supplied, should be valid`() {
                presenter.viewModel.name.set("Standard Name")

                assertThat(presenter.viewModel.isValid.value).isTrue()
            }

            @Test
            fun `if name is supplied and an invalid time, should be valid`() {
                presenter.viewModel.name.set("Standard Name")
                presenter.viewModel.timeText.set("herp")

                assertThat(presenter.viewModel.isValid.value).isTrue()
            }

            @Test
            fun `if name is supplied and an valid time, should be valid`() {
                presenter.viewModel.name.set("Standard Name")
                presenter.viewModel.timeText.set("18")

                assertThat(presenter.viewModel.isValid.value).isTrue()
            }

        }

    }

    @Nested
    inner class `When Creating` {

        val presenter = createStoryEventPromptPresenter()

        @Test
        fun `should do nothing if invalid`() {
            presenter.viewModel.name.set("")
            presenter.createStoryEvent()

            with(createStoryEventController) {
                assertThat(requestedName).isNull()
                assertThat(requestedTimeUnit).isNull()
                assertThat(requestedRelative).isNull()
            }
            assertThat(presenter.viewModel.isCreating.value).isFalse()
        }

        @Test
        fun `should send just the name if nothing input into time`() {
            presenter.viewModel.name.set("Some name")
            presenter.viewModel.timeText.set("")

            presenter.createStoryEvent()

            with(createStoryEventController) {
                assertThat(requestedName).isEqualTo("Some name")
                assertThat(requestedTimeUnit).isNull()
                assertThat(requestedRelative).isNull()
            }
            assertThat(presenter.viewModel.isCreating.value).isTrue()
            assertThat(presenter.viewModel.isCompleted.value).isFalse()
        }

        @Test
        fun `should send time if valid`() {
            presenter.viewModel.name.set("Some name")
            presenter.viewModel.timeText.set("9")

            presenter.createStoryEvent()

            with(createStoryEventController) {
                assertThat(requestedName).isEqualTo("Some name")
                assertThat(requestedTimeUnit).isEqualTo(9L)
                assertThat(requestedRelative).isNull()
            }
            assertThat(presenter.viewModel.isCreating.value).isTrue()
            assertThat(presenter.viewModel.isCompleted.value).isFalse()
        }

        @Test
        fun `should send relative story event if created with one`() {
            val id = StoryEvent.Id()
            val delta = listOf<Long>(-1, 0, 1).random()
            val presenter = createStoryEventPromptPresenter(
                CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative(
                    id, delta
                )
            )
            presenter.viewModel.name.set("Some name")
            presenter.viewModel.timeText.set("9") // even if time was somehow set to be valid

            presenter.createStoryEvent()

            with(createStoryEventController) {
                assertThat(requestedName).isEqualTo("Some name")
                assertThat(requestedTimeUnit).isNull()
                assertThat(requestedRelative!!.relativeStoryEventId).isEqualTo(id)
                assertThat(requestedRelative!!.delta).isEqualTo(delta)
            }
            assertThat(presenter.viewModel.isCreating.value).isTrue()
            assertThat(presenter.viewModel.isCompleted.value).isFalse()
        }

        @Test
        fun `should not send request if time value is invalid`() {
            presenter.viewModel.name.set("Some name")
            presenter.viewModel.timeText.set("non-viable value")

            presenter.createStoryEvent()

            with(createStoryEventController) {
                assertThat(requestedName).isNull()
                assertThat(requestedTimeUnit).isNull()
                assertThat(requestedRelative).isNull()
                assertThat(requestedRelative).isNull()
            }
            assertThat(presenter.viewModel.isCreating.value).isFalse()
        }

        @Nested
        inner class `When Successful` {

            @Test
            fun `should be in completed state`() {
                presenter.viewModel.name.set("Some name")
                presenter.createStoryEvent()
                createStoryEventController.job.complete()

                assertThat(presenter.viewModel.isCompleted.value).isTrue()
            }

            @Test
            fun `should process on gui thread to avoid thread errors`() {
                presenter.viewModel.name.set("Some name")
                presenter.createStoryEvent()
                presenter.viewModel.isCompleted.onChange {
                    if (!threadTransformer.isInGui) fail("Did not switch to gui thread")
                }
                createStoryEventController.job.complete()
            }

        }

        @Nested
        inner class `When Failed` {

            @Test
            fun `should not be completed`() {
                presenter.viewModel.name.set("Some name")
                presenter.createStoryEvent()
                createStoryEventController.job.completeExceptionally(Error("Intentional Error"))

                assertThat(presenter.viewModel.isCompleted.value).isFalse()
            }

            @Test
            fun `should not be creating`() {
                presenter.viewModel.name.set("Some name")
                presenter.createStoryEvent()
                createStoryEventController.job.completeExceptionally(Error("Intentional Error"))

                assertThat(presenter.viewModel.isCreating.value).isFalse()
            }

            @Test
            fun `should process on gui thread to avoid thread errors`() {
                presenter.viewModel.name.set("Some name")
                presenter.createStoryEvent()
                presenter.viewModel.isCreating.onChange {
                    if (!threadTransformer.isInGui) fail("Did not switch to gui thread")
                }
                createStoryEventController.job.completeExceptionally(Error("Intentional Error"))
            }

        }

    }

    @Nested
    inner class `When Cancelled` {

        @Test
        fun `should be completed`() {
            val presenter = createStoryEventPromptPresenter()
            presenter.cancel()

            assertThat(presenter.viewModel.isCompleted.value).isTrue()
        }

    }

}