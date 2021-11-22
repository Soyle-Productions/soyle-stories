package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.common.ProjectScopedModel
import com.soyle.stories.di.get
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListModel
import com.soyle.stories.scene.target.SceneTargeted
import com.soyle.stories.scene.target.SceneTargetedNotifier
import com.soyle.stories.scene.target.SceneTargetedReceiver
import javafx.application.Platform
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.StringBinding
import javafx.beans.property.*
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import tornadofx.*
import java.util.*

class SceneCharactersState : ProjectScopedModel<SceneCharactersViewModel>() {

    val selectedSceneId: ReadOnlyObjectProperty<Scene.Id?> = SimpleObjectProperty<Scene.Id?>(null)
    val selectedSceneName: ReadOnlyObjectProperty<String?> = SimpleObjectProperty<String?>("No Scene Selected")
    val isSceneSelected = selectedSceneId.isNotNull
    val selectedCharacter = SimpleObjectProperty<IncludedCharacterViewModel?>(null)
    val characterBeingEdited = SimpleObjectProperty<IncludedCharacterViewModel?>(null)

    val availableCharacters = bind(SceneCharactersViewModel::availableCharacters)

    val includedCharacters = bind(SceneCharactersViewModel::includedCharacters)
    val hasIncludedCharacters = includedCharacters.booleanBinding { ! it.isNullOrEmpty() }
    val firstCharacter = bind { it?.includedCharacters?.firstOrNull() }
    val incitingCharacter = bind { it?.includedCharacters?.find { it.roleInScene == CharacterRoleInScene.IncitingCharacter } }

    val sceneCharactersTitleText: ReadOnlyStringProperty = SimpleStringProperty("Track Characters in Scene")
    val addCharacterText: ReadOnlyStringProperty = SimpleStringProperty("Add Character")
    val characterOptionsText: ReadOnlyStringProperty = SimpleStringProperty("Options")

    fun toggleIncitingCharacterText(characterItem: IncludedCharacterViewModel?): ObservableStringValue
    {
        return SimpleStringProperty(
            when (characterItem?.roleInScene) {
                CharacterRoleInScene.IncitingCharacter -> "${characterItem.name} is not the Inciting Character"
                null -> "Make the Inciting Character"
                else -> "Make ${characterItem.name} the Inciting Character"
            }
        )
    }

    fun toggleOpponentCharacterText(characterItem: IncludedCharacterViewModel?): ObservableStringValue
    {
        return when (characterItem?.roleInScene) {
            CharacterRoleInScene.OpponentToIncitingCharacter -> incitingCharacter.stringBinding {
                when (it) {
                    null -> "${characterItem.name} is not an Opponent to the Inciting Character"
                    else -> "${characterItem.name} is not an Opponent to ${it.name}"
                }
            }
            else -> incitingCharacter.stringBinding {
                val characterItemName = characterItem?.name ?: ""
                when (it) {
                    null -> "$characterItemName Opposes the Inciting Character"
                    else -> "$characterItemName Opposes ${it.name}"
                }
            }
        }
    }

    fun selectScene(sceneId: Scene.Id, sceneName: String) {
        (selectedSceneId as ObjectProperty).set(sceneId)
        (selectedSceneName as ObjectProperty).set(sceneName)
    }

    private val characterBeingEditedUpdater = bind {
        val editingCharacterId = characterBeingEdited.value?.id ?: return@bind
        characterBeingEdited.value = it?.includedCharacters?.find { it.id == editingCharacterId }
    }

    override fun viewModel(): SceneCharactersViewModel? {
        return super.viewModel()?.copy(targetSceneId = selectedSceneId.value)
    }

    private val guiEventListener = object :
        SceneTargetedReceiver
    {
        override suspend fun receiveSceneTargeted(event: SceneTargeted) {
            if (! Platform.isFxApplicationThread()) {
                withContext(Dispatchers.JavaFx) {
                    selectScene(event.sceneId, event.sceneName)
                }
            } else {
                selectScene(event.sceneId, event.sceneName)
            }
        }
    }

    init {
        scope.get<SceneTargetedNotifier>().addListener(guiEventListener)
        (FX.getComponents(scope)[SceneListModel::class] as? SceneListModel)?.selectedItem?.value?.let {
            selectScene(it.id, it.name)
        }
    }

}