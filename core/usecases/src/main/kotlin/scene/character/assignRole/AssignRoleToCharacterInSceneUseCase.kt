package com.soyle.stories.usecase.scene.character.assignRole

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterRoleInSceneChanged
import com.soyle.stories.domain.scene.events.CompoundEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class AssignRoleToCharacterInSceneUseCase(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository,
    private val characters: CharacterRepository
) : AssignRoleToCharacterInScene {


    override suspend fun invoke(
        request: AssignRoleToCharacterInScene.RequestModel,
        output: AssignRoleToCharacterInScene.OutputPort
    ) {
        val sceneWithCharacter = getSceneWithCharacter(request.sceneId, request.characterId)
        val changeEvent = updateSceneWithNewRole(sceneWithCharacter.scene, request.characterId, request.role)
        output.roleAssignedToCharacterInScene(
            AssignRoleToCharacterInScene.ResponseModel(
                (sceneWithCharacter as? Successful)?.change,
                changeEvent
            )
        )
    }

    private suspend fun getSceneWithCharacter(
        sceneId: Scene.Id,
        characterId: Character.Id
    ): SceneUpdate<CharacterIncludedInScene> {
        val scene = sceneRepository.getSceneOrError(sceneId.uuid)
        val character = characters.getCharacterOrError(characterId.uuid)
        if (!scene.includesCharacter(characterId)) {
            val coveredStoryEventsWithCharacter = storyEventRepository
                .getStoryEventsCoveredBySceneAndInvolvingCharacter(scene.id, characterId)
            if (coveredStoryEventsWithCharacter.isEmpty()) {
                throw SceneDoesNotIncludeCharacter(scene.id, characterId)
            }
            return scene.withCharacterIncluded(character)
        }
        return scene.noUpdate()
    }

    private suspend fun updateSceneWithNewRole(
        scene: Scene,
        characterId: Character.Id,
        role: RoleInScene?
    ): CompoundEvent<CharacterRoleInSceneChanged>? {
        val characterOps = scene.withCharacter(characterId)
            ?: throw SceneDoesNotIncludeCharacter(scene.id, characterId)

        return when (val sceneUpdate = characterOps.assignedRole(role)) {
            is SceneUpdate.UnSuccessful -> {
                sceneUpdate.reason?.let { throw it }
                null
            }
            is Successful -> {
                commitUpdate(sceneUpdate)
                (sceneUpdate as? Successful)?.event
            }
        }
    }

    private suspend fun commitUpdate(update: Successful<*>) {
        sceneRepository.updateScene(update.scene)
    }
}