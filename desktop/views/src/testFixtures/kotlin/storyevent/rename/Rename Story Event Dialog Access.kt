package com.soyle.stories.desktop.view.storyevent.rename

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.desktop.view.storyevent.rename.`Rename Story Event Dialog Access`.Companion.access
import com.soyle.stories.storyevent.create.CreateStoryEventForm
import com.soyle.stories.storyevent.rename.RenameStoryEventForm
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import javafx.scene.control.TextInputControl
import tornadofx.Stylesheet
import tornadofx.hasClass

class `Rename Story Event Dialog Access` private constructor(private val view: RenameStoryEventForm) : NodeAccess<Node>(view.root) {
    companion object {
        fun RenameStoryEventForm.access(): `Rename Story Event Dialog Access` {
            return `Rename Story Event Dialog Access`(this)
        }
        fun RenameStoryEventForm.drive(op:  `Rename Story Event Dialog Access`.() -> Unit) {
            val access = `Rename Story Event Dialog Access`(this)
            access.interact { access.op() }
        }
    }

    val submitButton: Button by mandatoryChild(Stylesheet.button) { it.hasClass(Stylesheet.default) }
    val cancelButton: Button by mandatoryChild(Stylesheet.button) { it.hasClass(Stylesheet.cancel) }
    val nameInput: TextInputControl by mandatoryChild(Stylesheet.textInput)
}