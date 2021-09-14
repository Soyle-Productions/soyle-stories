package com.soyle.stories.desktop.view.storyevent.remove

import com.soyle.stories.desktop.view.common.ThreadTransformerDouble
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptPresenter
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptViewModel
import com.soyle.stories.storyevent.remove.RemoveStoryEventController
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.getDialogPreference.GetDialogPreferenceController
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.assertions.api.Assertions.assertThat

class `Remove Story Event Confirmation Prompt Presenter Test` {

    private val getDialogPreferences = object : GetDialogPreferenceController {
        var requestedType: DialogType? = null
        var output: GetDialogPreferences.OutputPort? = null

        override fun getPreferenceForDialog(type: DialogType, outputPort: GetDialogPreferences.OutputPort) {
            requestedType = type
            output = outputPort
        }
    }

    private val removeStoryEventController = object : RemoveStoryEventController {
        var confirmedStoryEventIds: Set<StoryEvent.Id>? = null
        var shouldShowAgain: Boolean? = null
        var job: CompletableJob = Job()
        override fun confirmRemoveStoryEvent(storyEventIds: Set<StoryEvent.Id>, shouldShowAgain: Boolean): Job {
            confirmedStoryEventIds = storyEventIds
            this.shouldShowAgain = shouldShowAgain
            job = Job()
            return job
        }

        override fun removeStoryEvent(storyEventIds: Set<StoryEvent.Id>) =
            fail("Should not be called by controller")
    }

    private val viewModel = RemoveStoryEventConfirmationPromptViewModel(ThreadTransformerDouble())

    private val storyEventIds = List(6) { StoryEvent.Id() }.toSet()

    private val presenter = RemoveStoryEventConfirmationPromptPresenter(storyEventIds, viewModel, getDialogPreferences, removeStoryEventController)

    @Test
    fun `presenter should request preferences`() {
        assertThat(getDialogPreferences.requestedType).isEqualTo(DialogType.DeleteStoryEvent)
        getDialogPreferences.output!!
    }

    @Nested
    inner class `Given Preferences Requested` {

        @Test
        fun `when get preference fails - should show`() {
            getDialogPreferences.output!!.failedToGetDialogPreferences(Exception("Intended Error"))

            assert(viewModel.isShowing)
        }

        @Test
        fun `when get preference is successful - should show`() {
            getDialogPreferences.output!!.gotDialogPreferences(DialogPreference(DialogType.DeleteStoryEvent, true))

            assert(viewModel.isShowing)
        }

        @Nested
        inner class `Given Get Preference is Successful and Does Not Want to Show` {

            init {
                getDialogPreferences.output!!.gotDialogPreferences(DialogPreference(DialogType.DeleteStoryEvent, false))
            }

            @Test
            fun `should not show`() {
                assertThat(viewModel.isShowing).isFalse
                assertThat(viewModel.isConfirming).isTrue
                assertThat(viewModel.shouldNotShowAgain).isTrue
            }

            @Test
            fun `should immediately call the remove controller`() {
                assertThat(removeStoryEventController.confirmedStoryEventIds).isEqualTo(storyEventIds)
                assertThat(removeStoryEventController.shouldShowAgain).isEqualTo(false)
            }

            @Nested
            inner class `Given Confirmation Succeeded` {

                init {
                    removeStoryEventController.job.complete()
                }

                @Test
                fun `should complete view model`() {
                    assert(viewModel.isCompleted)
                }

            }

            @Nested
            inner class `Given Confirmation Failed` {

                init {
                    removeStoryEventController.job.completeExceptionally(Exception("Intended Failure"))
                }

                @Test
                fun `should show view model`() {
                    assert(viewModel.isShowing)
                }

            }

        }

    }

    @Nested
    inner class `Presenter Can be Confirmed` {

        @Test
        fun `given view model cannot be confirmed - should not call controller`() {
            presenter.confirm()

            assertThat(removeStoryEventController.confirmedStoryEventIds).isNull()
        }

        @Nested
        inner class `Given View Model can be Confirmed` {

            init {
                viewModel.needed()
            }

            @Test
            fun `should update viewModel`() {
                presenter.confirm()

                assert(viewModel.isConfirming)
            }

            @Test
            fun `should call removal controller`() {
                presenter.confirm()

                assertThat(removeStoryEventController.confirmedStoryEventIds).isEqualTo(storyEventIds)
            }

            @ParameterizedTest
            @ValueSource(booleans = [true, false])
            fun `should pass show again option to controller`(shouldShow: Boolean) {
                viewModel.shouldNotShowAgain = !shouldShow

                presenter.confirm()

                assertThat(removeStoryEventController.shouldShowAgain).isEqualTo(shouldShow)

            }

        }

        @Nested
        inner class `Given Confirming` {

            init {
                viewModel.needed()
                presenter.confirm()
            }

            @Test
            fun `when job fails - should fail view model`() {
                removeStoryEventController.job.completeExceptionally(Exception("Intended Failure"))

                assertThat(viewModel.isShowing).isTrue
                assertThat(viewModel.isConfirming).isFalse
                assertThat(viewModel.canConfirm).isTrue
                assertThat(viewModel.isCompleted).isFalse
            }

            @Test
            fun `when job succeeds - should complete view model`() {
                removeStoryEventController.job.complete()

                assertThat(viewModel.isShowing).isFalse
                assertThat(viewModel.isConfirming).isFalse
                assertThat(viewModel.canConfirm).isFalse
                assertThat(viewModel.isCompleted).isTrue
            }

        }

    }

    @Nested
    inner class `Presenter Can be Cancelled` {

        @Test
        fun `given view model is showing - should cancel view model`() {
            viewModel.needed()

            presenter.cancel()

            assertThat(viewModel.isShowing).isFalse
            assertThat(viewModel.isConfirming).isFalse
            assertThat(viewModel.canConfirm).isFalse
            assertThat(viewModel.isCompleted).isTrue
        }

    }

}