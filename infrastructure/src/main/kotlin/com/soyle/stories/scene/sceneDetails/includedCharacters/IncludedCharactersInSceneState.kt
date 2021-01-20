package com.soyle.stories.scene.sceneDetails.includedCharacters

import com.soyle.stories.common.Model
import com.soyle.stories.common.mapObservable
import com.soyle.stories.di.get
import com.soyle.stories.scene.sceneDetails.SceneDetailsModel
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import com.soyle.stories.scene.sceneDetails.includedCharacter.IncludedCharacterInSceneState
import com.soyle.stories.scene.sceneDetails.includedCharacter.IncludedCharacterScope
import com.soyle.stories.soylestories.ApplicationScope
import javafx.collections.ObservableList
import tornadofx.onChangeOnce

class IncludedCharactersInSceneState : Model<SceneDetailsScope, IncludedCharactersInSceneViewModel>(SceneDetailsScope::class) {

    override val applicationScope: ApplicationScope
        get() = scope.projectScope.applicationScope

    val title = bind(IncludedCharactersInSceneViewModel::title)
    val storyEventId = bind(IncludedCharactersInSceneViewModel::storyEventId)
    val addCharacterLabel = bind(IncludedCharactersInSceneViewModel::addCharacterLabel)
    val removeCharacterLabel = bind(IncludedCharactersInSceneViewModel::removeCharacterLabel)
    val positionOnCharacterArcLabel = bind(IncludedCharactersInSceneViewModel::positionOnCharacterArcLabel)
    val motivationFieldLabel = bind(IncludedCharactersInSceneViewModel::motivationFieldLabel)
    val resetMotivationLabel = bind(IncludedCharactersInSceneViewModel::resetMotivationLabel)
    val motivationLastChangedLabel = bind(IncludedCharactersInSceneViewModel::motivationLastChangedLabel)

    val availableCharacters = bind(IncludedCharactersInSceneViewModel::availableCharactersToAdd)

    val includedCharacters = bind(IncludedCharactersInSceneViewModel::includedCharactersInScene)

    val includedCharacterScopes: ObservableList<IncludedCharacterScope> = includedCharacters
        .mapObservable(keyGenerator = { it.characterId }) {
            IncludedCharacterScope(scope.sceneId.toString(), item!!.storyEventId, it, scope)
        }.apply {
            onRemoved { if (! it.isClosed) it.close() }
        }

    override fun viewModel(): IncludedCharactersInSceneViewModel? {
        return item?.copy(
            includedCharactersInScene = includedCharacterScopes.mapNotNull {
                val state = it.get<IncludedCharacterInSceneState>()
                state.viewModel()
            }
        )
    }

    init {
        scope.get<SceneDetailsModel>().includedCharactersInScene.onChangeOnce {
            println("includedCharactersInScene changed $it")
            item = it
        }
    }

}