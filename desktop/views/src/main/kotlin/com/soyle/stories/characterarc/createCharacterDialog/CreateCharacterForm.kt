package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterNotifier
import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.character.create.characterNameInput
import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.soylestories.ApplicationScope
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateCharacterForm : Fragment("New Character") {

    class Dialog(val scope: ProjectScope) : CreateCharacterDialog {
        override fun create(onCharacterCreated: (CreatedCharacter) -> Unit) {
            val form = scope.get<CreateCharacterForm>()
            form.onCharacterCreated = onCharacterCreated

            form.themeId = null
            form.includeAsMajorCharacter = false
            form.useAsOpponentForCharacterId = null
            form.openModal(
                StageStyle.UTILITY,
                Modality.APPLICATION_MODAL,
                escapeClosesWindow = true,
                owner = scope.get<WorkBench>().currentWindow
            )?.apply {
                centerOnScreen()
            }
        }
    }

    override val scope: ProjectScope = super.scope as ProjectScope
    val viewListener = resolve<CreateCharacterDialogViewListener>()
    private val createdCharacterNotifier = resolve<CreatedCharacterNotifier>()

    internal var themeId: String? by singleAssign()
    internal var includeAsMajorCharacter: Boolean by singleAssign()
    internal var useAsOpponentForCharacterId: String? by singleAssign()
    internal var onCharacterCreated: (CreatedCharacter) -> Unit by singleAssign()

    private val createdCharacterReceiver: CreatedCharacterReceiver = object : CreatedCharacterReceiver {
        override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
            scope.applicationScope.get<ThreadTransformer>().gui {
                onCharacterCreated(createdCharacter)
            }
            createdCharacterNotifier.removeListener(this)
        }
    }

    override val root = form {
        characterNameInput { newName ->
            val themeId = themeId
            val includeAsMajorCharacter = includeAsMajorCharacter
            val useAsOpponentForCharacterId = useAsOpponentForCharacterId
            createdCharacterNotifier.addListener(createdCharacterReceiver)
            if (themeId != null) {
                when {
                    useAsOpponentForCharacterId != null -> viewListener.createCharacterForUseAsOpponent(
                        newName,
                        themeId,
                        useAsOpponentForCharacterId
                    )
                    includeAsMajorCharacter -> viewListener.createCharacterAsMajorCharacter(newName, themeId)
                    else -> viewListener.createCharacterAndIncludeInTheme(newName, themeId)
                }
            } else {
                viewListener.createCharacter(newName)
            }
            close()
        }.apply {
            requestFocus()
        }
    }

}

fun createCharacterDialog(
    scope: ProjectScope,
    includeInTheme: String? = null,
    includeAsMajorCharacter: Boolean = false,
    useAsOpponentForCharacter: String? = null,
    onCharacterCreated: (CreatedCharacter) -> Unit = {}
): CreateCharacterForm = scope.get<CreateCharacterForm>().apply {
    themeId = includeInTheme
    this.includeAsMajorCharacter = includeAsMajorCharacter
    useAsOpponentForCharacterId = useAsOpponentForCharacter
    this.onCharacterCreated = onCharacterCreated
    openModal(
        StageStyle.UTILITY,
        Modality.APPLICATION_MODAL,
        escapeClosesWindow = true,
        owner = scope.get<WorkBench>().currentWindow
    )?.apply {
        centerOnScreen()
    }
}