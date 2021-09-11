package com.soyle.stories.storyevent.time

import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.storyevent.NullableLongSpinnerValueFactory
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.Parent
import tornadofx.*
import tornadofx.Stylesheet.Companion.buttonBar
import tornadofx.Stylesheet.Companion.form

class TimeAdjustmentPromptView(
    private val actions: TimeAdjustmentPromptViewActions,
    private val viewModel: TimeAdjustmentPromptViewModel
) : Fragment() {

    init {
        title = "Reschedule Story Event"
    }

    override val root: Parent = vbox {
        addClass(Styles.timeAdjustmentPrompt)
        vbox {
            addClass(form)
            fieldLabel("Time").labelFor = spinner<Long?> {
                id = "time"
                valueFactory = NullableLongSpinnerValueFactory()
                editor.text = viewModel.time.value
                isEditable = true
                viewModel.time.bind(editor.textProperty())
                disableWhen(viewModel.submitting)
                editor.action(actions::submit)
            }
        }
        hbox {
            addClass(buttonBar)
            button("RESCHEDULE") {
                id = "save"
                addClass(primary, filled)
                enableWhen(viewModel.canSubmit)
                action(actions::submit)
            }
            button("CANCEL") {
                addClass(secondary, outlined)
                action(actions::cancel)
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
                form {
                    fillWidth = true
                    padding = box(12.px)
                    spinner {
                        maxWidth = Double.MAX_VALUE.px
                    }
                }
                buttonBar {
                    alignment = Pos.CENTER_RIGHT
                    padding = box(12.px)
                    spacing = 8.px

                }
            }
        }

    }

}