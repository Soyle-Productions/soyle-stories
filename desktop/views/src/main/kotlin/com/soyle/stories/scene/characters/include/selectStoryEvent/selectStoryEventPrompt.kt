package com.soyle.stories.scene.characters.include.selectStoryEvent

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.applyNothing
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.scopedListener
import com.soyle.stories.scene.charactersInScene.includeCharacterInScene.SelectStoryEventPrompt
import com.soyle.stories.usecase.storyevent.StoryEventItem
import impl.org.controlsfx.skin.AutoCompletePopup
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonBar.setButtonData
import javafx.scene.control.ContextMenu
import javafx.scene.layout.Priority
import javafx.stage.Window
import javafx.stage.WindowEvent
import javafx.util.StringConverter
import tornadofx.*

@ViewBuilder
fun Node.selectStoryEventPrompt(
    viewModel: SelectStoryEventPromptViewModel = SelectStoryEventPromptViewModel(),
    configure: Window.() -> Unit = Window::applyNothing
): SelectStoryEventPrompt {
    val owner = this

    val view = find<SelectStoryEventPromptView>(params = mapOf(SelectStoryEventPromptView::viewModel to viewModel))

    view.apply {
        scopedListener(viewModel.isNeeded()) {
            if (it == true) openModal(owner = owner.scene?.window, escapeClosesWindow = true)?.apply {
                addEventHandler(WindowEvent.WINDOW_HIDDEN) { viewModel.cancel() }
                configure()
            }
            else close()
        }
    }
    return viewModel
}