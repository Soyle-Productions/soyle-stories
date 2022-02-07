package com.soyle.stories.usecase.scene.character.include

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.usecase.scene.common.InheritedMotivation

interface IncludeCharacterInScene {

    suspend operator fun invoke(sceneId: Scene.Id, characterId: Character.Id, output: OutputPort): Result<Unit>

    data class ResponseModel(
        val characterIncludedInScene: CharacterIncludedInScene,
        val characterName: String,
        val inheritedMotivation: InheritedMotivation?
    )

    fun interface OutputPort {
        suspend fun characterIncludedInScene(response: ResponseModel)
    }

}