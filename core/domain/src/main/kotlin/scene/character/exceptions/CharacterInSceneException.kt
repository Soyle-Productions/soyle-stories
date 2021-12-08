package com.soyle.stories.domain.scene.character.exceptions

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.SceneException

interface CharacterInSceneException : SceneException {
    val characterId: Character.Id
}