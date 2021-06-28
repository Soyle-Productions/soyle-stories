package com.soyle.stories.desktop.view.character.arc.create

import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialog
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot

class `Create Character Arc View Access`(val view: PlanCharacterArcDialog) : FxRobot() {

    companion object {
        fun <T> PlanCharacterArcDialog.drive(op: `Create Character Arc View Access`.() -> T): T
        {
            var result: T? = null
            val access = `Create Character Arc View Access`(this)
            access.interact { result = access.op() }
            return result as T
        }
    }

    val nameInput: TextInputControl
        get() = from(view.root).lookup(".text-field").queryTextInputControl()

}