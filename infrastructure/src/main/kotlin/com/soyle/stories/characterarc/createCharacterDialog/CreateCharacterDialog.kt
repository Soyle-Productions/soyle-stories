package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.common.async
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
    val createCharacterDialogViewListener = resolve<CreateCharacterDialogViewListener>()

    internal var themeId: String? by singleAssign()
    internal var useAsOpponentForCharacterId: String? by singleAssign()

    private val errorMessage = SimpleStringProperty("")

    override val root = form {
        textfield {
            requestFocus()
            onAction = EventHandler {
                it.consume()
                if (text.isEmpty())
                {
                    val errorDecorator = SimpleMessageDecorator("Name cannot be blank", ValidationSeverity.Error)
                    decorators.toList().forEach { removeDecorator(it) }
                    addDecorator(errorDecorator)
                    return@EventHandler
                }
                createCharacterDialogViewListener.createCharacter(text, includeInTheme = themeId, useAsOpponentForCharacter = useAsOpponentForCharacterId)
                close()
            }
        }
    }

}
fun createCharacterDialog(scope: ProjectScope, includeInTheme: String? = null, useAsOpponentForCharacter: String? = null): CreateCharacterDialog = scope.get<CreateCharacterDialog>().apply {
    themeId = includeInTheme
    useAsOpponentForCharacterId = useAsOpponentForCharacter
    openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = scope.get<WorkBench>().currentWindow)?.apply {
        centerOnScreen()
    }
}