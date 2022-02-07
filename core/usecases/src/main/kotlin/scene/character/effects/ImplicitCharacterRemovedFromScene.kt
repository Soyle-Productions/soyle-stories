package com.soyle.stories.usecase.scene.character.effects

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

data class ImplicitCharacterRemovedFromScene(
    override val scene: Scene.Id,
    override val sceneName: String,
    override val character: Character.Id,
    override val characterName: String?
) : CharacterInSceneEffect