package com.soyle.stories.usecase.scene.character.involve

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.list.CharacterInSceneSourceItem

data class SourceAddedToCharacterInScene(
    val scene: Scene.Id,
    val character: Character.Id,
    val source: CharacterInSceneSourceItem
)