package com.soyle.stories.desktop.view.storyevent.rename

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import com.soyle.stories.storyevent.rename.RenameStoryEventPromptPresenter
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.onChange
import tornadofx.viewModelBundle

class `Rename Story Event Prompt Presenter Test` {

    private val renameController = object : RenameStoryEventController {
        var requestedId: StoryEvent.Id? = null
        var requestedName: NonBlankString? = null
        var job: CompletableJob = Job()
        override fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString): Job {
            requestedId = storyEventId
            requestedName = newName
            job = Job()
            return job
        }

        override fun requestToRenameStoryEvent(storyEventId: StoryEvent.Id, currentName: String) =
            fail("Should not be called by presenter")
    }

    private val threadTransformer = object : ThreadTransformer {
        override fun async(task: suspend CoroutineScope.() -> Unit): Job {
            fail("Should not call async from presenter")
        }

        var isGuiThread = false
            private set

        override fun gui(update: suspend CoroutineScope.() -> Unit) {
            isGuiThread = true
            runBlocking { update() }
            isGuiThread = false
        }
    }

    private val providedInputId = StoryEvent.Id()
    private val providedInputName = "Provided Input Name"
    private val presenter =
        RenameStoryEventPromptPresenter(providedInputId, providedInputName, renameController, threadTransformer)

    @Test
    fun `view model initial name should match the provided input name`() {
        assertThat(presenter.viewModel.name.value).isEqualTo("Provided Input Name")
    }

    @Nested
    inner class `Rename` {

        @Test
        fun `should do nothing if view model is not in valid state`() {
            presenter.rename()

            assertThat(renameController.requestedId).isNull()
            assertThat(renameController.requestedName).isNull()
            assertThat(presenter.viewModel.isDisabled.value).isFalse()
        }

        @Nested
        inner class `Given View Model is in Valid State` {

            init {
                presenter.viewModel.nameProperty().value = "Some Name 57"
                assert(presenter.viewModel.isValid.value)
            }

            @BeforeEach
            fun `should update view model on gui thread`() {
                presenter.viewModel.isCompleted.onChange {
                    if (!threadTransformer.isGuiThread) fail("Did not update on gui thread")
                }
                presenter.viewModel.isDisabled.onChange { disabled ->
                    if (!disabled && !threadTransformer.isGuiThread) fail("Did not update on gui thread")
                }
            }

            @Test
            fun `should send view model name value to rename controller`() {
                presenter.rename()

                assertThat(renameController.requestedName?.value)
                    .isNotNull()
                    .isEqualTo("Some Name 57")
            }

            @Test
            fun `should send provided story event id to rename controller`() {
                presenter.rename()

                assertThat(renameController.requestedId)
                    .isNotNull()
                    .isEqualTo(providedInputId)
            }

            @Test
            fun `should disable view model`() {
                presenter.rename()

                assertThat(presenter.viewModel.isDisabled.value)
                    .isTrue()
            }

            @Test
            fun `should enable view model on failure`() {
                presenter.rename()
                renameController.job.completeExceptionally(Error("Intentional Error"))

                assertThat(presenter.viewModel.isDisabled.value)
                    .isFalse()
            }

            @Test
            fun `should complete view model on success`() {
                presenter.rename()
                renameController.job.complete()

                assertThat(presenter.viewModel.isCompleted.value)
                    .isTrue()
            }

        }

    }

    @Nested
    inner class `Cancel` {

        @Test
        fun `should cancel view model`() {
            presenter.cancel()

            assertThat(presenter.viewModel.isCompleted.value).isTrue()
        }

    }

}