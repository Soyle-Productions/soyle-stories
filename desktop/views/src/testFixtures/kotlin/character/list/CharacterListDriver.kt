package com.soyle.stories.desktop.view.character.list

import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.characterarc.characterList.CharacterArcItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterTreeItemViewModel
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import javafx.scene.control.*
import org.testfx.api.FxRobot
/*
class CharacterListDriver(private val characterList: CharacterListView) : FxRobot() {

    val tree: TreeView<Any?>
        get() = from(characterList.root).lookup(".tree-view").query<TreeView<Any?>>()

    val characterItemContextMenu: ContextMenu?
        get() = tree.contextMenu

    val deleteButton: Button
        get() = from(characterList.root).lookup("#actionBar_delete").queryButton()

    val optionsButton: MenuButton
        get() = from(characterList.root).lookup("#actionBar_options").query()

    fun ContextMenu.getRenameOption() = items.find { it.text == "Rename" }!!

    val MenuButton.createNewMenu
        get() = items.find { it.text == "Create New..." } as Menu
    val MenuButton.createNewNameItem
        get() = createNewMenu.items.find { it.text == "Name" }!!

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

fun CharacterListView.driver() = CharacterListDriver(this)
inline fun <T : Any?> CharacterListView.drive(crossinline road: CharacterListDriver.() -> T): T {
    val driver = CharacterListDriver(this)
    var ret: T? = null
    driver.interact { ret = driver.road() }
    return ret as T
}*/