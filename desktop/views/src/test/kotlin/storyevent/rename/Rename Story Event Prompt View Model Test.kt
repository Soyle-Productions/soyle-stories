package com.soyle.stories.desktop.view.storyevent.rename

import com.soyle.stories.storyevent.rename.RenameStoryEventPromptViewModel
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.assertions.api.Assertions.assertThat

class `Rename Story Event Prompt View Model Test` {

    private val inputName = "Some Name"
    private val viewModel = RenameStoryEventPromptViewModel(inputName)

    @Test
    fun `name should match input name`() {
        assertThat(viewModel.name.value).isEqualTo(inputName)
    }

    @Test
    fun `should initially be invalid due to matching the provided name`() {
        assertThat(viewModel.isValid.value).isFalse()
    }

    @Test
    fun `cannot disable if invalid`() {
        viewModel.disable()

        assertThat(viewModel.isDisabled.value).isFalse()
        assertThat(viewModel.isEnabled.value).isTrue()
    }

    @Nested
    inner class `Given New Name Has Been Input` {

        @Test
        fun `should be valid if name is different than input name`() {
            viewModel.nameProperty().value = "Different name"

            assertThat(viewModel.isValid.value).isTrue()
        }

        @Test
        fun `should be invalid if new name is blank`() {
            viewModel.nameProperty().value = "  "

            assertThat(viewModel.isValid.value).isFalse()
        }

        @Test
        fun `can be disabled if name is valid`() {
            viewModel.nameProperty().value = "Different name"
            viewModel.disable()

            assertThat(viewModel.isDisabled.value).isTrue()
            assertThat(viewModel.isEnabled.value).isFalse()
        }

        @Nested
        inner class `Given has Been Disabled` {

            init {
                viewModel.nameProperty().value = "Different name"
                viewModel.disable()
            }

            @Test
            fun `can be enabled`() {
                viewModel.enable()

                assertThat(viewModel.isDisabled.value).isFalse()
                assertThat(viewModel.isEnabled.value).isTrue()
            }

            @Test
            fun `if name has been invalidated, disable should not re-enable`() {
                viewModel.nameProperty().value = "  " // blank name
                viewModel.disable() // second call to disable

                assertThat(viewModel.isDisabled.value).isTrue()
                assertThat(viewModel.isEnabled.value).isFalse()
            }

        }

        @Test
        fun `can be completed if name is valid`() {
            viewModel.nameProperty().value = "Different name"
            viewModel.complete()

            assertThat(viewModel.isCompleted.value).isTrue()
        }

    }

    @Test
    fun `can cancel`() {
        viewModel.cancel()

        assertThat(viewModel.isCompleted.value).isTrue()
    }

    @Test
    fun `cannot complete until name is valid`() {
        viewModel.complete()

        assertThat(viewModel.isCompleted.value).isFalse()
    }


}