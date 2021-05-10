package com.soyle.stories.character.create

import com.soyle.stories.character.buildNewCharacter.BuildNewCharacterController
import com.soyle.stories.character.buildNewCharacter.CreatedCharacterNotifier
import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.di.get
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.Fragment
import tornadofx.disableWhen
import tornadofx.form
import tornadofx.runLater

class CreateCharacterForm : Fragment("Create New Character"), CreatedCharacterReceiver {

    class InDialog(private val projectScope: ProjectScope) : CreateCharacterFlow {

        override fun start(onCharacterCreated: (CreatedCharacter) -> Unit) {
            val form = projectScope.get<CreateCharacterForm>()
            form.onCharacterCreated = onCharacterCreated
            form.openModal(
                StageStyle.UTILITY, Modality.APPLICATION_MODAL,
                escapeClosesWindow = true,
                owner = projectScope.get<WorkBench>().currentWindow
            )?.apply {
                centerOnScreen()
                form.root.requestFocus()
            }
        }
    }

    private val creatingCharacter = SimpleBooleanProperty(false)
    internal var onCharacterCreated: (CreatedCharacter) -> Unit = {}

    override val root: Parent = characterNameInput(onValid = ::createCharacter).apply {
        disableWhen(creatingCharacter)
    }

    private fun createCharacter(name: NonBlankString) {
        creatingCharacter.set(true)
        val createCharacterController = scope.get<BuildNewCharacterController>()
        val notifier = scope.get<CreatedCharacterNotifier>()
        notifier.addListener(this)
        createCharacterController.createCharacter(name).invokeOnCompletion {
            creatingCharacter.set(false)
            if (it == null) notifier.removeListener(this)
        }
    }

    override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
        runLater {
            if (currentStage?.isShowing == true) {
                onCharacterCreated(createdCharacter)
                close()
            }
        }
    }

}