package com.soyle.stories.scene.sceneDetails.includedCharacters

import com.soyle.stories.character.buildNewCharacter.CreatedCharacterReceiver
import com.soyle.stories.character.removeCharacterFromStory.RemovedCharacterReceiver
import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.scene.includeCharacterInScene.IncludedCharacterInSceneReceiver
import com.soyle.stories.scene.sceneDetails.includedCharacter.IncludedCharacterInSceneViewModel
import com.soyle.stories.scene.sceneDetails.includedCharacter.PreviousMotivation
import com.soyle.stories.scene.usecases.common.IncludedCharacterInScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.AvailableCharactersToAddToScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.GetAvailableCharactersToAddToScene
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromScene

class IncludedCharactersInScenePresenter(
    private val sceneId: String,
    private val view: View.Nullable<IncludedCharactersInSceneViewModel>
) : GetAvailableCharactersToAddToScene.OutputPort, IncludedCharacterInSceneReceiver,
    RemoveCharacterFromScene.OutputPort, CreatedCharacterReceiver, RemovedCharacterReceiver {

    override suspend fun receiveAvailableCharactersToAddToScene(response: AvailableCharactersToAddToScene) {
        if (response.sceneId.toString() != sceneId) return
        view.updateOrInvalidated {
            copy(
                availableCharactersToAdd = response.map {
                    CharacterItemViewModel(
                        it.characterId.toString(),
                        it.characterName,
                        it.mediaId?.toString() ?: ""
                    )
                }
            )
        }
    }

    override suspend fun receiveIncludedCharacterInScene(includedCharacterInScene: IncludedCharacterInScene) {
        if (includedCharacterInScene.sceneId.toString() != sceneId) return
        val includedCharacterViewModel = IncludedCharacterInSceneViewModel(
            includedCharacterInScene.characterId.toString(),
            includedCharacterInScene.characterName,
            includedCharacterInScene.motivation ?: "",
            includedCharacterInScene.motivation != null,
            includedCharacterInScene.inheritedMotivation?.let {
                PreviousMotivation(it.motivation, it.sceneId.toString())
            },
            listOf(),
            null
        )
        view.updateOrInvalidated {
            if (includedCharactersInScene.any { it.characterId == includedCharacterViewModel.characterId }) {
                return@updateOrInvalidated this
            }
            copy(
                includedCharactersInScene = includedCharactersInScene + includedCharacterViewModel,
                availableCharactersToAdd = null
            )
        }
    }

    override fun characterRemovedFromScene(response: RemoveCharacterFromScene.ResponseModel) {
        if (response.sceneId.toString() != sceneId) return
        val characterId = response.characterId.toString()
        view.updateOrInvalidated {
            copy(
                includedCharactersInScene = includedCharactersInScene
                    .filterNot { it.characterId == characterId }
            )
        }
    }

    override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
        view.updateOrInvalidated {
            copy(
                availableCharactersToAdd = null
            )
        }
    }

    override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
        view.updateOrInvalidated {
            copy(
                availableCharactersToAdd = null
            )
        }
    }

    override fun failedToRemoveCharacterFromScene(failure: Exception) {}

}