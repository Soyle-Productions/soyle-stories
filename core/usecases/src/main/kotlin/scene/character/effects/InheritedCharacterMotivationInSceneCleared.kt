package com.soyle.stories.usecase.scene.character.effects

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.common.InheritedMotivation

data class InheritedCharacterMotivationInSceneCleared(
    override val scene: Scene.Id,
    override val sceneName: String,
    override val character: Character.Id,
    override val characterName: String?,
    val inheritedMotivation: InheritedMotivation
) : CharacterInSceneEffect