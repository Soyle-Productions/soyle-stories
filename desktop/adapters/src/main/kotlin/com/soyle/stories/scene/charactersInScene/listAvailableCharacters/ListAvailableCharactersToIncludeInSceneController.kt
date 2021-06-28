package com.soyle.stories.scene.charactersInScene.listAvailableCharacters

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.listAvailableCharacters.ListAvailableCharactersToIncludeInScene
import kotlinx.coroutines.Job

class ListAvailableCharactersToIncludeInSceneController(
    private val threadTransformer: ThreadTransformer,
    private val listAvailableCharactersToIncludeInScene: ListAvailableCharactersToIncludeInScene
) {

    fun listAvailableCharacters(sceneId: Scene.Id, output: ListAvailableCharactersToIncludeInScene.OutputPort): Job
    {
        return threadTransformer.async {
            listAvailableCharactersToIncludeInScene.invoke(sceneId.uuid, output)
        }
    }

}