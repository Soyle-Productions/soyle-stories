package com.soyle.stories.usecase.scene.common

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterGainedMotivationInScene
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneChanged
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene

data class InheritedMotivation(
    val sceneId: Scene.Id,
    val character: Character.Id,
    val sceneName: String,
    val motivation: String
) {

    fun withEventApplied(event: SceneRenamed): InheritedMotivation {
        if (event.sceneId != sceneId) return this
        return copy(sceneName = event.sceneName)
    }

    fun withEventApplied(event: CharacterGainedMotivationInScene): InheritedMotivation {
        if (event.sceneId != sceneId || event.characterId != character) return this
        return copy(motivation = event.newMotivation)
    }

}