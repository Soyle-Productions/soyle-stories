package com.soyle.stories.character.create

import com.soyle.stories.character.buildNewCharacter.CreateCharacterPrompt
import com.soyle.stories.common.scopedListener
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import javafx.scene.Parent
import tornadofx.FX
import tornadofx.Scope
import tornadofx.View
import tornadofx.pane

class CreateCharacterDialog : View() {

    private val prompt = scope.get<CreateCharacterPromptView>()

    val viewModel
        get() = prompt.viewModel

    override val root: Parent = pane { add(prompt) }

    init {
        scopedListener(prompt.viewModel.isOpen()) {
            if (it == true) openModal(escapeClosesWindow = true)?.apply {
                setOnHidden { viewModel.cancel() }
            }
            else close()
        }
        titleProperty.bind(prompt.titleProperty)
    }

}

fun createCharacterPrompt(
    scope: Scope = FX.defaultScope
): CreateCharacterPromptViewModel {
    val view = scope.get<CreateCharacterDialog>()
    return view.viewModel
}