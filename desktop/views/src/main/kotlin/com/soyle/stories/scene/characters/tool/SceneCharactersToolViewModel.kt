package com.soyle.stories.scene.characters.tool

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.doNothing
import com.soyle.stories.common.scopedListener
import com.soyle.stories.di.DI.get
import com.soyle.stories.di.DI.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.scene.FocusedSceneQueries
import com.soyle.stories.scene.characters.inspect.CharacterInSceneInspectionViewModel
import com.soyle.stories.scene.characters.list.CharactersInSceneViewModel
import com.soyle.stories.scene.characters.list.item.CharacterInSceneItemViewModel
import com.soyle.stories.scene.charactersInScene.inspect.InspectCharacterInSceneController
import com.soyle.stories.scene.charactersInScene.listCharactersInScene.ListCharactersInSceneController
import com.soyle.stories.usecase.scene.character.inspect.CharacterInSceneInspection
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneItem
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.withContext
import tornadofx.*

class SceneCharactersToolViewModel(
    sceneSelection: ObjectProperty<SceneSelection>,
    characterFocus: ObjectProperty<CharacterFocus?>,
    private val scope: Scope? = null
) {

    private val _sceneSelection = sceneSelection
    val sceneSelection: ObservableValue<SceneSelection> = _sceneSelection

    private val _characterFocus = characterFocus
    val focusedCharacter: ObservableValue<CharacterInSceneInspectionViewModel?> = _characterFocus.objectBinding {
        it?.viewModel
    }

    sealed class SceneSelection {
        object None : SceneSelection()
        sealed interface Selected {
            val sceneId: Scene.Id
            val sceneName: ObservableValue<String>
        }

        class Loading(
            override val sceneId: Scene.Id,
            override val sceneName: ObservableValue<String> = stringProperty()
        ) : SceneSelection(), Selected

        class Loaded(val viewModel: CharactersInSceneViewModel) : SceneSelection(), Selected {
            override val sceneId: Scene.Id
                get() = viewModel.sceneId
            override val sceneName: ObservableValue<String>
                get() = viewModel.name()
        }
    }

    inner class CharacterFocus(
        private val itemProperty: ObjectProperty<CharacterInSceneItem>,
        private val inspectionProperty: ObjectProperty<CharacterInSceneInspection?>,
        private val isLoading: BooleanProperty = booleanProperty(true),
        onNavigateToPreviousScene: (Scene.Id) -> Unit,
    ) {

        val viewModel: CharacterInSceneInspectionViewModel = CharacterInSceneInspectionViewModel(
            CharacterInSceneItemViewModel(itemProperty, scope), inspectionProperty, isLoading, onNavigateToPreviousScene, scope
        )

        private val presenter = if (scope != null) object {

            private val mainContext = scope.get<ThreadTransformer>().guiContext
            private val inspectCharacter = scope.get<InspectCharacterInSceneController>()::inspectCharacter

            fun loadFocusedCharacter(sceneId: Scene.Id, characterId: Character.Id) {
                inspectCharacter.invoke(sceneId, characterId) { result ->
                    withContext(mainContext) {
                        val inspection = result.getOrThrow()
                        itemProperty.set(inspection.characterItem)
                        inspectionProperty.set(inspection)
                        isLoading.set(false)
                    }
                }
            }

        } else null

        fun loadFocusedCharacter(sceneId: Scene.Id, characterId: Character.Id) {
            isLoading.set(true)
            presenter?.loadFocusedCharacter(sceneId, characterId)
        }

        fun beginLoadingProcess() {
            isLoading.set(true)
        }
    }

    private val presenter = scope?.let { Presenter(it) }

    val onOpenSceneList: () -> Unit = presenter?.let { it::openSceneList } ?: ::doNothing
    val onCloseEditor: () -> Unit = { _characterFocus.set(null) }

    private inner class Presenter(private val scope: Scope) {

        private val mainContext = scope.get<ThreadTransformer>().guiContext

        private val focusedSceneQueries: FocusedSceneQueries = scope.get()

        private val listCharacters = scope.get<ListCharactersInSceneController>()::getCharactersInScene

        private fun loadScene(sceneId: Scene.Id) {
            _sceneSelection.set(SceneSelection.Loading(sceneId))
            _characterFocus.value?.beginLoadingProcess()
            listCharacters(sceneId) { charactersInScene ->
                withContext(mainContext) {
                    val loaded = SceneSelection.Loaded(
                        CharactersInSceneViewModel(
                            objectProperty(charactersInScene),
                            onEditCharacter = { loadFocusedCharacter(charactersInScene.sceneId, it.item) },
                            scope
                        )
                    )
                    _sceneSelection.set(loaded)
                    reloadFocusedCharacter(_characterFocus.value, loaded.viewModel)
                }
            }
        }

        private fun reloadFocusedCharacter(
            currentFocus: CharacterFocus?,
            charactersInScene: CharactersInSceneViewModel
        ) {
            when (currentFocus) {
                is CharacterFocus -> {
                    val correspondingItem =
                        charactersInScene.characters.find { it.character == currentFocus.viewModel.character }
                    if (correspondingItem != null) {
                        currentFocus.loadFocusedCharacter(
                            charactersInScene.sceneId,
                            correspondingItem.character
                        )
                    } else {
                        _characterFocus.set(null)
                    }
                }
                else -> doNothing()
            }
        }

        private fun loadFocusedCharacter(
            sceneId: Scene.Id,
            item: CharacterInSceneItem
        ) {
            val focused = CharacterFocus(
                objectProperty(item),
                objectProperty(null),
                onNavigateToPreviousScene = ::loadScene
            )
            _characterFocus.set(focused)
            focused.loadFocusedCharacter(sceneId, item.characterId)
        }

        init {
            scopedListener(focusedSceneQueries.focusedScene()) {
                if (it == null) {
                    _sceneSelection.set(SceneSelection.None)
                    _characterFocus.set(null)
                } else loadScene(it)
            }
        }

        fun openSceneList() {
            resolve<OpenToolController>().scene.openSceneList()
        }

    }


}