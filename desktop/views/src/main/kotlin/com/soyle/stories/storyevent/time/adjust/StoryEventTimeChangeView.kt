package com.soyle.stories.storyevent.time.adjust

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.storyevent.NullableLongSpinnerValueFactory
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import tornadofx.*
import tornadofx.spinner

class StoryEventTimeChangeView(
    private val viewModel: StoryEventTimeChangeViewModel
) : Fragment() {

    init {
        title = "Reschedule Story Event"
    }

    override val root: Parent = vbox {
        addClass(Styles.timeAdjustmentPrompt)
        vbox {
            addClass(Stylesheet.form)
            fieldLabel("Time").labelFor = spinner<Long?> {
                id = "time"
                valueFactory = NullableLongSpinnerValueFactory()
                isEditable = true
                viewModel.adjustment().bindBidirectional(editor.textProperty())
                disableWhen(viewModel.submitting())
                editor.action(viewModel::submit)
            }
        }
        hbox {
            addClass(Stylesheet.buttonBar)
            button("RESCHEDULE") {
                id = "save"
                addClass(ComponentsStyles.primary, ComponentsStyles.filled)
                enableWhen(viewModel.canSubmit())
                action(viewModel::submit)
            }
            button("CANCEL") {
                addClass(ComponentsStyles.secondary, ComponentsStyles.outlined)
                action { close() }
            }
        }
    }

    init {
        root.properties[UI_COMPONENT_PROPERTY] = this
    }

    class Styles : Stylesheet() {

        companion object {

            val timeAdjustmentPrompt by cssclass()

            init {
                if (Platform.isFxApplicationThread()) importStylesheet<Styles>()
                else runLater { importStylesheet<Styles>() }
            }

        }

        init {
            timeAdjustmentPrompt {
                fillWidth = true
                Stylesheet.form {
                    fillWidth = true
                    padding = box(12.px)
                    Stylesheet.spinner {
                        maxWidth = Double.MAX_VALUE.px
                    }
                }
                Stylesheet.buttonBar {
                    alignment = Pos.CENTER_RIGHT
                    padding = box(12.px)
                    spacing = 8.px

                }
            }
        }

    }

}