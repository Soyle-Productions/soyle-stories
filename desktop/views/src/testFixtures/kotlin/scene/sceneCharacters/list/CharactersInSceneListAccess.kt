package com.soyle.stories.desktop.view.scene.sceneCharacters.list

import com.soyle.stories.common.ViewOf
import com.soyle.stories.common.cells
import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.desktop.view.common.asViewOf
import com.soyle.stories.desktop.view.common.maybeViewOf
import com.soyle.stories.domain.character.Character
import com.soyle.stories.scene.characters.list.CharactersInSceneViewModel
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemStyles
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemViewModel
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import tornadofx.Stylesheet

class CharactersInSceneListAccess (private val view: ViewOf<CharactersInSceneViewModel>) : NodeAccess<Node>(view as Node) {

    private val itemList by temporaryChild<ListView<CharacterInSceneItemViewModel>>(Stylesheet.listView)

    fun getCharacterItem(characterId: Character.Id): CharacterInSceneItemViewAccess? {
        return itemList?.findChild<ListCell<CharacterInSceneItemViewModel>>(Stylesheet.listCell) {
            it.graphic?.maybeViewOf<CharacterInSceneItemViewModel>()?.viewModel?.character == characterId
        }?.graphic?.asViewOf<CharacterInSceneItemViewModel>()?.access()
    }

    fun getCharacterItemByName(characterName: String): CharacterInSceneItemViewAccess? {
        return itemList?.findChild<Parent>(CharacterInSceneItemStyles.characterInSceneItem) {
            it.maybeViewOf<CharacterInSceneItemViewModel>()?.viewModel?.name == characterName
        }?.asViewOf<CharacterInSceneItemViewModel>()?.access()
    }

}

fun ViewOf<CharactersInSceneViewModel>.access() = CharactersInSceneListAccess(this)