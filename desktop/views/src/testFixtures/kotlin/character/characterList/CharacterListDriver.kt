package com.soyle.stories.desktop.view.character.characterList

import com.soyle.stories.characterarc.characterList.CharacterArcItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterList
import com.soyle.stories.characterarc.characterList.CharacterTreeItemViewModel
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import org.testfx.api.FxRobot

class CharacterListDriver(private val characterList: CharacterList) : FxRobot() {

    val tree: TreeView<Any?>
        get() = from(characterList.root).lookup(".tree-view").query<TreeView<Any?>>()

    val characterItemContextMenu: ContextMenu?
        get() = tree.contextMenu

    val deleteButton: Button
        get() = from(characterList.root).lookup("#actionBar_delete").queryButton()

    fun ContextMenu.getRenameOption() = items.find { it.text == "Rename" }!!

    fun getCharacterItemOrError(characterId: Character.Id): TreeItem<CharacterTreeItemViewModel?> =
        getCharacterItem(characterId) ?: error("No item in character list with id $characterId")

    fun getCharacterItem(characterId: Character.Id): TreeItem<CharacterTreeItemViewModel?>?
    {
        return tree.root.children.asSequence().mapNotNull {
            val value = it.value as? CharacterTreeItemViewModel
            if (value?.id == characterId.uuid.toString()) it as TreeItem<Any?>
            else null
        }.firstOrNull() as? TreeItem<CharacterTreeItemViewModel?>
    }

    fun getArcItem(characterId: Character.Id, themeId: Theme.Id): TreeItem<CharacterArcItemViewModel>?
    {
        return getCharacterItem(characterId)
            ?.children?.find { (it.value as CharacterArcItemViewModel).themeId == themeId.uuid.toString() }
                as? TreeItem<CharacterArcItemViewModel>
    }

}

fun CharacterList.driver() = CharacterListDriver(this)
inline fun CharacterList.drive(crossinline road: CharacterListDriver.() -> Unit) {
    val driver = CharacterListDriver(this)
    driver.interact { driver.road() }
}