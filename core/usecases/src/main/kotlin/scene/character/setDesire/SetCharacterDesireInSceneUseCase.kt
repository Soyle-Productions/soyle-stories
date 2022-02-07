package com.soyle.stories.usecase.scene.character.setDesire

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class SetCharacterDesireInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val storyEvents: StoryEventRepository,
    private val characters: CharacterRepository
) : SetCharacterDesireInScene {
    override suspend fun invoke(
        request: SetCharacterDesireInScene.RequestModel,
        output: SetCharacterDesireInScene.OutputPort
    ) {
        val sceneWithCharacter = getSceneWithCharacter(request.sceneId, request.characterId)

        val characterOps = sceneWithCharacter.scene.withCharacter(request.characterId)
            ?: throw SceneDoesNotIncludeCharacter(sceneWithCharacter.scene.id, request.characterId)

        val sceneUpdate = characterOps.desireChanged(request.desire)

        if (sceneUpdate is Successful) {
            sceneRepository.updateScene(sceneUpdate.scene)
            output.receiveSetCharacterDesireInSceneResponse(
                SetCharacterDesireInScene.ResponseModel(
                    (sceneWithCharacter as? Successful)?.event,
                    sceneUpdate.event
                )
            )
        }
    }

    private suspend fun getSceneWithCharacter(
        sceneId: Scene.Id,
        characterId: Character.Id
    ): SceneUpdate<CharacterIncludedInScene> {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val character = characters.getCharacterOrError(characterId.uuid)
        if (!scene.includesCharacter(characterId)) {
            val coveredStoryEventsWithCharacter =
                storyEvents.getStoryEventsCoveredBySceneAndInvolvingCharacter(scene.id, characterId)
            if (coveredStoryEventsWithCharacter.isEmpty()) {
                throw SceneDoesNotIncludeCharacter(scene.id, characterId)
            }
            return scene.withCharacterIncluded(character)
        }
        return scene.noUpdate()
    }
}