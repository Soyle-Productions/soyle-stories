package com.soyle.stories.theme.characterConflict.components

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.theme.characterConflict.AvailableOpponentViewModel
import com.soyle.stories.theme.characterConflict.CharacterConflictModel
import javafx.collections.ObservableList
import javafx.scene.control.MenuButton
import javafx.util.Duration
import tornadofx.*

internal fun MenuButton.populateOpponentList(model: CharacterConflictModel)
{
    setOnHidden { model.availableOpponents.clear() }
    model.availableOpponents.onChange { list: ObservableList<AvailableOpponentViewModel>? ->
        items.clear()
        when {
            list == null -> loading()
            list.isEmpty() -> noAvailableCharacters()
            else -> {
                charactersInTheme(list)
                otherCharactersInStory(list)
            }
        }
    }
    loading()
}

internal var MenuButton.onOpponentCharacterSelected: ((AvailableOpponentViewModel) -> Unit)?
    get() = properties.get("com.soyle.stories.onOpponentCharacterSelected") as? (AvailableOpponentViewModel) -> Unit
    set(value) {
        properties["com.soyle.stories.onOpponentCharacterSelected"] = value
    }

private fun MenuButton.charactersInTheme(list: List<AvailableOpponentViewModel>)
{
    item("Characters in Theme") {
        addClass(ComponentsStyles.contextMenuSectionHeaderItem)
        isDisable = true
    }
    list.filter { it.isInTheme }.forEach {
        item(it.characterName) {
            id = it.characterId
            addClass(ComponentsStyles.contextMenuSectionedItem)
            action {
                onOpponentCharacterSelected?.invoke(it)
            }
        }
    }
}

private fun MenuButton.otherCharactersInStory(list: List<AvailableOpponentViewModel>)
{
    item("Other Characters in Story") {
        addClass(ComponentsStyles.contextMenuSectionHeaderItem)
        isDisable = true
    }
    items.add(createCharacterItem())
    list.filterNot { it.isInTheme }.forEach {
        customitem {
            id = it.characterId
            addClass(ComponentsStyles.contextMenuSectionedItem)
            addClass(ComponentsStyles.discouragedSelection)
            content = label(it.characterName) {
                tooltip {
                    showDelay = Duration.seconds(0.0)
                    hideDelay = Duration.seconds(0.0)
                    style { fontSize = 1.em }
                    text =
                        "${it.characterName} is not included in this theme.  By " +
                                "selecting them, they will be included as a Minor Character."
                }
            }
            action {
                onOpponentCharacterSelected?.invoke(it)
            }
        }
    }
}