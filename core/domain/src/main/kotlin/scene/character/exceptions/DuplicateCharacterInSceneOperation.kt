package com.soyle.stories.domain.scene.character.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.RoleInScene
import com.soyle.stories.domain.validation.DuplicateOperationException
import java.util.*

data class DuplicateCharacterInSceneOperation(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id,
    override val message: String
) : DuplicateOperationException(), CharacterInSceneException

fun CharacterAlreadyIncludedInScene(sceneId: Scene.Id, characterId: Character.Id) =
    DuplicateCharacterInSceneOperation(sceneId, characterId, "$characterId has already been included in $sceneId")

fun CharacterAlreadyHasRoleInScene(sceneId: Scene.Id, characterId: Character.Id, role: RoleInScene) =
    DuplicateCharacterInSceneOperation(sceneId, characterId, "$characterId in $sceneId already has role \"$role\"")


fun CharacterAlreadyDoesNotHaveRoleInScene(sceneId: Scene.Id, characterId: Character.Id) =
    DuplicateCharacterInSceneOperation(sceneId, characterId, "$characterId in $sceneId already does not have role")

internal fun CharacterInSceneAlreadyHasDesire(sceneId: Scene.Id, characterId: Character.Id, desire: String) =
    DuplicateCharacterInSceneOperation(sceneId, characterId, "$characterId in $sceneId already has desire \"$desire\"")

fun CharacterInSceneAlreadyHasMotivation(sceneId: Scene.Id, characterId: Character.Id, motivation: String) =
    DuplicateCharacterInSceneOperation(sceneId, characterId, "$characterId in $sceneId already has motivation \"$motivation\"")

fun CharacterInSceneAlreadyDoesNotHaveMotivation(sceneId: Scene.Id, characterId: Character.Id) =
    DuplicateCharacterInSceneOperation(sceneId, characterId, "$characterId in $sceneId already does not have motivation")