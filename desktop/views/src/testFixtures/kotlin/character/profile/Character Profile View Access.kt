package com.soyle.stories.desktop.view.character.profile

import com.soyle.stories.character.profile.CharacterProfileView
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.control.Labeled
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot

class `Character Profile View Access`(val view: CharacterProfileView) : FxRobot() {

    companion object {
        fun CharacterProfileView.access() = `Character Profile View Access`(this)
        fun <T> CharacterProfileView.drive(op: `Character Profile View Access`.() -> T): T
        {
            var result: T? = null
            val access = access()
            access.interact { result = access.op() }
            return result as T
        }
    }

    val createCharacterNameVariantForm: TextInputControl?
        get() = from(view.root).lookup("#create-alt-name-input").queryAll<TextInputControl>().firstOrNull()

    val isCreatingCharacterNameVariant: Boolean
        get() = createCharacterNameVariantForm?.isVisible == true

    val createCharacterNameVariantButton: Button
        get() = from(view.root).lookup("#create-alt-name-button").queryButton()

    internal fun getCharacterAltNameItem(altName: String): Node? =
        from(view.root).lookup(".character-alt-name-item").queryAll<Node>().asSequence()
            .find { item ->
                val label = from(item).lookup(".label").queryAll<Labeled>().filter { it.text == altName }.firstOrNull()
                label != null
            }

    fun isRenamingNameVariant(variant: String): Boolean = altNameRenameField(variant)?.isVisible == true

    fun altNameRenameButton(altName: String): Button? = getCharacterAltNameItem(altName)?.run {
        from(this).lookup(".edit-button").queryAll<Button>().firstOrNull()
    }

    fun altNameDeleteButton(altName: String): Button? = getCharacterAltNameItem(altName)?.run {
        from(this).lookup(".delete-button").queryAll<Button>().firstOrNull()
    }

    fun altNameRenameField(altName: String): TextInputControl? = getCharacterAltNameItem(altName)?.run {
        from(this).lookup(".character-name-input").queryAll<TextInputControl>().firstOrNull()
    }


}