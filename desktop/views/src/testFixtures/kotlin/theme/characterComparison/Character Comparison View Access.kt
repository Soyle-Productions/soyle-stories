package com.soyle.stories.desktop.view.theme.characterComparison

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.domain.character.Character
import com.soyle.stories.theme.characterValueComparison.CharacterValueComparison
import com.soyle.stories.theme.characterValueComparison.components.CharacterCard
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import tornadofx.CssRule
import tornadofx.uiComponent

class `Character Comparison View Access`(val view: CharacterValueComparison) : NodeAccess<Parent>(view.root) {
    companion object {
        fun CharacterValueComparison.access() = `Character Comparison View Access`(this)
        fun CharacterValueComparison.access(op: `Character Comparison View Access`.() -> Unit) = `Character Comparison View Access`(this).op()
        fun <T> CharacterValueComparison.drive(op: `Character Comparison View Access`.() -> T): T {
            var result: Result<T>? = null
            access {
                interact {
                    result = kotlin.runCatching { op() }
                }
            }
            return result!!.getOrThrow()
        }
    }

    val addCharacterSelection: MenuButton by mandatoryChild(CssRule.id("add-character-button"))

    val MenuButton.createCharacterItem: MenuItem?
        get() = items.find { it.id == "create-new-character" }

    fun getAvailableCharacterToAdd(characterId: Character.Id): MenuItem? {
        return addCharacterSelection.items.find { it.id == characterId.uuid.toString() }
    }

    val characterCards: List<CharacterCard>
        get() = from(node).lookup(ComponentsStyles.card.render()).queryAll<Node>().mapNotNull { it.uiComponent() }

    fun getCharacterCard(characterId: Character.Id): CharacterCard? {
        return characterCards.singleOrNull { it.root.id == characterId.uuid.toString() }
    }

}