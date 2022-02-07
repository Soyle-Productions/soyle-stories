package com.soyle.stories.character.rename

import com.soyle.stories.character.create.characterNameInput
import com.soyle.stories.character.renameCharacter.RenameCharacterController
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class RenameCharacterForm : View("Rename") {

    class InDialog(private val projectScope: ProjectScope) : RenameCharacterFlow {

        override fun start(
            characterId: Character.Id,
            currentName: NonBlankString
        ) {
            val scope = RenamingCharacterScope(
                characterId,
                currentName,
                projectScope
            )
            setInScope(RenameCharacterViewModel(characterId, currentName.value), scope, RenameCharacterViewModel::class)
            val form = scope.get<RenameCharacterForm>()

            form.openModal(
                StageStyle.UTILITY,
                Modality.APPLICATION_MODAL,
                escapeClosesWindow = true,
                owner = projectScope.get<WorkBench>().currentWindow
            )?.apply {
                centerOnScreen()
                setOnHidden {
                    scope.deregister()
                    DI.deregister(scope)
                }
            }
        }
    }

    override val scope: RenamingCharacterScope = super.scope as RenamingCharacterScope

    private val viewModel: RenameCharacterViewModel = resolve()

    override val root: Parent = form {
        characterNameInput(initialValue = viewModel.currentName.value, onValid = ::renameCharacter).apply {
            disableWhen(viewModel.locked)
        }
    }

    private fun renameCharacter(newName: NonBlankString) {
        viewModel.locked.set(true)
        val renameCharacterController: RenameCharacterController = scope.projectScope.get()
        renameCharacterController
            .renameCharacter(viewModel.characterId, scope.currentName, newName)
            .invokeOnCompletion { failure ->
                if (failure == null) runLater { close() }
                else runLater { viewModel.locked.set(false) }
            }

    }

}