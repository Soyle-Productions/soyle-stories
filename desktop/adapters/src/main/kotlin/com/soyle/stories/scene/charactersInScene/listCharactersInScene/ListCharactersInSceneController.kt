package com.soyle.stories.scene.charactersInScene.listCharactersInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.listIncluded.ListCharactersInScene

import kotlinx.coroutines.Job

class ListCharactersInSceneController(
    private val threadTransformer: ThreadTransformer,
    private val listCharactersInScene: ListCharactersInScene
) {

    fun listCharactersInScene(sceneId: Scene.Id, output: ListCharactersInScene.OutputPort): Job
    {
        return threadTransformer.async {
            listCharactersInScene.invoke(sceneId, output)
        }
    }

}