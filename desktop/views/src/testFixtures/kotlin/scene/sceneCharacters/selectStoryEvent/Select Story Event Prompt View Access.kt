package com.soyle.stories.desktop.view.scene.sceneCharacters.selectStoryEvent

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.scene.characters.include.selectStoryEvent.SelectStoryEventPromptView
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Labeled
import javafx.scene.control.TextInputControl
import tornadofx.CssRule

class `Select Story Event Prompt View Access`(view: SelectStoryEventPromptView) : NodeAccess<Parent>(view.root) {

    val storyEventItems: List<CheckBox>
        get() = node.findChildren(CssRule.c("story-event-item"))

    fun storyEventItemNamed(name: String): CheckBox? =
        storyEventItems.find { it.text == name }

    val creatingStoryEventToggle: CheckBox by mandatoryChild(CssRule.id("create"))

    val newNameField: TextInputControl by mandatoryChild(CssRule.id("name"))
    val newTimeField: TextInputControl by mandatoryChild(CssRule.id("time"))

    val doneButton by mandatoryChild<Button>(CssRule.id("done"))
}

fun SelectStoryEventPromptView.access() = `Select Story Event Prompt View Access`(this)
fun <T> SelectStoryEventPromptView.drive(op: `Select Story Event Prompt View Access`.() -> T): T {
    val access = access()
    var result: Result<T> = Result.failure(Error("Never received result"))
    access.interact {
        result = runCatching { access.op() }
    }
    return result.getOrThrow()
}