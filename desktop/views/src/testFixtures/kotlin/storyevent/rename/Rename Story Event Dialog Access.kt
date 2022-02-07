package com.soyle.stories.desktop.view.storyevent.rename

import com.soyle.stories.common.components.dataDisplay.decorator.singleDecorator
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.storyevent.rename.RenameStoryEventPromptView
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Labeled
import javafx.scene.control.TextInputControl
import tornadofx.Stylesheet
import tornadofx.hasClass

class `Rename Story Event Dialog Access` private constructor(private val view: RenameStoryEventPromptView) : NodeAccess<Node>(view.root) {
    companion object {
        fun RenameStoryEventPromptView.access(): `Rename Story Event Dialog Access` {
            return `Rename Story Event Dialog Access`(this)
        }
        fun RenameStoryEventPromptView.drive(op:  `Rename Story Event Dialog Access`.() -> Unit) {
            val access = `Rename Story Event Dialog Access`(this)
            access.interact { access.op() }
        }
    }

    val submitButton: Button by mandatoryChild(Stylesheet.button) { it.hasClass(Stylesheet.default) }
    val cancelButton: Button by mandatoryChild(Stylesheet.button) { it.hasClass(Stylesheet.cancel) }
    val nameInput: TextInputControl by mandatoryChild(Stylesheet.textInput)
    val error
        get() = nameInput.singleDecorator
}