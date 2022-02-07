package com.soyle.stories.character.create

import javafx.scene.Parent
import tornadofx.Fragment
import tornadofx.disableWhen

class CreateCharacterPromptView : Fragment("Create New Character") {

    val viewModel = CreateCharacterPromptViewModel()

    override val root: Parent = characterNameInput(
        viewModel.name(),
        viewModel::create
    )

}