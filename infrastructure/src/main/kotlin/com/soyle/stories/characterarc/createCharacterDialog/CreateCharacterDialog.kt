package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterNotifier
import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.StageStyle
import tornadofx.*

class CreateCharacterDialog : Fragment("New Character") {

    override val scope: ProjectScope = super.scope as ProjectScope
    val viewListener = resolve<CreateCharacterDialogViewListener>()
    private val createdCharacterNotifier = resolve<CreatedCharacterNotifier>()

    internal var themeId: String? by singleAssign()
    internal var includeAsMajorCharacter: Boolean by singleAssign()
    internal var useAsOpponentForCharacterId: String? by singleAssign()
    internal var onCharacterCreated: (CreatedCharacter) -> Unit by singleAssign()

    private val createdCharacterReceiver: CreatedCharacterReceiver = object : CreatedCharacterReceiver {
        override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
            onCharacterCreated(createdCharacter)
            createdCharacterNotifier.removeListener(this)
        }
    }

    private val errorMessage = SimpleStringProperty("")

    override val root = form {
        textfield {
            requestFocus()
            onAction = EventHandler {
                it.consume()
                val name = NonBlankString.create(text)
                if (name == null) {
                    val errorDecorator = SimpleMessageDecorator("Name cannot be blank", ValidationSeverity.Error)
                    decorators.toList().forEach { removeDecorator(it) }
                    addDecorator(errorDecorator)
                    return@EventHandler
                }
                val themeId = themeId
                val includeAsMajorCharacter = includeAsMajorCharacter
                val useAsOpponentForCharacterId = useAsOpponentForCharacterId
                createdCharacterNotifier.addListener(createdCharacterReceiver)
                if (themeId != null) {
                    when {
                        useAsOpponentForCharacterId != null -> viewListener.createCharacterForUseAsOpponent(
                            name,
                            themeId,
                            useAsOpponentForCharacterId
                        )
                        includeAsMajorCharacter -> viewListener.createCharacterAsMajorCharacter(name, themeId)
                        else -> viewListener.createCharacterAndIncludeInTheme(name, themeId)
                    }
                } else {
                    viewListener.createCharacter(name)
                }
                close()
            }
        }
    }

}

fun createCharacterDialog(
    scope: ProjectScope,
    includeInTheme: String? = null,
    includeAsMajorCharacter: Boolean = false,
    useAsOpponentForCharacter: String? = null,
    onCharacterCreated: (CreatedCharacter) -> Unit = {}
): CreateCharacterDialog = scope.get<CreateCharacterDialog>().apply {
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