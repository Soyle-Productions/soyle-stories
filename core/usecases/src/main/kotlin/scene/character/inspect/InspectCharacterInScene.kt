package com.soyle.stories.usecase.scene.character.inspect

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

interface InspectCharacterInScene {

    suspend operator fun invoke(sceneId: Scene.Id, characterId: Character.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receiveCharacterInSceneInspection(inspection: Result<CharacterInSceneInspection>)
    }

}