package com.soyle.stories.scene.characters.include.selectStoryEvent

import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.buttons.secondaryButton
import com.soyle.stories.common.components.inputs.*
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.components.text.TextStyles.Companion.sectionTitle
import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.scopedListener
import com.soyle.stories.common.styleImporter
import com.soyle.stories.storyevent.NullableLongSpinnerValueFactory
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ButtonBar
import javafx.scene.layout.Priority
import tornadofx.*

class SelectStoryEventPromptView : Fragment() {

    val viewModel: SelectStoryEventPromptViewModel by param(defaultValue = SelectStoryEventPromptViewModel())

    override val root: Parent = vbox {
        addClass(Styles.selectStoryEventPrompt)
        sectionTitle("Would you like to involve this character in any of the following story events?")
        vbox {
            addClass(Stylesheet.form)
            checkbox("Create New Story Event") {
                id = "create"
                selectedProperty().bindBidirectional(viewModel.shouldCreateNewEvent())
            }
            hbox {
                addClass(Stylesheet.fieldset)
                enableWhen(viewModel.shouldCreateNewEvent())
                existsWhen(viewModel.shouldCreateNewEvent())
                vbox {
                    hgrow = Priority.ALWAYS
                    fieldLabel("Name").labelFor = nonBlankTextField(
                        value = viewModel.newEvent.name(),
                        onValueChange = viewModel.newEvent.name()::set,
                        configure = {
                            id = "name"
                            onAction { viewModel.submit() }
                            disableWhen(viewModel.isSubmitting())
                        }
                    )
                }
                vbox {
                    hgrow = Priority.ALWAYS
                    fieldLabel("Time").labelFor = optionalLongInput(
                        value = viewModel.newEvent.time(),
                        onValueChange = viewModel.newEvent.time()::set,
                        configure = {
                            id = "time"
                            onAction { viewModel.submit() }
                            disableWhen(viewModel.isSubmitting())
                        }
                    )
                }
            }
        }
        scrollpane(
            fitToWidth = true
        ) {
            vgrow = Priority.ALWAYS
            content = vbox {
                id = "story-event-items"
                scopedListener(viewModel.items) { storyEventItems ->
                    if (storyEventItems == null) children.clear()
                    else storyEventItems.forEach {
                        checkbox(it.storyEventName) {
                            addClass("story-event-item")
                            selectedProperty().bindBidirectional(viewModel.isSelected(it))
                        }
                    }
                }
            }
        }
        buttonbar {
            button("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE) {
                addClass(secondary, outlined)
                action(viewModel::cancel)
            }
            button("OK", ButtonBar.ButtonData.OK_DONE) {
                id = "done"
                addClass(primary, filled)
                action(viewModel::submit)
            }
        }
    }

    class Styles : Stylesheet() {

        companion object {

            val selectStoryEventPrompt by cssclass()

            init {
                styleImporter<Styles>()
            }
        }

        init {
            selectStoryEventPrompt {
                fillWidth = true
                padding = box(24.px)
                spacing = 24.px

                form {
                    spacing = 16.px
                    fieldset {
                        spacing = 16.px
                    }
                }

                buttonBar {
                    alignment = Pos.CENTER_RIGHT
                    spacing = 8.px
                }
            }
        }

    }

}