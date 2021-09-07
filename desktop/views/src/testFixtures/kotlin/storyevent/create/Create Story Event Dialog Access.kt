package com.soyle.stories.desktop.view.storyevent.create

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.storyevent.create.CreateStoryEventPromptView
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Spinner
import javafx.scene.control.TextInputControl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.runBlocking
import tornadofx.CssRule
import tornadofx.Stylesheet
import tornadofx.cssid
import tornadofx.hasClass

class `Create Story Event Dialog Access` private constructor(private val view: CreateStoryEventPromptView) : NodeAccess<Node>(view.root) {
    companion object {
        fun CreateStoryEventPromptView.access(): `Create Story Event Dialog Access` {
            return `Create Story Event Dialog Access`(this)
        }
        fun CreateStoryEventPromptView.drive(op: `Create Story Event Dialog Access`.() -> Unit) {
            val access = `Create Story Event Dialog Access`(this)
            access.interact { access.op() }
        }
    }

    val submitButton: Button by mandatoryChild(Stylesheet.button) { it.hasClass(Stylesheet.default) }
    val cancelButton: Button by mandatoryChild(Stylesheet.button) { it.hasClass(Stylesheet.cancel) }
    val nameInput: TextInputControl by mandatoryChild(CssRule.id("name"))
    val timeInput: Spinner<Long?>? by temporaryChild(Stylesheet.spinner)
}