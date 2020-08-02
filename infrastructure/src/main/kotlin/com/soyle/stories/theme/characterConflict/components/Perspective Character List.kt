package com.soyle.stories.theme.characterConflict.components

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel
import com.soyle.stories.theme.characterConflict.CharacterConflictModel
import com.soyle.stories.theme.characterConflict.CharacterConflictViewModel
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.util.Duration
import tornadofx.*

internal fun MenuButton.populatePerspectiveCharacterList(model: CharacterConflictModel) {
    model.availablePerspectiveCharacters.onChange { list ->
        items.clear()
        when {
            list == null -> loading()
            list.isEmpty() -> noAvailableCharacters()
            else -> {
                majorCharacters(list)
                minorCharacters(list)
                //remainingCharacters(list)
            }
        }
    }
    loading()
}

internal var MenuButton.onCreateCharacter: (() -> Unit)?
    get() = properties.get("com.soyle.stories.onCreateCharacter") as? () -> Unit
    set(value) {
        properties["com.soyle.stories.onCreateCharacter"] = value
    }

internal var MenuButton.onPerspectiveCharacterSelected: ((AvailablePerspectiveCharacterViewModel) -> Unit)?
    get() = properties.get("com.soyle.stories.onPerspectiveCharacterSelected") as? (AvailablePerspectiveCharacterViewModel) -> Unit
    set(value) {
        properties["com.soyle.stories.onPerspectiveCharacterSelected"] = value
    }

internal fun MenuButton.loading() {
    items.add(loadingItem)
}

internal val MenuButton.loadingItem: MenuItem
    get() = properties.getOrPut("com.soyle.stories.loadingItem") {
        MenuItem("Loading...").apply {
            isDisable = true
        }
    } as MenuItem

internal fun MenuButton.noAvailableCharacters() {
    item("No available characters") { isDisable = true }
    items.add(createCharacterItem().apply {
        if (hasClass(ComponentsStyles.contextMenuSectionedItem))
            removeClass(ComponentsStyles.contextMenuSectionedItem)
    })
}

internal fun MenuButton.createCharacterItem(): MenuItem =
    properties.getOrPut("com.soyle.stories.createCharacterItem") {
        MenuItem("[Create New Character]").apply {
            action {
                onCreateCharacter?.invoke()
            }
        }
    } as MenuItem

private fun MenuButton.majorCharacters(list: List<AvailablePerspectiveCharacterViewModel>) {
    item("Major Characters") {
        addClass(ComponentsStyles.contextMenuSectionHeaderItem)
        isDisable = true
    }
    list.filter { it.isMajorCharacter }.forEach {
        item(it.characterName) {
            addClass(ComponentsStyles.contextMenuSectionedItem)
            action {
                onPerspectiveCharacterSelected?.invoke(it)
            }
        }
    }
}

private fun MenuButton.minorCharacters(list: List<AvailablePerspectiveCharacterViewModel>) {
    item("Minor Characters") {
        addClass(ComponentsStyles.contextMenuSectionHeaderItem)
        isDisable = true
    }
    list.filterNot { it.isMajorCharacter }.forEach {
        customitem {
            addClass(ComponentsStyles.contextMenuSectionedItem)
            addClass(ComponentsStyles.discouragedSelection)
            content = label(it.characterName) {
                tooltip {
                    showDelay = Duration.seconds(0.0)
                    hideDelay = Duration.seconds(0.0)
                    style { fontSize = 1.em }
                    text = "${it.characterName} is a minor character in this theme." +
                            "  By selecting this character, they will be promoted" +
                            " to a major character in the theme.  This means they" +
                            " will gain a character arc."
                }
            }
            action {
                onPerspectiveCharacterSelected?.invoke(it)
            }
        }
    }
}