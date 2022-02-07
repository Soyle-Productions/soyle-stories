package com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterGainedMotivationInScene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterMotivationInSceneChanged

interface SetMotivationForCharacterInScene {

    class RequestModel(val sceneId: Scene.Id, val characterId: Character.Id, val motivation: String?)

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val characterMotivationInSceneChanged: CharacterMotivationInSceneChanged,
        val characterIncludedInScene: CharacterIncludedInScene?
    )

    fun interface OutputPort {
        suspend fun motivationSetForCharacterInScene(response: ResponseModel)
    }
}