package com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.SuccessfulSceneUpdate
import com.soyle.stories.domain.scene.character.CharacterInSceneOperations
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneChanged
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class SetMotivationForCharacterInSceneUseCase(
    private val scenes: SceneRepository,
    private val storyEvents: StoryEventRepository,
    private val characters: CharacterRepository
) : SetMotivationForCharacterInScene {

    override suspend fun invoke(
        request: SetMotivationForCharacterInScene.RequestModel,
        output: SetMotivationForCharacterInScene.OutputPort
    ) {
        val scene = sceneMustExist(request.sceneId)
        val sceneWithCharacter = scene.mustIncludeCharacter(request.characterId)
        val update = sceneWithCharacter.setMotivation(request.characterId, request.motivation)
        when (update) {
            is SceneUpdate.UnSuccessful -> update.reason?.let { throw it }
            is SceneUpdate.Successful -> {
                scenes.updateScene(update.scene)
                val response = update.asResponse(sceneWithCharacter)
                output.motivationSetForCharacterInScene(response)
            }
        }
    }

    private suspend fun sceneMustExist(sceneId: Scene.Id): Scene {
        return scenes.getSceneOrError(sceneId.uuid)
    }

    private suspend fun Scene.mustIncludeCharacter(characterId: Character.Id): SceneUpdate<CharacterIncludedInScene> {
        return if (includesCharacter(characterId)) noUpdate()
        else {
            val character = characters.getCharacterOrError(characterId.uuid)
            ensureCharacterImplicitlyIncluded(characterId)
            withCharacterIncluded(character)
        }
    }

    private suspend fun Scene.ensureCharacterImplicitlyIncluded(characterId: Character.Id) {
        storyEvents.getStoryEventsCoveredByScene(id)
            .find { it.involvedCharacters.containsEntityWithId(characterId) }
            ?: throw SceneDoesNotIncludeCharacter(id, characterId)
    }

    private fun SceneUpdate<CharacterIncludedInScene>.setMotivation(
        characterId: Character.Id,
        motivation: String?
    ): SceneUpdate<CharacterMotivationInSceneChanged> {
        return scene.withCharacter(characterId)!!.motivationChanged(motivation)
    }

    private fun SuccessfulSceneUpdate<CharacterMotivationInSceneChanged>.asResponse(
        characterIncludedUpdate: SceneUpdate<CharacterIncludedInScene>?
    ): SetMotivationForCharacterInScene.ResponseModel {
        return SetMotivationForCharacterInScene.ResponseModel(
            change,
            (characterIncludedUpdate as? SceneUpdate.Successful)?.change
        )
    }
}