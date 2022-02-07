package com.soyle.stories.domain.scene.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneEvent

sealed class CharacterMotivationInSceneChanged : SceneEvent() {
    abstract val characterId: Character.Id
    abstract val newMotivation: String?
}

data class CharacterGainedMotivationInScene(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id,
    override val newMotivation: String
) : CharacterMotivationInSceneChanged()

data class CharacterMotivationInSceneCleared(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id
) : CharacterMotivationInSceneChanged() {
    override val newMotivation: String?
        get() = null
}