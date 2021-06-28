package com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.GetAvailableCharacterArcsForCharacterInScene
import kotlinx.coroutines.Job

class ListAvailableArcSectionsToCoverInSceneController(
    private val threadTransformer: ThreadTransformer,
    private val getAvailableCharacterArcsForCharacterInScene: GetAvailableCharacterArcsForCharacterInScene
) {

    fun listAvailableSectionsToCoverForCharacterInScene(
        characterId: Character.Id,
        sceneId: Scene.Id,
        output: GetAvailableCharacterArcsForCharacterInScene.OutputPort
    ): Job {
        return threadTransformer.async {
            getAvailableCharacterArcsForCharacterInScene.invoke(sceneId.uuid, characterId.uuid, output)
        }
    }

}