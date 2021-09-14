package com.soyle.stories.desktop.view.storyevent.remove

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.desktop.view.common.ThreadTransformerDouble
import com.soyle.stories.storyevent.remove.RemoveStoryEventConfirmationPromptViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.onChange

class `Remove Story Event Confirmation Prompt ViewModel Test` {

    @Nested
    inner class `When First Created` {

        val viewModel = RemoveStoryEventConfirmationPromptViewModel(threadTransformer)

        @Test
        fun `should not yet be showing`() {
            assertThat(viewModel.isShowing).isFalse
        }

        @Test
        fun `should not yet be completed`() {
            assertThat(viewModel.isCompleted).isFalse
        }

        @Test
        fun `should not yet be confirming`() {
            assertThat(viewModel.isConfirming).isFalse
        }

    }

    private val threadTransformer = ThreadTransformerDouble()
    val viewModel = RemoveStoryEventConfirmationPromptViewModel(threadTransformer).apply {
        showing().onChange { if (! threadTransformer.isGuiThread()) fail("showing property changed outside gui thread") }
        completed().onChange { if (! threadTransformer.isGuiThread()) fail("showing property changed outside gui thread") }
        canConfirm().onChange { if (! threadTransformer.isGuiThread()) fail("showing property changed outside gui thread") }
    }

    @Nested
    inner class `Prompt Can be Unneeded` {

        @Test
        fun `should be confirming`() {
            viewModel.unneeded()

            assertThat(viewModel.isConfirming).isTrue
            assertThat(viewModel.canConfirm).isFalse
            assertThat(viewModel.isCompleted).isFalse
            assertThat(viewModel.shouldNotShowAgain).isTrue
        }

    }

    @Nested
    inner class `Prompt Can be Needed` {

        @Test
        fun `should be showing`() {
            viewModel.needed()

            assertThat(viewModel.isShowing).isTrue
            assertThat(viewModel.shouldNotShowAgain).isFalse
        }

        @Test
        fun `should be able to confirm`() {
            viewModel.needed()

            assertThat(viewModel.canConfirm).isTrue
        }

        @Test
        fun `can detect when shown`() {
            var detected = false
            viewModel.showing().onChange { detected = true }

            viewModel.needed()

            assert(detected)
        }

    }

    @Nested
    inner class `Prompt Can be Cancelled` {

        @Nested
        inner class `Given is Needed` {

            init {
                viewModel.needed()
            }

            @Test
            fun `should be completed`() {
                viewModel.cancel()

                assert(viewModel.isCompleted)
            }

            @Test
            fun `should not be showing`() {
                viewModel.cancel()

                assertThat(viewModel.isShowing).isFalse
            }
        }

        @Test
        fun `given not needed - should not be completed`() {
            viewModel.cancel()

            assertThat(viewModel.isCompleted).isFalse
        }

    }

    @Nested
    inner class `Prompt Can be Confirmed` {

        @Nested
        inner class `Given is Needed` {

            init {
                viewModel.needed()
            }

            @Test
            fun `should be confirming`() {
                viewModel.confirm()

                assert(viewModel.isConfirming)
            }

            @Test
            fun `should not be able to confirm`() {
                viewModel.confirm()

                assertThat(viewModel.canConfirm).isFalse
            }

            @Test
            fun `should be able to detect when can confirm changes`() {
                var detected = false
                viewModel.canConfirm().onChange { detected = true }

                viewModel.confirm()

                assert(detected)
            }


        }

        @Test
        fun `given not needed - should not be confirming`() {
            viewModel.confirm()

            assertThat(viewModel.isConfirming).isFalse
        }

    }

    @Nested
    inner class `Confirmation Can Fail` {

        @Nested
        inner class `Given is Confirming` {

            init {
                viewModel.needed()
                viewModel.confirm()
            }

            @Test
            fun `should not be confirming`() {
                viewModel.failed()

                assertThat(viewModel.isConfirming).isFalse
            }

            @Test
            fun `should be able to confirm`() {
                viewModel.failed()

                assert(viewModel.canConfirm)
            }

        }

    }

    @Nested
    inner class `Confirmation Can be Completed` {

        @Nested
        inner class `Given is Confirming` {

            init {
                viewModel.needed()
                viewModel.confirm()
            }

            @Test
            fun `should be completed`() {
                viewModel.complete()

                assert(viewModel.isCompleted)
            }

            @Test
            fun `can detect when completed`() {
                var detected = false
                viewModel.completed().onChange { detected = true }

                viewModel.complete()

                assertThat(detected).isTrue
            }

            @Test
            fun `should not be showing`() {
                viewModel.complete()

                assertThat(viewModel.isShowing).isFalse
            }

        }

        @Test
        fun `given not confirming - should not be completed`() {
            viewModel.complete()

            assertThat(viewModel.isCompleted).isFalse
        }


    }

}