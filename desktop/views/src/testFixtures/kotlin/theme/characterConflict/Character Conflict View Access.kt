package com.soyle.stories.desktop.view.theme.characterConflict

import com.soyle.stories.domain.character.Character
import com.soyle.stories.theme.characterConflict.CharacterConflict
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import org.testfx.api.FxRobot

class `Character Conflict View Access`(val view: CharacterConflict) : FxRobot() {
    companion object {
        fun <T> CharacterConflict.drive(op: `Character Conflict View Access`.() -> T): T
        {
            var result: T? = null
            val access = `Character Conflict View Access`(this)
            access.interact { result = access.op() }
            return result as T
        }
    }

    val perspectiveCharacterSelection: MenuButton
        get() = from(view.root).lookup("#perspective_character_selection").query()

    fun MenuButton.characterItem(characterId: Character.Id): MenuItem?
    {
        return items.find { it.id == characterId.uuid.toString() }
    }
}