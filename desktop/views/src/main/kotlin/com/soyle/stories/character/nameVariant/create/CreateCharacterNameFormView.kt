package com.soyle.stories.character.nameVariant.create

import com.soyle.stories.character.nameVariant.addNameVariant.AddCharacterNameVariantController
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import javafx.scene.Parent
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateCharacterNameFormView : Fragment() {

    class InDialog(private val projectScope: ProjectScope) : CreateCharacterNameVariantFlow {

        override fun start(characterId: Character.Id) {
            val form = projectScope.get<CreateCharacterNameFormView>()
            form.characterId = characterId
            form.openModal(
                    StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true,
                    owner = projectScope.get<WorkBench>().currentWindow
                )
        }
    }

    var characterId: Character.Id by singleAssign()

    override val root: Parent = pane {
        textfield {
            action {
                val validName = NonBlankString.create(text) ?: return@action
                decorators.toList().forEach { removeDecorator(it) }
                isDisable = true
                scope.get<AddCharacterNameVariantController>().addCharacterNameVariant(characterId, validName).invokeOnCompletion {
                    if (it == null) close()
                    else {
                        isDisable = false
                        addDecorator(SimpleMessageDecorator(it.localizedMessage, ValidationSeverity.Error))
                    }
                }

            }
        }
    }
}