package com.soyle.stories.desktop.view.storyevent.create

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import javafx.scene.control.TextInputControl
import tornadofx.Stylesheet
import tornadofx.hasClass

class `Create Story Event Dialog Access` private constructor(private val view: CreateStoryEventForm) : NodeAccess<Node>(view.root) {
    companion object {
        fun CreateStoryEventForm.access(): `Create Story Event Dialog Access` {
            return `Create Story Event Dialog Access`(this)
        }
    }

    val submitButton: Button by mandatoryChild(Stylesheet.button) { it.hasClass(Stylesheet.default) }
    val cancelButton: Button by mandatoryChild(Stylesheet.button) { it.hasClass(Stylesheet.cancel) }
    val nameInput: TextInputControl by mandatoryChild(CreateStoryEventForm.Styles.name)
    val timeInput: Spinner<Long?>? by temporaryChild(CreateStoryEventForm.Styles.time)
}