package com.soyle.stories.characterarc.createCharacterDialog

import com.soyle.stories.characterarc.characterList.CharacterListViewListener
import com.soyle.stories.di.characterarc.CharacterArcModule
import com.soyle.stories.di.resolve
import javafx.event.EventHandler
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.runBlocking
import tornadofx.Component
import tornadofx.Fragment
import tornadofx.form
import tornadofx.textfield

class CreateCharacterDialog : Fragment("New Character") {

    val createCharacterDialogViewListener = resolve<CreateCharacterDialogViewListener>()

    override val root = form {
        textfield {
            requestFocus()
            onAction = EventHandler {
                it.consume()
                if (text.isEmpty())
                {
                    return@EventHandler
                }
                runAsync {
                    runBlocking {
                        createCharacterDialogViewListener.createCharacter(text)
                    }
                }
                close()
            }
        }
    }

}
fun Component.createCharacterDialog(owner: Stage?): CreateCharacterDialog = find {
    openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = owner)?.apply {
        centerOnScreen()
    }
}
