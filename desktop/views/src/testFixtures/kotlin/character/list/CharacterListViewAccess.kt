package com.soyle.stories.desktop.view.character.list

import com.soyle.stories.character.list.CharacterListState
import com.soyle.stories.character.list.CharacterListView
import com.soyle.stories.characterarc.characterList.CharacterArcItemViewModel
import com.soyle.stories.characterarc.characterList.CharacterListItemViewModel
import com.soyle.stories.di.get
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.theme.addSymbolicItemToOpposition.CharacterId
import javafx.event.EventTarget
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Region
import org.testfx.api.FxRobot
import tornadofx.getChildList

class CharacterListViewAccess(private val view: CharacterListView) : FxRobot() {

    companion object {
        fun CharacterListView.access() = CharacterListViewAccess(this)
        fun <T> CharacterListView.drive(access: CharacterListViewAccess.() -> T): T {
            var result: T? = null
            val accessor = access()
            accessor.interact { result = accessor.access() }
            return result as T
        }
    }

    val loader: Control?
        get() = from(view.root).lookup("#loader").queryAll<Control>().firstOrNull()

    val inviteImage: ImageView?
        get() = from(view.root).lookup("#inviteImage").queryAll<ImageView>().firstOrNull()

    val createCharacterButton: Button?
        get() = from(view.root).lookup("#create_character_button").queryAll<Button>().firstOrNull()

    val optionsButton: MenuButton?
        get() = from(view.root).lookup("Options").queryAll<MenuButton>().firstOrNull()

    val MenuButton.renameOption: MenuItem?
        get() = items.find { it.id == "rename" }

    private val MenuButton.createNewMenu: Menu?
        get() = items.find { it.id == "create_new" } as? Menu

    val MenuButton.createNewNameOption: MenuItem?
        get() = createNewMenu?.items?.find { it.id == "create_new_name" }

    val MenuButton.deleteOption: MenuItem?
        get() = items.find { it.id == "delete" }

    val characterItemLayout: Region?
        get() = from(view.root).lookup("#character_item_layout").queryAll<Region>().firstOrNull()

    fun Region.selectItem(characterListItemViewModel: CharacterListItemViewModel)
    {
        view.scope.get<CharacterListState>().selectedCharacterItem.set(characterListItemViewModel)
    }

    private val tree: TreeView<Any?>?
        get() = characterItemLayout as? TreeView<Any?>

    private val grid: FlowPane?
        get() = characterItemLayout as? FlowPane

    fun getCharacterItemOrError(characterId: Character.Id) = getCharacterItem(characterId) ?: error("Character not listed $characterId")
    fun getCharacterItem(characterId: Character.Id): CharacterListItemViewModel? {
        val tree = tree
        if (tree != null) {
            return tree.root.children.asSequence().mapNotNull {
                it.value as? CharacterListItemViewModel
            }.find { it.item.characterId == characterId.uuid.toString() }
        }
        val grid = grid
        if (grid != null) {
            return grid.children.asSequence().mapNotNull {
                it.userData as? CharacterListItemViewModel
            }.find { it.item.characterId == characterId.uuid.toString() }
        }
        return null
    }

    fun getArcItem(characterId: Character.Id, themeId: Theme.Id): CharacterArcItemViewModel?
    {
        val tree = tree
        if (tree != null) {
            return tree.root.children.asSequence().find {
                val value = it.value as? CharacterListItemViewModel
                value?.item?.characterId == characterId.uuid.toString()
            }?.children?.asSequence()?.mapNotNull {
                it.value as? CharacterArcItemViewModel
            }?.find { it.themeId == themeId.uuid.toString() }
        }
        val grid = grid
        if (grid != null) {
            return grid.children.asSequence().find {
                val value = it.userData as? CharacterListItemViewModel
                value?.item?.characterId == characterId.uuid.toString()
            }?.getChildList()?.asSequence()?.mapNotNull {
                it.userData as? CharacterArcItemViewModel
            }?.find { it.themeId == themeId.uuid.toString() }
        }
        return null
    }

}