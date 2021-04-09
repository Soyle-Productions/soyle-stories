package com.soyle.stories.scene.charactersInScene.setDesire

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInScene
import kotlinx.coroutines.Job

class SetCharacterDesireInSceneController(
    private val threadTransformer: ThreadTransformer,
    private val setCharacterDesireInScene: SetCharacterDesireInScene,
    private val setCharacterDesireInSceneOutput: SetCharacterDesireInScene.OutputPort
) {

    fun setDesire(sceneId: Scene.Id, characterId: Character.Id, desire: String): Job
    {
        val request = SetCharacterDesireInScene.RequestModel(sceneId, characterId, desire)
        return threadTransformer.async {
            setCharacterDesireInScene(request, setCharacterDesireInSceneOutput)
        }
    }

}