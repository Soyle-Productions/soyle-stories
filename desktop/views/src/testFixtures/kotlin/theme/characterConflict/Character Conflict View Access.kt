package com.soyle.stories.desktop.view.theme.characterConflict

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.theme.characterConflict.CharacterConflict
import com.soyle.stories.theme.characterConflict.CharacterConflictModel
import javafx.scene.Parent
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot
import tornadofx.CssRule
import tornadofx.Field
import tornadofx.Stylesheet

class `Character Conflict View Access`(val view: CharacterConflict) : NodeAccess<Parent>(view.root) {
    companion object {
        fun CharacterConflict.access(): `Character Conflict View Access` = `Character Conflict View Access`(this)
        fun <T> CharacterConflict.drive(op: `Character Conflict View Access`.() -> T): T
        {
            var result: Result<T>? = null
            val access = `Character Conflict View Access`(this)
            access.interact {
                result = kotlin.runCatching { access.op() }
            }
            return result!!.getOrThrow()
        }
    }

    fun isFocusedOn(characterId: Character.Id): Boolean
    {
        return view.scope.get<CharacterConflictModel>().selectedPerspectiveCharacter.value?.characterId == characterId.uuid.toString()
    }

    val perspectiveCharacterSelection: MenuButton
        get() = from(view.root).lookup("#perspective_character_selection").query()

    fun MenuButton.characterItem(characterId: Character.Id): MenuItem?
    {
        return items.find { it.id == characterId.uuid.toString() }
    }

    private val psychologicalWeaknessField: Field? by temporaryChild<Field>(CssRule.id("psychological-weakness-field"))
    val psychologicalWeaknessInput: TextInputControl? by psychologicalWeaknessField.temporaryChild(Stylesheet.textInput)

    private val moralWeaknessField: Field? by temporaryChild<Field>(CssRule.id("moral-weakness-field"))
    val moralWeaknessInput: TextInputControl? by moralWeaknessField.temporaryChild(Stylesheet.textInput)

    val addOpponentSelection: MenuButton by mandatoryChild(CssRule.id("add-opponent-selection"))

}