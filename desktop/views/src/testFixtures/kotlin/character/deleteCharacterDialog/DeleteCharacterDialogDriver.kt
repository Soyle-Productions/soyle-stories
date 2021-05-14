package com.soyle.stories.desktop.view.character.deleteCharacterDialog

import com.soyle.stories.character.delete.DeleteCharacterForm
import com.soyle.stories.characterarc.deleteCharacterDialog.DeleteCharacterDialogView
import javafx.scene.control.Button
import org.testfx.api.FxRobot

class DeleteCharacterFormAccess(private val form: DeleteCharacterForm) : FxRobot() {

    companion object {
        fun DeleteCharacterForm.access() = DeleteCharacterFormAccess(this)
        fun <T> DeleteCharacterForm.drive(op: DeleteCharacterFormAccess.() -> T): T
        {
            var result: T? = null
            val access = DeleteCharacterFormAccess(this)
            access.interact { result = access.op() }
            return result as T
        }
    }

    val confirmButton: Button
        get() = from(form.root).lookup(".button").queryAll<Button>().single { it.isDefaultButton }

}