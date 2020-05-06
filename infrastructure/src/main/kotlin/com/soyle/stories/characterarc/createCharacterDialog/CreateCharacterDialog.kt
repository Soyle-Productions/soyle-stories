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
import tornadofx.Fragment
import tornadofx.form
import tornadofx.text
import tornadofx.textfield

class CreateCharacterDialog : Fragment("New Character") {

    override val scope: ProjectScope = super.scope as ProjectScope
    val createCharacterDialogViewListener = resolve<CreateCharacterDialogViewListener>()

    private val errorMessage = SimpleStringProperty("")

    override val root = form {
        textfield {
            requestFocus()
            onAction = EventHandler {
                it.consume()
                if (text.isEmpty())
                {
                    errorMessage.set("Cannot create character with a blank name.")
                    return@EventHandler
                }
                async(scope) {
                    createCharacterDialogViewListener.createCharacter(text)
                }
                close()
            }
        }
        text(errorMessage) {
            id = "error-message"
        }
    }

}
fun createCharacterDialog(scope: ProjectScope): CreateCharacterDialog = scope.get<CreateCharacterDialog>().apply {
    openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = scope.get<WorkBench>().currentWindow)?.apply {
        centerOnScreen()
    }
}