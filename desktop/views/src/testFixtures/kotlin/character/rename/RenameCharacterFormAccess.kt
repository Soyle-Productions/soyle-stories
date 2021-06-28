package com.soyle.stories.desktop.view.character.rename

import com.soyle.stories.character.rename.RenameCharacterForm
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot

class RenameCharacterFormAccess(private val form: RenameCharacterForm) : FxRobot() {

    companion object {
        fun <T> RenameCharacterForm.drive(op: RenameCharacterFormAccess.() -> T): T
        {
            val access = RenameCharacterFormAccess(this)
            var result: T? = null
            access.interact { result = access.op() }
            return result as T
        }
    }

    val characterNameInput: TextInputControl
        get() = from(form.root).lookup(".text-field").queryTextInputControl()

}