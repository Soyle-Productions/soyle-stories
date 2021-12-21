package com.soyle.stories.domain.scene.character.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.DuplicateOperationException

data class DuplicateCharacterInSceneOperation(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id,
    override val message: String
) : DuplicateOperationException(), CharacterInSceneException

internal fun characterInSceneAlreadyHasDesire(sceneId: Scene.Id, characterId: Character.Id, desire: String) =
    DuplicateCharacterInSceneOperation(sceneId, characterId, "$characterId in $sceneId already has desire \"$desire\"")
