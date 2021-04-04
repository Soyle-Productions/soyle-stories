package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.common.existsWhen
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.sceneCharacters.characterEditor.SelectedSceneCharacterEditor
import com.soyle.stories.scene.sceneCharacters.includedCharacterItem.IncludedCharacterItemView
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.control.Hyperlink
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import tornadofx.*

class SceneCharactersViewLogic(private val view: SceneCharactersView, private val state: SceneCharactersState) {

    private val viewListener = view.resolve<SceneCharactersViewListener>()

    private fun loadScene(sceneId: Scene.Id?)
    {
        if (sceneId == null) return
        viewListener.getCharactersInScene(sceneId)
    }

    init {
        state.selectedSceneId.onChange { loadScene(it) }
        loadScene(state.selectedSceneId.value)
    }

    internal fun selectedSceneHeader(parent: Parent)
    {
        parent.existsWhen(state.isSceneSelected)
    }

    internal fun sceneListHyperLink(hyperlink: Hyperlink) {
        hyperlink.action {  }
    }

    internal fun addCharacterButton(menuButton: MenuButton) {
        menuButton.setOnShowing {
            state.availableCharacters.set(null)
            viewListener.getAvailableCharacters()
        }
    }

    internal fun createCharacterOption(menuItem: MenuItem) {
        menuItem.action {
            createCharacterDialog(view.scope) {
                viewListener.addCharacter(Character.Id(it.characterId))
            }
        }
    }

    internal fun availableCharacterOption(menuItem: MenuItem)
    {
        menuItem.action {
            viewListener.addCharacter(menuItem.userData as Character.Id)
        }
    }

    internal fun characterOptionsButton(menuButton: MenuButton) {
        menuButton.enableWhen(state.selectedCharacter.isNotNull)
    }

    internal fun incitingCharacterToggle(menuItem: MenuItem) {
        menuItem.action {
        }
    }

    internal fun opponentCharacterToggle(menuItem: MenuItem) {

    }

    internal fun editCharacterOption(menuItem: MenuItem)
    {
        menuItem.action {
            state.selectedCharacter.value?.let {
                state.characterBeingEdited.set(it)
            }
        }
    }

    internal fun removeCharacterOption(menuItem: MenuItem)
    {
        menuItem.action {
            state.selectedCharacter.value?.let {
                viewListener.removeCharacter(it.id)
            }
        }
    }

    internal fun listedCharacterItem(itemContainer: Parent) = fun(listedItem: IncludedCharacterItemView) {
        listedItem.action.bind(listedItem.props.itemProperty.objectBinding { includedCharacter ->
            if (includedCharacter == null) return@objectBinding null
            EventHandler {
                val itemBounds = listedItem.root.localToScene(listedItem.root.boundsInLocal)
                val containerBounds = itemContainer.localToScene(view.root.boundsInLocal)
                val deltaY = itemBounds.minY - (containerBounds.minY)
                find<SelectedSceneCharacterEditor>(view.scope).root.translateY = deltaY
                state.characterBeingEdited.set(includedCharacter)
            }
        })
    }

}