package com.soyle.stories.desktop.view.character.deleteCharacterDialog

import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogView
import javafx.scene.control.Button
import org.testfx.api.FxRobot

class DeleteCharacterDialogDriver (private val dialog: DeleteCharacterDialogView) : FxRobot() {

    val confirmButton: Button
        get() = from(dialog.root).lookup(".button").queryAll<Button>().single { it.isDefaultButton }

}