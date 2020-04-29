package com.soyle.stories.characterarc.planCharacterArcDialog

import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
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

class PlanCharacterArcDialog : Fragment("New Character Arc") {

    val planCharacterArcDialogViewListener = resolve<PlanCharacterArcDialogViewListener>()
    var characterId: String = ""

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
                        planCharacterArcDialogViewListener.planCharacterArc(characterId, text)
                    }
                }
                close()
            }
        }
    }

}
fun Component.planCharacterArcDialog(characterId: String, owner: Stage?): PlanCharacterArcDialog = find {
    this.characterId = characterId
    openModal(StageStyle.UTILITY, Modality.APPLICATION_MODAL, escapeClosesWindow = true, owner = owner)?.apply {
        centerOnScreen()
    }
}
