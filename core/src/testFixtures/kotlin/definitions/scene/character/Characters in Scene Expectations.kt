package com.soyle.stories.core.definitions.scene.character

import com.soyle.stories.core.framework.scene.`Scene Character Steps`
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.usecase.scene.SceneRepository
import kotlinx.coroutines.runBlocking

class `Characters in Scene Expectations`(
    private val sceneRepository: SceneRepository,

    private val `when`: `Scene Character Steps`.When
) : `Scene Character Steps`.Given {
    override fun characterInScene(
        sceneId: Scene.Id,
        characterId: Character.Id
    ): `Scene Character Steps`.Given.CharacterInSceneExpectations = object :
        `Scene Character Steps`.Given.CharacterInSceneExpectations {
        override fun `has been assigned to be the`(role: RoleInScene) {
            val scene = runBlocking { sceneRepository.getSceneOrError(sceneId.uuid) }
            val character = scene.includedCharacters[characterId]
            if (character?.roleInScene != role) {
                `when`.characterInScene(sceneId, characterId).`is assigned to be the`(role)
            }
        }

        override fun `has been motivated by`(motivation: String) {
            val scene = runBlocking { sceneRepository.getSceneOrError(sceneId.uuid) }
            val character = scene.includedCharacters[characterId]
            if (character?.motivation != motivation) {
                `when`.characterInScene(sceneId, characterId).`is motivated by`(motivation)
            }
        }
    }

    override fun characterIncludedInScene(scene: Scene.Id, character: Character.Id) {
        if (runBlocking { sceneRepository.getSceneOrError(scene.uuid) }.includesCharacter(character)) return
        `when`.includeCharacterInScene(scene, character)
    }

    override fun characterRemovedFromScene(scene: Scene.Id, character: Character.Id) {
        if (! runBlocking { sceneRepository.getSceneOrError(scene.uuid) }.includesCharacter(character)) return
        `when`.removeCharacterFromScene(scene, character)
    }
}